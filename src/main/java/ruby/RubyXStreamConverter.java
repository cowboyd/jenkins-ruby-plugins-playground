package ruby;

import com.thoughtworks.xstream.converters.Converter;
import org.jruby.javasupport.proxy.InternalJavaProxy;


public abstract class RubyXStreamConverter implements Converter {


	public boolean canConvert(Class type) {
		System.out.printf("RubyXStreamConverter.canConvert(%s) -> %s\n", type, ("" + InternalJavaProxy.class.isAssignableFrom(type)));
		return InternalJavaProxy.class.isAssignableFrom(type);
	}
}
