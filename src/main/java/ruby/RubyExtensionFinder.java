package ruby;


import hudson.Extension;
import hudson.ExtensionComponent;
import hudson.ExtensionFinder;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Hudson;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"UnusedDeclaration"})
@Extension
public class RubyExtensionFinder extends ExtensionFinder {

	@Override
	public <T> Collection<ExtensionComponent<T>> find(Class<T> type, Hudson hudson) {
		Collection<ExtensionComponent<T>> hits = new ArrayList<ExtensionComponent<T>>();
		for (ExtensionComponent c: RubyPlugin.getExtensions()) {
			if (type.isAssignableFrom(c.getInstance().getClass())) {
				hits.add(c);
			}
		}
		System.out.printf("RubyExtensionFinder.find(%s) -> %d extensions\n",type, hits.size());
		return hits;
	}
}
