

class NoopWrapper < Jenkins::Tasks::BuildWrapper

  display_name "The Amazing Noop Wrapper"

  def setup(build, launcher, listener, env)
    listener.log "Hello from the NoopWrapper!"
  end

  def teardown(build, listener, env)
    listener.log "Hello from the NoopWrapper#teardown!"
  end

end
