package ruby;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.IOException;
import java.io.InputStream;

@Extension
public class RubyPlugin extends Plugin implements Describable<RubyPlugin> {
    private transient ScriptingContainer ruby;

	private Object plugin;

	public RubyPlugin() {
		this.ruby = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
		this.ruby.setClassLoader(this.getClass().getClassLoader());
		this.ruby.getLoadPaths().add(0, this.getClass().getResource("support").getPath());
		this.ruby.runScriptlet("require 'hudson/plugin/controller'");
		Object pluginClass = this.ruby.runScriptlet("Hudson::Plugin::Controller");
		this.plugin = this.ruby.callMethod(pluginClass, "new", this);
	}


	public String read(String resource) {
		InputStream stream = this.getClass().getResourceAsStream(resource);
		try {
			if (stream == null) {
				throw new RuntimeException("no such resource: " + resource);
			}
			StringBuffer buffer = new StringBuffer();
			for (int c = stream.read(); c > 0; c = stream.read()) {
				buffer.append((char)c);
			}
			return buffer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
    public void start() throws Exception {
        this.ruby.callMethod(plugin, "start");
    }

	@Override
	public void stop() throws Exception {
		this.ruby.callMethod(plugin, "stop");
	}

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<RubyPlugin> {
        @Override
        public String getDisplayName() {
            return "Fog RubyPlugin";
        }
    }
}
