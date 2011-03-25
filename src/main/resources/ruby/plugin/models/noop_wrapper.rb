
require 'hudson/plugin/build_wrapper'

class NoopWrapper < Hudson::Plugin::BuildWrapper

  def self.display_name
    "The Amazing Noop Wrapper"
  end

  def setup(*args)
    puts "Hello from the NoopWrapper: (#{args.inspect})"
    return true
  end

  def teardown(build, listener)
    puts "Hello from the NoopWrapper.teardown: (#{build.inspect}, #{listener.inspect})"
    return true
  end
end