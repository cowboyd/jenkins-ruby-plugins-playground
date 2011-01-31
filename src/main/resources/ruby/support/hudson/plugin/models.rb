
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
@java.addDescriptor Hudson::Plugin::Descriptor.new("fog_cloud", FogCloud, self, Java::HudsonSlaves::Cloud.java_class)
