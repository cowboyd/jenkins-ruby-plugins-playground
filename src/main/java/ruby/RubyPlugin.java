package ruby;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.jruby.RubyObject;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import thefrontside.ci.RubyDelegate;

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
        // Use bundler to load our gems up.

		//Hudson.getInstance().getPlugin(this.getClass()).getClass().getResource("ruby").getPath();

        //ruby.runScriptlet("ENV['BUNDLE_GEMFILE'] = \"" + Hudson.getInstance().getPlugin(this.getClass()).getClass().getResource("Gemfile").getPath() + "\"");
        //ruby.runScriptlet("require 'bundler/setup'");

        // Init some ruby storage stuff
        //ruby.setClassLoader(ruby.getClass().getClassLoader());
        //rubyClassHash = new WeakHashMap<RubyClass, Class<? extends RubyDelegate>>();
        //rubyObjectHash = new WeakHashMap<RubyObject, RubyDelegate>();

        // Load the ruby files
        //loadRubyScript("hudson.rb");
		//loadRubyScript("EC2CloudDelegate/ec2_cloud.rb");
        //loadRubyScript("EC2SlaveDelegate/ec2_slave.rb");
        //loadRubyScript("EC2SlaveDelegate/slave_template.rb");


        // Finish loading
        //load();
    }

	@Override
	public void stop() throws Exception {
		this.ruby.callMethod(plugin, "stop");
	}

	private Object loadRubyScript(String script) {
		InputStream stream = Hudson.getInstance().getPlugin(this.getClass()).getClass().getResourceAsStream(script);
		if (stream != null) {
			return ruby.runScriptlet(stream, script);
		} else {
			throw new IllegalStateException("Unable to load requested ruby script: " + script);
		}
    }

    public static void addRubyDelegate(RubyDelegate owner) {
        //RubyPlugin.get().rubyClassHash.put(owner.getClassObject(), owner.getClass());
        //RubyPlugin.get().rubyObjectHash.put(owner.getInstancedObject(), owner);
    }

    public static void removeRubyDelegate(RubyDelegate owner) {
        //RubyPlugin.get().rubyClassHash.remove(owner.getClassObject());
        //RubyPlugin.get().rubyObjectHash.remove(owner.getInstancedObject());
        owner.setInstancedObject(null);
    }

    public static RubyDelegate resolveRubyDelegate(RubyObject obj) {
        // Look for an existing Java instance
        //RubyDelegate result = RubyPlugin.get().rubyObjectHash.get(obj);
//        if (result != null) {
//            return result;
//        } else {
//            // None found, create a new one
//            Class<? extends RubyDelegate> delegate = RubyPlugin.get().rubyClassHash.get(obj.getMetaClass());
//            System.out.println("resolveRubyDelegate: " + delegate.getClass());
//            try {
//                Constructor<? extends RubyDelegate> constructor = delegate.getConstructor(RubyObject.class);
//                constructor.setAccessible(true);
//                return constructor.newInstance(obj);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (InstantiationException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//            return null;
//        }
		return null;
    }
    
    public ScriptingContainer getRuby() {
        return ruby;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    public static RubyPlugin get() {
        return Hudson.getInstance().getPlugin(RubyPlugin.class);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<RubyPlugin> {
        @Override
        public String getDisplayName() {
            return "Fog RubyPlugin";
        }
    }
}
