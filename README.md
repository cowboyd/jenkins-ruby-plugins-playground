
# Extend Hudson CI with JRuby

Currently there isn't much here beyond the standard Hello World Plugin. That's mostly because we've been 
familiarizing ourselves with the Hudson [extension mechanisms](http://wiki.hudson-ci.org/display/HUDSON/Extension+points) that are available

Currently, it seems like the most promising tack is to bundle a custom [ExtensionFinder](http://hudson-ci.org/javadoc/index.html?hudson/ExtensionFinder.html). This would be our bootstrap class implemented
in Java  which can instantiate a jruby interpreter and load instances of the desired extension points in ruby.

## Next Steps
Before coming up with a fully generic mechanism, I'm going to try and re-implement the HelloWorldBuilder in jruby, including

* Implementation
* Descriptor
* global config
* field config

Once the class is converted to ruby, then we'll look to using something other than Jelly to extend the forms. maybe erb, mustache
liquid or just builder.

## Questions

* If every plugin gets its own JRuby interpreter, and every plugin is loaded in its own ClassLoader. Is that going to release
the proverbial memory Kraken?
* How can we replace the current plugin development with Rake, so that it's more like
  * hudson plugin --init     #(generates Rakefile)
  * rake package             #builds.hpi
  
## Random Thoughts

* It would be nice to use bundler to install all plugins required gems into the generated .hpi file


