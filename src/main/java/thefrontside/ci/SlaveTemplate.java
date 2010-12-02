package thefrontside.ci;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import hudson.model.Label;
import hudson.model.Node;
import hudson.Extension;
import hudson.Util;
import hudson.model.labels.LabelAtom;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class SlaveTemplate implements Describable<SlaveTemplate> {
    public final String ami;
    public final String description;
    public final String remoteFS;
    public final String remoteAdmin;
    public final String flavor;
    public final String labels;
    protected transient EC2CloudDelegate parent;

    private transient /*almost final*/ Set<LabelAtom> labelSet;

    @DataBoundConstructor
    public SlaveTemplate(String ami, String flavor, String remoteFS, String labelString, String description, String remoteAdmin) {
        System.out.println("SlaveTemplate.SlaveTemplate");
        this.ami = ami;
        this.remoteFS = remoteFS;
        this.remoteAdmin = remoteAdmin;
        this.flavor = flavor;
        this.labels = Util.fixNull(labelString);
        this.description = description;
        readResolve(); // initialize
    }

    public String getDisplayName() {
        return description+" ("+ami+")";
    }


    /**
     * Provisions a new EC2 slave.
     *
     * @return always non-null. This needs to be then added to {@link Hudson#addNode(Node)}.
     */
    public EC2SlaveDelegate provision(TaskListener listener) throws IOException {
        // @TODO provision via ruby-fog
        System.out.println("SlaveTemplate.provision");
        return null;
    }

    /**
     * Provisions a new EC2 slave based on the currently running instance on EC2,
     * instead of starting a new one.
     */
    public EC2SlaveDelegate attach(String instanceId, TaskListener listener) throws IOException {
        System.out.println("SlaveTemplate.attach");
        // @TODO attach via ruby-fog
        return null;
    }

    /**
     * Initializes data structure that we don't persist.
     */
    protected Object readResolve() {
        labelSet = Label.parse(labels);
        return this;
    }

    public boolean containsLabel(Label l) {
        return l==null || labelSet.contains(l);
    }

    public Descriptor<SlaveTemplate> getDescriptor() {
        System.out.println("SlaveTemplate.getDescriptor");
        return Hudson.getInstance().getDescriptor(getClass());
    }

    @Extension
    public static final class SlaveTemplateDescriptor extends Descriptor<SlaveTemplate> {
        public SlaveTemplateDescriptor() {
            System.out.println("SlaveTemplateDescriptor.SlaveTemplateDescriptor");
        }
        public String getDisplayName() {
            return null;
        }
    }
}
