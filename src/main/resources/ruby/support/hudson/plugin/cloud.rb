
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
        def initialize(plugin, object)
          super(object.name)
          @plugin, @object = plugin, object
        end

        def canProvision(label)
          true if @object.can_provision?(@plugin.import(label))
        end

        def provision(label, excess_workload)
          @plugin.export @object.provision(@plugin.import(label), excess_workload)
        end

        def descriptor
          @plugin.descriptors[Cloud]
        end

        def unwrap
          @object
        end
      end

      module ClassMethods
        def new(properties)
          allocate.tap do |instance|
            for k,v in properties
              instance.instance_variable_set("@#{k}", v)
            end
            instance.send(:initialize)
          end
        end
      end
    end
  end
end