package ruby;


import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public interface RubyDoDynamic {

	void doDynamic(StaplerRequest request, StaplerResponse response);
}
