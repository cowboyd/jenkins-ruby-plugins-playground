package thefrontside.ci;

import hudson.Util;
import hudson.slaves.SlaveComputer;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.Collections;

public class EC2ComputerDelegate extends SlaveComputer {

    public EC2ComputerDelegate(EC2SlaveDelegate slave) {
        super(slave);
        System.out.println("EC2ComputerDelegate.EC2ComputerDelegate");
    }

    @Override
    public EC2SlaveDelegate getNode() {
        return (EC2SlaveDelegate)super.getNode();
    }

    /**
     * When the slave is deleted, terminate the instance.
     */
    @Override
    public HttpResponse doDoDelete() throws IOException {
        System.out.println("EC2ComputerDelegate.doDoDelete");
        // @TODO Link to ruby fog
        //checkPermission(DELETE);
        //getNode().terminate();
        return new HttpRedirect("..");
    }

    /** What username to use to run root-like commands
     *
     */
    public String getRemoteAdmin() {
        return getNode().getRemoteAdmin();
    }
}
