require 'java'
require 'jruby/core_ext'

class RubyHelloWorldBuilder


  def perform(build, launcher, listener)
    listener.getLogger().println("Hello From Ruby Land!");
    true
  end

  #add_method_annotation("new")
  #add_method_annotation("super", org.kohsuke.stapler.DataBoundConstructor => {})
  #add_method_annotation("initialize", org.kohsuke.stapler.DataBoundConstructor => {})
  #add_method_annotation("perform", java.lang.Override => {})
end

RubyHelloWorldBuilder

