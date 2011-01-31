
module Hudson
  module Plugin

    class Controller

      def initialize(java)
        @java = java
        @start = @stop = proc {}
        @wrappers = Hash.new do |wrappers, object|
          wrappers[object] = object.class::Wrapper.new(object, self)
        end
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

      def import(wrapper)
        wrapper.unwrap
      end

      def export(object)
        @wrappers[object]
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