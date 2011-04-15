
module Hudson
  module Plugin
    class RootAction

      class Wrapper
        include Java::HudsonModel::RootAction
        include Java::Ruby::SimpleGet
        include Java::OrgKohsukeStapler::StaplerProxy

        def initialize(object)
          puts "Hudson::Plugin::RootAction::Wrapper.new(#{object})"
          @object = object
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

        def unwrap
          @object
        end

        def get(name)
          @object.respond_to?(name) ? @object.send(name) : nil
        end

        def getTarget()
          @object
        end
      end

    end
  end
end