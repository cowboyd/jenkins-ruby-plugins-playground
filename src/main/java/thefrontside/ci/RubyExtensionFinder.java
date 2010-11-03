package thefrontside.ci;

import hudson.ExtensionFinder;
import hudson.ExtensionComponent;
import hudson.Extension;
import hudson.tasks.Builder;
import hudson.model.Hudson;
import hudson.model.Descriptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import java.lang.reflect.Method;

import net.sf.json.JSONObject;
import org.jruby.embed.ScriptingContainer;
import org.jruby.embed.EmbedEvalUnit;
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

	@Extension
	public static class RubyHelloWorldDescriptor extends Descriptor<Builder> {

		public RubyHelloWorldDescriptor() {
			super(getRubyClass());
		}

		public String getDisplayName() {
			return "Say Hello From Ruby!";
		}

        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            // TODO: this is where you'd create an object by yourself
            return super.newInstance(req, formData);
        }

        private static Class<? extends Builder> getRubyClass() {
			System.out.println("Loading the JRuby Descriptor");
			ScriptingContainer jruby = new ScriptingContainer();
			jruby.setClassLoader(jruby.getClass().getClassLoader());
			InputStream script = HelloWorldBuilder.class.getResourceAsStream("HelloWorldBuilder/hello_world_builder.rb");
			System.out.println("script = " + script);
			Class<? extends Builder> rubyClass = (Class<? extends Builder>) jruby.runScriptlet(script, "hello_world_builder.rb");

			for	(Method method : rubyClass.getMethods()) {
                System.out.println(method.getName()+ Arrays.asList(method.getParameterTypes()));

			}

			System.out.println("rubyClass = " + rubyClass);
			return rubyClass;
		}


	}
}
