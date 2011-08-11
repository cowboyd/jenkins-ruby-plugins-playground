

#Jenkins::Model::Descriptor.new("fog_cloud", FogCloud, self, Java::hudson.slaves.Cloud.java_class).tap do |d|
#  @java.addExtension(d)
#  descriptors[FogCloud] = d
#end

Jenkins::Model::Descriptor.new("noop_wrapper", NoopWrapper, self, Java::hudson.tasks.BuildWrapper.java_class).tap do |d|
  @java.addExtension(d)
  @descriptors[NoopWrapper] = d
end

#Jenkins::Model::Descriptor.new("test_root_action", TestRootAction, self, Java::hudson.model.RootAction.java_class).tap do |d|
#  @java.addExtension(d)
#  @descriptors[TestRootAction] = d
#end
#
#TestRootAction.new.tap do |action|
#  @java.addExtension(@wrappers[action] = Hudson::Plugin::RootAction::Wrapper.new(action))
#end
