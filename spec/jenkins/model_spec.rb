require 'spec_helper'

describe Jenkins::Model do

  it "has a display name which is settable via the class, and accessable via the class and instance" do
    cls = new_model do
      display_name "One-Off Class"
    end
    cls.display_name.should eql "One-Off Class"
    cls.new.display_name.should eql "One-Off Class"
  end
  
  it "passes down display_name capabilities to subclasses" do
    parent = new_model
    child = Class.new(parent)
    child.class_eval do
      display_name "Child"
    end
    child.display_name.should eql "Child"
  end
  
  it "has a default display name of the class name" do
    cls = new_model do
      def self.name
        "AwesomeClass"
      end
    end
    cls.display_name.should eql "AwesomeClass"
  end
  
  it "keeps a record of properties for all readers" do
    cls = new_model do
      attr_reader :foo, :bar, :baz
    end
    cls.properties.should eql [:foo, :bar, :baz]
  end
  
  it "keeps a record of all properties for all accessors" do
    cls = new_model do 
      attr_reader :foo
      attr_accessor :bar, :baz
      attr_reader :bang
    end
    cls.properties.should eql [:foo, :bar, :baz, :bang]
  end

  it "includes parent classes's properties in the property list, but doesn't affect the parent property list" do
    parent = new_model do
      attr_reader :foo
    end
    child = Class.new(parent)
    child.class_eval do
      attr_reader :bar
    end
    parent.properties.should eq([:foo])
    child.properties.should eql([:foo, :bar])
    child.properties(false).should eql([:bar])
  end

  private
  
  def new_model(&block)
    cls = Class.new
    cls.send(:include, Jenkins::Model)
    cls.class_eval(&block) if block_given?
    return cls
  end
end
