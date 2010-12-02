package thefrontside.ci;

import hudson.model.Descriptor;
import hudson.slaves.RetentionStrategy;
import hudson.util.TimeUnit2;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Logger;

public class EC2RetentionStrategyDelegate extends RetentionStrategy<EC2ComputerDelegate> {
    @DataBoundConstructor
    public EC2RetentionStrategyDelegate() {
    }

    public synchronized long check(EC2ComputerDelegate c) {
        System.out.println("EC2RetentionStrategyDelegate.check");
        return 1;
    }

    /**
     * Try to connect to it ASAP.
     */
    @Override
    public void start(EC2ComputerDelegate c) {
        System.out.println("EC2RetentionStrategyDelegate.start");
    }

    // no registration since this retention strategy is used only for EC2 nodes that we provision automatically.
    // @Extension
    public static class DescriptorImpl extends Descriptor<RetentionStrategy<?>> {
        public String getDisplayName() {
            return "EC2";
        }
    }

    private static final Logger LOGGER = Logger.getLogger(EC2RetentionStrategyDelegate.class.getName());

    public static boolean disabled = Boolean.getBoolean(EC2RetentionStrategyDelegate.class.getName()+".disabled");
}
