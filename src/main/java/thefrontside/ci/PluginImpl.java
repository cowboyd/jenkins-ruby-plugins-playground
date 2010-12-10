package thefrontside.ci;

import hudson.Extension;
import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.Util;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.List;
import java.util.WeakHashMap;

@Extension
public class PluginImpl extends Plugin implements Describable<PluginImpl> {
    private transient ScriptingContainer rubyContainer;

    private transient WeakHashMap<RubyClass, Class<? extends RubyDelegate>> rubyClassHash;
    private transient WeakHashMap<RubyObject, RubyDelegate> rubyObjectHash;
    
    @Override
    public void start() throws Exception {
        // Load up the ruby stuff
        rubyContainer = new ScriptingContainer(LocalContextScope.SINGLETHREAD);

        // Use bundler to load our gems up.
        PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(PluginImpl.class);
        rubyContainer.runScriptlet("ENV['BUNDLE_GEMFILE'] = \"" + Hudson.getInstance().getPlugin(this.getClass()).getClass().getResource("Gemfile").getPath() + "\"");
        rubyContainer.runScriptlet("require 'rubygems'");
        rubyContainer.runScriptlet("require 'bundler/setup'");
        rubyContainer.runScriptlet("Bundler.require");
        
        // Init some ruby storage stuff
        rubyContainer.setClassLoader(rubyContainer.getClass().getClassLoader());
        rubyClassHash = new WeakHashMap<RubyClass, Class<? extends RubyDelegate>>();
        rubyObjectHash = new WeakHashMap<RubyObject, RubyDelegate>();

        // Load the ruby files
        loadRubyScript("hudson.rb");    
		loadRubyScript("EC2CloudDelegate/ec2_cloud.rb");
        loadRubyScript("EC2SlaveDelegate/ec2_slave.rb");
        loadRubyScript("EC2SlaveDelegate/slave_template.rb");                
        System.out.println("Fog.hpi: Scripts loaded.");
                                        
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

    public static void addRubyDelegate(RubyDelegate owner) {
        PluginImpl.get().rubyClassHash.put(owner.getClassObject(), owner.getClass());
        PluginImpl.get().rubyObjectHash.put(owner.getInstancedObject(), owner);
    }

    public static void removeRubyDelegate(RubyDelegate owner) {
        PluginImpl.get().rubyClassHash.remove(owner.getClassObject());
        PluginImpl.get().rubyObjectHash.remove(owner.getInstancedObject());
        owner.setInstancedObject(null);
    }

    public static RubyDelegate resolveRubyDelegate(RubyObject obj) {
        // Look for an existing Java instance
        RubyDelegate result = PluginImpl.get().rubyObjectHash.get(obj);
        if (result != null) {
            return result;
        } else {
            // None found, create a new one
            Class<? extends RubyDelegate> delegate = PluginImpl.get().rubyClassHash.get(obj.getMetaClass());
            System.out.println("resolveRubyDelegate: " + delegate.getClass());
            try {
                Constructor<? extends RubyDelegate> constructor = delegate.getConstructor(RubyObject.class);
                constructor.setAccessible(true);
                return constructor.newInstance(obj);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
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
