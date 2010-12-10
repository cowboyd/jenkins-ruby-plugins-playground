package thefrontside.ci;

import org.jruby.RubyClass;
import org.jruby.RubyObject;

/**
 * RubyDelegate
 *
 * This interface is for objects that delegate to a Ruby object for functionality. This allows them to be managed by
 * our PluginImpl, which keeps a WeakHashMap to all RubyDelegates and their objects.

 * User: elixir
 * Date: Dec 7, 2010
 * Time: 7:36:01 AM

 */

public interface RubyDelegate {
    /*
     * implements RubyDelegate
     */
    RubyObject getInstancedObject();
    void setInstancedObject(RubyObject newObj);
    
    RubyClass getClassObject();
    void setClassObject(RubyClass newClass);
}
