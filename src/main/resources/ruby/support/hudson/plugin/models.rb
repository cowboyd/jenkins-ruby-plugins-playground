
require 'hudson/plugin/descriptor'
load 'plugin/models/fog_cloud.rb'
load 'plugin/models/noop_wrapper.rb'
load 'plugin/models/test_root_action.rb'

Hudson::Plugin::Descriptor.new("fog_cloud", FogCloud, self, Java::HudsonSlaves::Cloud.java_class).tap do |d|
  @java.addExtension(d)
  descriptors[FogCloud] = d
end

Hudson::Plugin::Descriptor.new("noop_wrapper", NoopWrapper, self, Java::HudsonTasks::BuildWrapper.java_class).tap do |d|
  @java.addExtension(d)
  descriptors[NoopWrapper] = d
end


Hudson::Plugin::Descriptor.new("test_root_action", TestRootAction, self, Java::HudsonModel::RootAction.java_class).tap do |d|
  @java.addExtension(d)
  descriptors[TestRootAction] = d
end

TestRootAction.new.tap do |action|
  @java.addExtension(@wrappers[action] = Hudson::Plugin::RootAction::Wrapper.new(action))
end
