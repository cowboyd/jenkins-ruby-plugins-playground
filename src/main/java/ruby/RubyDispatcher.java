package ruby;

import org.kohsuke.stapler.Dispatcher;
import org.kohsuke.stapler.RequestImpl;
import org.kohsuke.stapler.ResponseImpl;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class RubyDispatcher extends Dispatcher {

	public boolean dispatch(RequestImpl req, ResponseImpl rsp, Object node) throws IOException, ServletException, IllegalAccessException, InvocationTargetException {
		System.out.printf("RubyDispatcher.dispatch(%s,%s, %s)\n", req, rsp, node);
		return false;
	}

	public String toString() {
		return "dispatches requests on ruby objects";
	}
}
