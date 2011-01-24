
require 'hudson/plugin/descriptor'
load 'plugin/models/cloud.rb'
@java.addDescriptor Hudson::Plugin::Descriptor.new("cloud", Cloud)