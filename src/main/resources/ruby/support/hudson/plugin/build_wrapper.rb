
module Hudson
  module Plugin
    class BuildWrapper

      def self.inherited(cls)
        super(cls)
        cls.send(:extend, ClassMethods)
      end


      def setup(build, launcher, lister)
        raise "Clouds must implement can_provision?(label)"
      end

      class Wrapper < Java::HudsonTasks::BuildWrapper
        include Java::Ruby::SimpleGet
        include Java::Ruby::RubyDoDynamic

        def initialize(plugin, object)
          super()
          puts "Hudson::Plugin::BuildWrapper::Wrapper.new(#{plugin}, #{object})"
          @object = object
        end

        def setUp(build, launcher, listener)
          @object.setup(build, launcher, listener)
          Hudson::Plugin::BuildWrapper::EnvironmentWrapper.new(self)
        end

        def getDescriptor
          puts "object.class: #{@object.class} -> #{plugin.descriptors[@object.class]}"
          plugin.descriptors[@object.class]
        end

        def plugin
          Java::Ruby::RubyPlugin.getRubyController()
        end

        def unwrap
          @object
        end

        def get(name)
          @object.respond_to?(name) ? @object.send(name) : nil
        end

        def doDynamic(request, response)
          response.getWriter().println("Hello")
        end

      end


      class EnvironmentWrapper < Java::HudsonTasks::BuildWrapper::Environment

        def initialize(build_wrapper)
          super(build_wrapper)
          @build_wrapper = build_wrapper
        end

        def tearDown(*args)
          @build_wrapper.unwrap.teardown(*args)
        end
      end

      module ClassMethods
        def new(properties)
          allocate.tap do |instance|
            for k,v in properties
              instance.instance_variable_set("@#{k}", v)
            end
            instance.send(:initialize)
            puts "new instance created: #{instance.inspect}"
            $stdout.flush()
          end
        end
      end
    end
  end
end