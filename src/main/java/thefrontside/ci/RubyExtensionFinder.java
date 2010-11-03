package thefrontside.ci;

import hudson.ExtensionFinder;
import hudson.ExtensionComponent;
import hudson.Extension;
import hudson.Launcher;
import hudson.tasks.Builder;
import hudson.model.Hudson;
import hudson.model.Descriptor;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import net.sf.json.JSONObject;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.EmbedEvalUnit;
import org.jruby.RubyObject;
import org.kohsuke.stapler.StaplerRequest;


@Extension
public class RubyExtensionFinder extends ExtensionFinder {

	ScriptingContainer jruby;

	public RubyExtensionFinder() {
		jruby = new ScriptingContainer();
		jruby.setClassLoader(jruby.getClass().getClassLoader());
	}

	public <T> Collection<ExtensionComponent<T>> find(Class<T> tClass, Hudson hudson) {
		jruby.runScriptlet(String.format("puts 'find(%s)'", tClass.getName()));
		return new ArrayList<ExtensionComponent<T>>();
	}



	public static class JRubyHelloWorldBuilderDelegate extends Builder {
		private transient ScriptingContainer jruby;
		private transient RubyObject rubyObject;

		public JRubyHelloWorldBuilderDelegate(ScriptingContainer jruby, RubyObject object) {
			this.jruby = jruby;
			this.rubyObject = object;
		}

		@Override
		public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
			return (Boolean)jruby.callMethod(rubyObject, "perform", build, launcher, listener);
		}
	}

	@Extension
	public static class RubyHelloWorldDescriptor extends Descriptor<Builder> {
		private static ScriptingContainer jruby;
		private Object rubyClass;

		public RubyHelloWorldDescriptor() {
			super(JRubyHelloWorldBuilderDelegate.class);
			jruby = new ScriptingContainer();
			jruby.setClassLoader(jruby.getClass().getClassLoader());
			InputStream script = HelloWorldBuilder.class.getResourceAsStream("HelloWorldBuilder/hello_world_builder.rb");
			System.out.println("script = " + script);
			rubyClass = jruby.runScriptlet(script, "hello_world_builder.rb");
		}

		public String getDisplayName() {
			return "Say Hello From Ruby!";
		}

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return new JRubyHelloWorldBuilderDelegate(jruby, (RubyObject) jruby.callMethod(rubyClass, "new"));
        }
	}
}
