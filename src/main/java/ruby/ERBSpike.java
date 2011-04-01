package ruby;


import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.embed.ScriptingContainer;

public class ERBSpike {

	public static void main(String[] args) {
		ScriptingContainer jruby = new ScriptingContainer();
		jruby.runScriptlet("require 'erb'");
		RubyClass erb = (RubyClass) jruby.runScriptlet("ERB");
		RubyObject template = (RubyObject) jruby.callMethod(erb, "new", "Hello <%= @name %>");
		String result = (String) jruby.callMethod(template, "result");
		System.out.println("result = " + result);
	}
}
