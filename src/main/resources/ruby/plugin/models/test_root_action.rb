
require 'hudson/plugin/root_action'

class TestRootAction < Hudson::Plugin::RootAction
    def icon_file_name
      "gear.png";
    end

    def display_name
        return "ERB Test"
    end

    def url_name
        return "erb"
    end
end