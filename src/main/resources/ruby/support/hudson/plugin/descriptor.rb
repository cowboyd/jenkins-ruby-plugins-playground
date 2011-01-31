
module Hudson
  module Plugin
    module Descriptor
      def getDisplayName
        @impl.display_name
      end

      def getConfigPage
        "/ruby/plugin/views/#{@name}/config.jelly"
      end

      def getGlobalConfigPage
        "/ruby/plugin/views/#{@name}/global.jelly"
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
