
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
load 'plugin/models/noop_wrapper.rb'
Hudson::Plugin::Descriptor.new("fog_cloud", FogCloud, self, Java::HudsonSlaves::Cloud.java_class).tap do |d|
  @java.addDescriptor d
  descriptors[FogCloud] = d
end

Hudson::Plugin::Descriptor.new("noop_wrapper", NoopWrapper, self, Java::HudsonTasks::BuildWrapper.java_class).tap do |d|
  @java.addDescriptor d
  descriptors[NoopWrapper] = d
end
