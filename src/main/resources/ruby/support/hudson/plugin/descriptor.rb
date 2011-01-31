
module Hudson
  module Plugin
    class Descriptor < Java::HudsonModel::Descriptor
      def initialize(name, impl, plugin, java_type)
        super(Java::OrgJruby::RubyObject.java_class)
        @name, @impl, @plugin, @java_type = name, impl, plugin, java_type
      end

      def getDisplayName
        @impl.display_name
      end

      def getT()
        @java_type
      end

      def getConfigPage
        "/ruby/plugin/views/#{@name}/config.jelly".tap do |path|
          puts "getGlobalConfigPage -> #{path}"
        end
      end

      def getGlobalConfigPage
        "/ruby/plugin/views/#{@name}/config.jelly".tap do |path|
          puts "getGlobalConfigPage -> #{path}"
        end
      end

      def newInstance(request, form)
        puts "formdata: #{form.toString(2)}"
        @plugin.export @impl.new
      end

    end

    class Cloud

      def can_provision?(label)
        raise "Clouds must implement can_provision?(label)"
      end

      def provision(label, excess_workload)
        []
      end


      class Wrapper < Java::HudsonSlaves::Cloud
        def initialize(plugin, object)
          @plugin, @object = plugin, object
        end

        def canProvision(label)
          true if @object.can_provision?(@plugin.import(label))
        end

        def provision(label, excess_workload)
          @plugin.export @object.provision(@plugin.import(label), excess_workload)
        end
      end
    end


  end
end
