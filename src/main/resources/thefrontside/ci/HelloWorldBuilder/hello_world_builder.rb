begin
require 'java'
require 'jruby/core_ext'

class RubyHelloWorldBuilder < Java::HudsonTasks::Builder

  def self.new()
    super()
  end

  def initialize()
    super()
  end

  def perform(build, launcher, listener)
    listener.getLogger().println("Hello From Ruby Land!");
  end

  #add_method_annotation("new")
  #add_method_annotation("super", org.kohsuke.stapler.DataBoundConstructor => {})
  add_method_annotation("initialize", org.kohsuke.stapler.DataBoundConstructor => {})
  add_method_annotation("perform", java.lang.Override => {})
end
RubyHelloWorldBuilder.become_java!('.')
rescue Exception => e
  puts "#{e.message}\n#{e.backtrace.join("\n")}"
end
