
module Hudson
  module Plugin
    class Descriptor < Java::HudsonModel::Descriptor
      def initialize(name, impl)
        super(org.jruby.RubyObject.java_class)
        @name, @impl = name, impl
      end

      def getDisplayName
        @impl.display_name
      end

      def getConfigPage
        "/ruby/plugin/views/#{@name}/config.jelly"
      end

      def getGlobalConfigPage
        "/ruby/plugin/views/#{@name}/global.jelly"
      end
    end
  end
end
