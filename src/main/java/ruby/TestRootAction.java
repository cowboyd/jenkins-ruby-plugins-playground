package ruby;

import hudson.Extension;
import hudson.model.RootAction;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class TestRootAction implements RootAction {
    public String getIconFileName() {
        return "gear.png";
    }

    public String getDisplayName() {
        return "ERB Test";
    }

    public String getUrlName() {
        return "erb";
    }
}
