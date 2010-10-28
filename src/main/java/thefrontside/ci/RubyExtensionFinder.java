package thefrontside.ci;

import hudson.ExtensionFinder;
import hudson.ExtensionComponent;
import hudson.Extension;
import hudson.model.Hudson;

import java.util.Collection;
import java.util.ArrayList;

import org.jruby.embed.ScriptingContainer;


@Extension
public class RubyExtensionFinder extends ExtensionFinder {

	ScriptingContainer jruby = new ScriptingContainer();

	public <T> Collection<ExtensionComponent<T>> find(Class<T> tClass, Hudson hudson) {
		System.out.printf("find(%s)\n", tClass.getName());
		return new ArrayList<ExtensionComponent<T>>();
	}
}
