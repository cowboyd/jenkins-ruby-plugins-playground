# Do the eigenclass thingy
# See: http://www.ruby-forum.com/topic/77046
class Object
  protected
  def eigenclass # :nodoc:
    class << self; self; end  
  end  
end
