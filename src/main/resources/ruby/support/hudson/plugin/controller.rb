
module Hudson
  module Plugin

    class Controller
      attr_reader :descriptors

      def initialize(java)
        @java = java
        @start = @stop = proc {}
        @descriptors = {}
        @wrappers = {}
        #commented out right now because we don't actually want to load any ruby
        #It takes too long!
        require 'bundled-gems.jar'
        require 'rubygems'
        DSL.new(self) do |dsl|
          script = @java.read("plugin.rb")
          dsl.instance_eval(script, "plugin.rb")
        end
      end

      def start
        script = 'support/hudson/plugin/models.rb'
        self.instance_eval @java.read(script), script
        @start.call()
      end

      def stop
        @stop.call()
      end

      def import(object)
        object.respond_to?(:unwrap) ? object.unwrap : object
      end

      def export(object)
        return @wrappers[object] if @wrappers[object]
        case object
          when Hudson::Plugin::Cloud then @wrappers[object] = Hudson::Plugin::Cloud::Wrapper.new(self, object)
          else object
        end
      end

      private

      class DSL
        def initialize(controller)
          @controller = controller
          yield self if block_given?
        end

        def start(&impl)
          @controller.instance_variable_set(:@start, impl) if impl
        end

        def stop(&impl)
          @controller.instance_variable_set(:@stop, impl) if impl
        end
      end

    end
  end
end