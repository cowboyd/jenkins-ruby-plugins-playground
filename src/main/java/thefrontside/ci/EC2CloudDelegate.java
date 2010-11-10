package thefrontside.ci;
import hudson.Launcher;
import hudson.Extension;
import hudson.slaves.Cloud;
import hudson.slaves.NodeProvisioner;
import hudson.util.FormValidation;
import hudson.model.*;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import org.jruby.embed.ScriptingContainer;
import org.jruby.RubyClass;
import org.jruby.RubyObject;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

public class EC2CloudDelegate extends Cloud  {

	protected EC2CloudDelegate() {
		super("EC2");
	}

	public boolean canProvision(Label label) {
		return false;
	}

	public Collection<NodeProvisioner.PlannedNode> provision(Label label, int i) {
		return new HashSet<NodeProvisioner.PlannedNode>();
	}

	@Override
	public EC2CloudDescriptor getDescriptor() {
		return (EC2CloudDescriptor) super.getDescriptor();
	}

	@Extension
	public static class EC2CloudDescriptor extends Descriptor<Cloud> {

		private transient ScriptingContainer jruby;
		private transient Object cloud;

		public EC2CloudDescriptor() {
			jruby = new ScriptingContainer();
			jruby.setClassLoader(jruby.getClass().getClassLoader());
			load("hudson.rb");
			load("ec2_cloud.rb");
			cloud = jruby.runScriptlet("EC2Cloud");
		}

		public String getDisplayName() {
			return jruby.callMethod(cloud, "display_name").toString();
		}

		private Object load(String script) {
			Class resources = EC2CloudDelegate.class;
			InputStream stream = resources.getResourceAsStream("EC2CloudDelegate/" + script);
			if (stream != null) {
				return jruby.runScriptlet(stream, script);
			} else {
				throw new IllegalStateException("can't find the ruby implementation: ec2_cloud.rb");
			}
		}
	}
}

