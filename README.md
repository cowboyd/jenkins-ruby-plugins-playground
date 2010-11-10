
# Provision Hudson Slave Nodes with Fog (formerly hello world in ruby)

This hudson plugin will allow you to spin up hudson slave nodes on-demand using the [Fog](http://github.com/geemus/fog)
ruby library.

It addresses my immediate need for a cloud plugin that I can maintain, but it also serves as a template / proving ground for
authoring hudson plugins in ruby.

# Hacking

to run, you need a the plugin development maven setup. To summarize, add the following to `~/.m2/settings.xml`

    <settings>
      <pluginGroups>
        <pluginGroup>org.jvnet.hudson.tools</pluginGroup>
      </pluginGroups>
    </settings>

check it out and run it:

    git clone git://github.com/cowboyd/fog.hpi.git
    cd fog.hpi
    mvn hpi:run
