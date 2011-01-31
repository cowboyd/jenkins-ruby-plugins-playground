package ruby;


import hudson.model.Descriptor;
import hudson.slaves.Cloud;

@SuppressWarnings({"UnusedDeclaration"})
public abstract class RubyCloudDescriptor extends Descriptor<Cloud> {
	protected RubyCloudDescriptor() {
		super(Cloud.class);
	}
}
