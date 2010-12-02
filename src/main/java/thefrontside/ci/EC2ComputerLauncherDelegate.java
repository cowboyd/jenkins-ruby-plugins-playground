package thefrontside.ci;


import hudson.model.TaskListener;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.SlaveComputer;

import java.io.IOException;
import java.io.PrintStream;

/**
 * {@link ComputerLauncher} for EC2 that waits for the instance to really come up before proceeding to
 * the real user-specified {@link ComputerLauncher}.
 *
 * @author Kohsuke Kawaguchi
 */
public class EC2ComputerLauncherDelegate extends ComputerLauncher {
    @Override
    public void launch(SlaveComputer computer, TaskListener listener) {
        // @TODO link to ruby fog
        System.out.println("EC2ComputerLauncherDelegate.launch");
    }
}