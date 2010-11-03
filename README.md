
# Extend Hudson CI with JRuby

This is an experimental playground for testing hudson and jruby integration.

At present, implements the following ruby class as a hudson builder:

    class RubyHelloWorldBuilder < Hudson::Builder

      def perform(build, launcher, listener)
        listener.getLogger().println("Hello From Ruby Land!");
        true
      end

    end

# Hacking

to run, you need a the plugin development maven setup. To summarize, add the following to `~/.m2/settings.xml`

    <settings>
      <pluginGroups>
        <pluginGroup>org.jvnet.hudson.tools</pluginGroup>
      </pluginGroups>
    </settings>

check it out and run it:

    git clone git://github.com/cowboyd/ruby.hpi.git
    cd ruby.hpi
    mvn hpi:run

please record your findings on the wiki.