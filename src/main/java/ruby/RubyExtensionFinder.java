package ruby;


import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.ExtensionFinder;
import hudson.model.Descriptor;
import hudson.model.Hudson;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"UnusedDeclaration"})
@Extension
public class RubyExtensionFinder extends ExtensionFinder {

	@Override
	public <T> Collection<ExtensionComponent<T>> find(Class<T> type, Hudson hudson) {
		System.out.printf("RubyExtensionFinder.find(%s)\n",type);
		if (Descriptor.class.isAssignableFrom(type)) {
			Collection descriptors = RubyPlugin.getDescriptors();
			return descriptors;
		} else {
			return new ArrayList<ExtensionComponent<T>>();
		}

	}
}
