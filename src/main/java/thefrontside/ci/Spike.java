package thefrontside.ci;


import org.jruby.embed.ScriptingContainer;

public class Spike {

	public static void needThread(Thread t) {
		System.out.println("t = " + t);
		System.out.println("t.getClass() = " + t.getClass());
	}

	public static void main(String[] args) {
		ScriptingContainer ruby = new ScriptingContainer();
		ruby.runScriptlet("" +
				"class Foo < Java::JavaLang::Thread\n" +
				"  def run()\n" +
				"  end\n" +
				"end\n" +
				"\n" +
				"class Bar < Java::JavaLang::Thread\n" +
				"  def run();puts 'hi';end;\n" +
				"end\n" +
				"");
		ruby.runScriptlet("Java::ThefrontsideCi::Spike.needThread(Foo.new)");
		ruby.runScriptlet("Java::ThefrontsideCi::Spike.needThread(Foo.new)");
	}
}
