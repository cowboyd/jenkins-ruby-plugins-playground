package thefrontside.ci;
import hudson.Extension;
import hudson.slaves.Cloud;
import hudson.slaves.NodeProvisioner;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.jruby.embed.ScriptingContainer;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

public class EC2CloudDelegate extends Cloud  {
	@SuppressWarnings({"FieldCanBeLocal"})
	private transient RubyClass rubyClass;
	private transient ScriptingContainer ruby;
	private transient RubyObject rubyObject;

	@DataBoundConstructor
	public EC2CloudDelegate(String name) {
		super(name);
		this.name = name;
		this.readResolve();
		System.out.println("EC2CloudDelegate.EC2CloudDelegate");
		this.ruby = this.getDescriptor().getRuby();
		this.rubyClass = this.getDescriptor().getRubyClass();
		this.rubyObject = (RubyObject)this.ruby.callMethod(this.rubyClass, "new", name);

	}

	public boolean canProvision(Label label) {
		System.out.println("EC2CloudDelegate.canProvision");
		RubyObject value = (RubyObject) invoke("can_provision?");
		return !(value.isFalse() || value.isNil());
	}

	public Collection<NodeProvisioner.PlannedNode> provision(Label label, int i) {
		System.out.println("EC2CloudDelegate.provision");
		return (Collection<NodeProvisioner.PlannedNode>) invoke("provision", label, i);
	}

	@Override
	public EC2CloudDescriptor getDescriptor() {
		return (EC2CloudDescriptor) super.getDescriptor();
	}

	private Object invoke(String method, Object... args) {
		return this.ruby.callMethod(this.rubyObject, method, args);
	}

	@Extension
	public static class EC2CloudDescriptor extends Descriptor<Cloud> {

		private transient ScriptingContainer jruby;
		private transient RubyClass rubyClass;

		public EC2CloudDescriptor() {
			System.out.println("EC2CloudDelegate$EC2CloudDescriptor.EC2CloudDescriptor");
			jruby = new ScriptingContainer();
			jruby.setClassLoader(jruby.getClass().getClassLoader());
			load("hudson.rb");
			load("ec2_cloud.rb");
			rubyClass = (RubyClass) jruby.runScriptlet("EC2Cloud");
		}

		public String getDisplayName() {
			return jruby.callMethod(rubyClass, "display_name").toString();
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

		public RubyClass getRubyClass() {
			return rubyClass;
		}

		public ScriptingContainer getRuby() {
			return jruby;
		}
	}
}

