package thefrontside.ci;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.jruby.embed.ScriptingContainer;

import java.io.InputStream;

@Extension
public class PluginImpl extends Plugin implements Describable<PluginImpl> {
    private transient ScriptingContainer rubyContainer;

    @Override
    public void start() throws Exception {
        // Load up the ruby stuff
        rubyContainer = new ScriptingContainer();
        rubyContainer.setClassLoader(rubyContainer.getClass().getClassLoader());
        // Load the ruby files
        loadRubyScript("hudson.rb");
		loadRubyScript("EC2CloudDelegate/ec2_cloud.rb");
        loadRubyScript("EC2SlaveDelegate/ec2_slave.rb");

        // Finish loading
        load();
    }

    private Object loadRubyScript(String script) {
		InputStream stream = Hudson.getInstance().getPlugin(this.getClass()).getClass().getResourceAsStream(script);
		if (stream != null) {
			return rubyContainer.runScriptlet(stream, script);
		} else {
			throw new IllegalStateException("Unable to load requested ruby script: " + script);
		}        
    }

    public ScriptingContainer getRuby() {
        return rubyContainer;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static PluginImpl get() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<PluginImpl> {
        @Override
        public String getDisplayName() {
            return "Fog PluginImpl";
        }
    }
}
