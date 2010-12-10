package thefrontside.ci;

import hudson.model.Computer;
import hudson.model.Descriptor.FormException;
import hudson.model.Slave;

import hudson.slaves.NodeProperty;
import hudson.Extension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.embed.ScriptingContainer;
import org.kohsuke.stapler.DataBoundConstructor;

public final class EC2SlaveDelegate extends Slave implements RubyDelegate {
    public final String remoteAdmin; // e.g. 'ubuntu'

    private transient ScriptingContainer ruby;
    private transient RubyClass rubyClass;
    private transient RubyObject rubyObject;
    
    @DataBoundConstructor
    public EC2SlaveDelegate(String instanceId, String description, String remoteFS, int numExecutors, Mode mode, String labelString, List<? extends NodeProperty<?>> nodeProperties, String remoteAdmin) throws FormException, IOException {
        super(instanceId, description, remoteFS, numExecutors, mode, labelString, new EC2ComputerLauncherDelegate(), new EC2RetentionStrategyDelegate(), nodeProperties);
        System.out.println("EC2SlaveDelegate.EC2SlaveDelegate");
        this.remoteAdmin = remoteAdmin;
    }
            
    /**
     * Constructor for debugging.
     */
//    public EC2SlaveDelegate(String instanceId) throws FormException, IOException {
//        this(instanceId,"debug","/tmp/hudson",1, Mode.NORMAL, "debug", "", Collections.<NodeProperty<?>>emptyList(), null);
//    }

    @Override
    public Computer createComputer() {
        // @TODO Create computer via ruby fog
        System.out.println("EC2SlaveDelegate.createComputer");
        return new EC2ComputerDelegate(this);
    }

    /**
     * Terminates the instance in EC2.
     */
    public void terminate() {
        System.out.println("EC2SlaveDelegate.terminate");
    }

    String getRemoteAdmin() {
        if (remoteAdmin == null || remoteAdmin.length() == 0)
            return "root";
        return remoteAdmin;
    }

    public RubyObject getInstancedObject() {
        return rubyObject;
    }

    public void setInstancedObject(RubyObject newObj) {
        rubyObject = newObj;
    }

    public RubyClass getClassObject() {
        return rubyClass;
    }

    public void setClassObject(RubyClass newClass) {
        rubyClass = newClass;
    }

    public static final class DescriptorImpl extends SlaveDescriptor {
        public DescriptorImpl() {
            System.out.println("EC2SlaveDescriptor.EC2SlaveDescriptor");
        }
        public String getDisplayName() {
            return "Amazon EC2";
        }

        @Override
        public boolean isInstantiable() {
            return false;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(EC2SlaveDelegate.class.getName());
}
