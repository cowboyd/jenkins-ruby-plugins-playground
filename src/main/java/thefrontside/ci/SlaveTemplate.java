package thefrontside.ci;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import hudson.model.Label;
import hudson.model.Node;
import hudson.Extension;
import hudson.Util;
import hudson.model.labels.LabelAtom;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.embed.ScriptingContainer;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Set;


public class SlaveTemplate implements Describable<SlaveTemplate>,RubyDelegate {
    public final String ami;
    public final String description;
    public final String remoteFS;
    public final String remoteAdmin;
    public final String flavor;
    public final String labels;
    protected transient EC2CloudDelegate parent;

    private transient ScriptingContainer ruby;
    private transient RubyClass rubyClass;
    private transient RubyObject rubyObject;

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

    /**
     * Initializes data structure that we don't persist.
     */
    protected Object readResolve() {
		System.out.println("SlaveTemplate.readResolve");
        // Cache the ruby object
        ruby = Hudson.getInstance().getPlugin(PluginImpl.class).getRuby();
        rubyClass = (RubyClass)ruby.runScriptlet("SlaveTemplate");
        // Create object & save
        rubyObject = (RubyObject)ruby.callMethod(rubyClass, "new", ami, description, remoteFS, remoteAdmin, flavor, labels);
        PluginImpl.addRubyDelegate(this);

        labelSet = Label.parse(labels);

        return this;
    }

    public String getDisplayName() {
        return description + " (" + ami + ")";
    }


    /**
     * Provisions a new EC2 slave.
     *
     * @return always non-null. This needs to be then added to {@link Hudson#addNode(Node)}.
     */
    public EC2SlaveDelegate provision(TaskListener listener) throws IOException {
        // @TODO provision via ruby-fog
        System.out.println("SlaveTemplate.provision");
        Object value = invoke("provision", listener);
        EC2SlaveDelegate result = (EC2SlaveDelegate)PluginImpl.resolveRubyDelegate((RubyObject)value);
        return result;
    }

    /**
     * Provisions a new EC2 slave based on the currently running instance on EC2,
     * instead of starting a new one.
     */
    public EC2SlaveDelegate attach(String instanceId, TaskListener listener) throws IOException {
        System.out.println("SlaveTemplate.attach");
        Object value = invoke("attach", instanceId, listener);
        EC2SlaveDelegate result = (EC2SlaveDelegate)PluginImpl.resolveRubyDelegate((RubyObject)value);
        return result;
    }

    public RubyObject getInstancedObject() {
        return rubyObject;
    }

    public void setInstancedObject(RubyObject obj) {
        rubyObject = obj;
    }

    public RubyClass getClassObject() {
        return rubyClass;
    }

    public void setClassObject(RubyClass newClass) {
        rubyClass = newClass;
    }

    private Object invoke(String method, Object... args) {
        return ruby.callMethod(rubyObject, method, args);
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

        public String[] getFlavors() {
            return new String[] {"m1.large", "Medium", "Fucking Awesome"};
        }

        public String getDisplayName() {
            return null;
        }
    }
}
