
module Hudson
  module Plugin
    class RootAction

      def self.inherited(cls)
        super(cls)
        cls.send(:extend, ClassMethods)
      end


      class Wrapper
        include Java::HudsonModel::RootAction
        include Java::Ruby::SimpleGet
        include Java::Ruby::RubyDoDynamic

        def initialize(plugin, object)
          super(object.name)
          puts "Hudson::Plugin::RootAction::Wrapper.new(#{plugin}, #{object})"
          @plugin, @object = plugin, object
        end

        def getIconFileName()
          @object.icon_file_name
        end

        def getDisplayName
          @object.display_name
        end

        def getUrlName
          @object.url_name
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