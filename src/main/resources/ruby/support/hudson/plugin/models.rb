
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
@java.addDescriptor Hudson::Plugin::Descriptor.new("cloud", FogCloud, self, Java::HudsonSlaves::Cloud.java_class)
