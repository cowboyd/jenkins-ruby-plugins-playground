package thefrontside.ci;

import hudson.Extension;
import hudson.util.Secret;
import hudson.slaves.Cloud;
import hudson.slaves.NodeProvisioner;
import hudson.model.*;

import org.kohsuke.stapler.DataBoundConstructor;

import org.jruby.embed.ScriptingContainer;
import org.jruby.RubyClass;
import org.jruby.RubyObject;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EC2CloudDelegate extends Cloud  {
	@SuppressWarnings({"FieldCanBeLocal"})
    private transient ScriptingContainer ruby;
	private transient RubyClass rubyClass;
	private transient RubyObject rubyObject;

    private final String region;
    private final String accessId;
    private final Secret secretKey;
    private final String privateKey;
    public final int instanceCap;
    private final List<SlaveTemplate> templates;


    @DataBoundConstructor
	public EC2CloudDelegate(String region, String accessId, String secretKey, String privateKey, String instanceCapStr, List<SlaveTemplate> templates) {
		super("ec2-"+region);
        this.region = region;
        this.accessId = accessId;
        this.secretKey = Secret.fromString(secretKey.trim());
        this.privateKey = privateKey;
        if(instanceCapStr == null || instanceCapStr.equals(""))
            this.instanceCap = Integer.MAX_VALUE;
        else
            this.instanceCap = Integer.parseInt(instanceCapStr);
        if( templates == null )
            templates = Collections.emptyList();
        this.templates = templates;

		this.readResolve(); // Set parents

		System.out.println("EC2CloudDelegate.EC2CloudDelegate");

        this.ruby = this.getDescriptor().getRuby();
		this.rubyClass = this.getDescriptor().getRubyClass();
        // Create ruby instance too
		this.rubyObject = (RubyObject)this.ruby.callMethod(this.rubyClass, "new", region, accessId, secretKey, privateKey, instanceCap, templates);
    }

    public Object readResolve() {
        // @TODO migrate to ruby fog
        for (SlaveTemplate t : templates)
            t.parent = this;
        return this;
    }

    public String getAccessId() {
        return accessId;
    }

    public String getSecretKey() {
        return secretKey.getEncryptedValue();
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getInstanceCapStr() {
        if(instanceCap==Integer.MAX_VALUE)
            return "";
        else
            return String.valueOf(instanceCap);
    }

    public List<SlaveTemplate> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    public SlaveTemplate getTemplate(Label label) {
        for (SlaveTemplate t : templates)
            if(t.containsLabel(label))
                return t;
        return null;
    }

	public boolean canProvision(Label label) {
		System.out.println("EC2CloudDelegate.canProvision");
		Object value = invoke("can_provision?", label);
		return (value != null && value != Boolean.FALSE);
	}

	public Collection<NodeProvisioner.PlannedNode> provision(Label label, int excessWorkload) {
		System.out.println("EC2CloudDelegate.provision");
		return (Collection<NodeProvisioner.PlannedNode>) invoke("provision", label, excessWorkload);
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	private Object invoke(String method, Object... args) {
		return this.ruby.callMethod(this.rubyObject, method, args);
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<Cloud> {

		private transient ScriptingContainer ruby;
		private transient RubyClass rubyClass;

		public DescriptorImpl() {
			System.out.println("EC2CloudDelegate$DescriptorImpl.DescriptorImpl");

            this.ruby = ((PluginImpl)Hudson.getInstance().getPlugin(PluginImpl.class)).getRuby();
		    // This would be generated as <RubyClass>Delegate
			rubyClass = (RubyClass)ruby.runScriptlet("EC2Cloud");
		}

		public String getDisplayName() {
			return ruby.callMethod(rubyClass, "display_name").toString();
		}

//        public FormValidation doTestConnection(
//                @QueryParameter AwsRegion region,
//                @QueryParameter String accessId,
//                @QueryParameter String secretKey,
//                @QueryParameter String privateKey) throws IOException, ServletException {
//            return null;
//        }

		public RubyClass getRubyClass() {
			return rubyClass;
		}

		public ScriptingContainer getRuby() {
			return ruby;
		}
	}
}

