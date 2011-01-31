
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
@java.tap do |java|
  @java.addDescriptor(Class.new(Java::Ruby::RubyCloudDescriptor).class_eval do
    include Hudson::Plugin::Descriptor

    def initialize(name, impl, plugin)
      super()
      @name, @impl, @plugin = name, impl, plugin
    end

    new("cloud", FogCloud, java)
  end)
end