
module Hudson
  module Plugin
    class Cloud

      def self.inherited(cls)
        super(cls)
        cls.send(:extend, ClassMethods)
      end


      def can_provision?(label)
        raise "Clouds must implement can_provision?(label)"
      end


      def provision(label, excess_workload)
        []
      end

      class Wrapper < Java::HudsonSlaves::Cloud
        include Java::Ruby::SimpleGet
        include Java::Ruby::RubyDoDynamic

        def initialize(plugin, object)
          super(object.name)
          puts "Hudson::Plugin::Cloud::Wrapper.new(#{plugin}, #{object})"
          @plugin, @object = plugin, object
        end

        def canProvision(label)
          true if @object.can_provision?(@plugin.import(label))
        end

        def provision(label, excess_workload)
          @plugin.export @object.provision(@plugin.import(label), excess_workload)
        end

        def getDescriptor
          puts "object.class: #{@object.class} -> #{@plugin.descriptors[@object.class]}"
          @plugin.descriptors[@object.class]
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