package ruby;

import com.thoughtworks.xstream.XStream;
import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Items;
import hudson.util.XStream2;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.jenkinsci.jruby.JRubyMapper;
import org.jenkinsci.jruby.JRubyXStream;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.proxy.InternalJavaProxy;
import org.kohsuke.stapler.MetaClass;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.jelly.JellyClassTearOff;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@Extension
@SuppressWarnings({"UnusedDeclaration"})
public class RubyPlugin extends Plugin implements Describable<RubyPlugin> {
    private ScriptingContainer ruby;

	private Object plugin;
	private ArrayList<ExtensionComponent> extensions;

	public static RubyPlugin get() {
		return Hudson.getInstance().getPlugin(RubyPlugin.class);
	}

    public static Object getRubyController() {
        return get().plugin;
    }

	public static Object callMethod(Object object, String methodName, Object... args) {
		return RubyPlugin.get().ruby.callMethod(object, methodName, args);
	}

	public void addExtension(Object extension) {
		extensions.add(new ExtensionComponent(extension));
	}

	public static String readf(String resource, Object... args) {
		return RubyPlugin.get().read(String.format(resource, args));
	}

	public RubyPlugin() {
		this.ruby = new ScriptingContainer(LocalContextScope.SINGLETHREAD);
		this.ruby.setClassLoader(this.getClass().getClassLoader());
		this.ruby.getLoadPaths().add(0, this.getClass().getResource("support").getPath());
		this.ruby.getLoadPaths().add(this.getClass().getResource(".").getPath());
		this.extensions = new ArrayList<ExtensionComponent>();
		this.ruby.runScriptlet("require 'hudson/plugin/controller'");
		Object pluginClass = this.ruby.runScriptlet("Hudson::Plugin::Controller");
		this.plugin = this.ruby.callMethod(pluginClass, "new", this);

        register((XStream2)Hudson.XSTREAM, ruby);
        register((XStream2)Items.XSTREAM, ruby);
	}

    private void register(XStream2 xs, ScriptingContainer ruby) {
        JRubyXStream.register(xs,ruby);
        synchronized (xs) {
            xs.setMapper(new JRubyMapper(xs.getMapperInjectionPoint()));
        }
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
		//WebApp.getCurrent().getMetaClass(Object.class).dispatchers.add(0, new RubyDispatcher());
        this.ruby.callMethod(plugin, "start");
    }

	@Override
	public void stop() throws Exception {
		this.ruby.callMethod(plugin, "stop");
	}

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

	public static String getResourceURI(String relativePathFormat, Object... args) {
		return get().getClass().getResource(String.format(relativePathFormat, args)).getPath();
	}

	public static Collection<ExtensionComponent> getExtensions() {
		return get().extensions;
	}

	@Extension
    public static final class DescriptorImpl extends Descriptor<RubyPlugin> {
        @Override
        public String getDisplayName() {
            return "Ruby Plugin";
        }
    }
}
