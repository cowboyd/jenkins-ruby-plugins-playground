

class NoopWrapper < Jenkins::Tasks::BuildWrapper

  display_name "The Amazing Noop Wrapper"

  def setup(*args)
    puts "Hello from the NoopWrapper: (#{args.inspect})"
    return true
  end

  def teardown(build, listener)
    puts "Hello from the NoopWrapper.teardown: (#{build.inspect}, #{listener.inspect})"
    return true
  end
end
