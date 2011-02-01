
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
Hudson::Plugin::Descriptor.new("fog_cloud", FogCloud, self, Java::HudsonSlaves::Cloud.java_class).tap do |d|
  @java.addDescriptor d
  descriptors[FogCloud] = d
end
