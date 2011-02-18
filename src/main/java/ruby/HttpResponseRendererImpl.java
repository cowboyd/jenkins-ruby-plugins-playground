package ruby;

import org.jruby.javasupport.proxy.InternalJavaProxy;
import org.kohsuke.stapler.HttpResponseRenderer;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Takes a return value from methods like "doDynamic" and convert it to HTML response.
 *
 * @author Kohsuke Kawaguchi
 */
public class HttpResponseRendererImpl extends HttpResponseRenderer {
    @Override
    public boolean generateResponse(StaplerRequest req, StaplerResponse rsp, Object node, Object response) throws IOException, ServletException {
        if (InternalJavaProxy.class.isAssignableFrom(response.getClass())) {
            // TODO: render the response
        }
    }
}
