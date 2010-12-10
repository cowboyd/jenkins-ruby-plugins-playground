require File.expand_path(File.dirname(__FILE__) + '/spec_helper')

# TODO: reorganize the tests for clarity
# TODO: need separate spec for the NamedParameters.validate_specs method
# TODO: factor out specs for required, recognizes, and has_named_parameters
describe "NamedParameters" do
  before :all do
    class Foo
      has_named_parameters :initialize, :required => :x, :optional => [ :y, :z ]
      def initialize opts = {}; end
      
      has_named_parameters :method_one, :required => :x, :optional => [ :y, :z ]
      def method_one x, y, opts = {}; end
      
      def method_two x, y, opts = {}; end
      
      has_named_parameters :'self.method_three', :required => :x, :optional => [ :y, :z ]
      def self.method_three x, y, opts = {}; end

      def self.method_four x, y, opts = {}; end
    end

    class Bar
      has_named_parameters :method_with_one_required, :required => :x
      def method_with_one_required opts = {}; end
      
      has_named_parameters :method_with_many_required, :required => [ :x, :y ]
      def method_with_many_required opts = {}; end
      
      has_named_parameters :method_with_one_oneof, :oneof => :x
      def method_with_one_oneof opts = {}; end
      
      has_named_parameters :method_with_many_oneof, :oneof => [ :x, :y ]
      def method_with_many_oneof opts = {}; end
      
      has_named_parameters :method_with_one_optional, :optional => :x
      def method_with_one_optional opts = {}; end
      
      has_named_parameters :method_with_many_optional, :optional => [ :x, :y ]
      def method_with_many_optional opts = {}; end
    
      has_named_parameters :method_with_one_of_each_requirement, :required => :w, :oneof => [ :x, :y ], :optional => :z
      def method_with_one_of_each_requirement opts = {}; end
    end
  end
  
  it "should allow declaration of has_named_parameters" do
    Foo.should respond_to :has_named_parameters
  end
  
  it "should enforce named parameters for constructor" do
    lambda{ Foo.new }.should raise_error ArgumentError
    lambda{ Foo.new :w => :w }.should raise_error ArgumentError
    lambda{ Foo.new :x => :x }.should_not raise_error
    lambda{ Foo.new :x => :x, :y => :y }.should_not raise_error
    lambda{ Foo.new :x => :x, :y => :y, :z => :z }.should_not raise_error
  end
  
  it "should enforce named parameters for instrumented instance methods" do
    lambda{ @foo = Foo.new :x => :x, :y => :y, :z => :z }.should_not raise_error
    lambda{ @foo.method_one :x }.should raise_error ArgumentError
    lambda{ @foo.method_one :x, :y }.should raise_error ArgumentError
    lambda{ @foo.method_one :x, :y, :x => :x, :y => :y, :z => :z, :w => :w }.should raise_error ArgumentError
    lambda{ @foo.method_one :x => :x, :y => :y, :z => :z }.should raise_error ArgumentError
    lambda{ @foo.method_one :x, :y, :w => :w }.should raise_error ArgumentError
    lambda{ @foo.method_one :x, :y, :x => :x }.should_not raise_error
    lambda{ @foo.method_one :x, :y, :x => :x, :y => :y }.should_not raise_error
    lambda{ @foo.method_one :x, :y, :x => :x, :y => :y, :z => :z }.should_not raise_error
  end
  
  it "should not enforce named parameters for un-instrumented instance methods" do
    lambda{ @foo = Foo.new :x => :x, :y => :y, :z => :z }.should_not raise_error
    lambda{ @foo.method_two :x }.should raise_error ArgumentError
    lambda{ @foo.method_two :x, :y }.should_not raise_error ArgumentError
    lambda{ @foo.method_two :x, :y, :w => :w }.should_not raise_error ArgumentError
  end

  it "should enforce named parameters for instrumented class methods" do
    lambda{ Foo.method_three :x }.should raise_error ArgumentError
    lambda{ Foo.method_three :x, :y }.should raise_error ArgumentError
    lambda{ Foo.method_three :x, :y, :x => :x, :y => :y, :z => :z, :w => :w }.should raise_error ArgumentError
    lambda{ Foo.method_three :x => :x, :y => :y, :z => :z }.should raise_error ArgumentError
    lambda{ Foo.method_three :x, :y, :w => :w }.should raise_error ArgumentError
    lambda{ Foo.method_three :x, :y, :x => :x }.should_not raise_error
    lambda{ Foo.method_three :x, :y, :x => :x, :y => :y }.should_not raise_error
    lambda{ Foo.method_three :x, :y, :x => :x, :y => :y, :z => :z }.should_not raise_error
  end

  it "should not enforce named parameters for un-instrumented class methods" do
    lambda{ Foo.method_four :x }.should raise_error ArgumentError
    lambda{ Foo.method_four :x, :y }.should_not raise_error ArgumentError
    lambda{ Foo.method_four :x, :y, :w => :w }.should_not raise_error ArgumentError
  end
  
  it "should require all :required parameters" do
    bar = Bar.new
    lambda{ bar.method_with_one_required }.should raise_error ArgumentError
    lambda{ bar.method_with_one_required :a => :a }.should raise_error ArgumentError
    lambda{ bar.method_with_one_required :x => :x }.should_not raise_error
            
    lambda{ bar.method_with_many_required }.should raise_error ArgumentError
    lambda{ bar.method_with_many_required :x => :x }.should raise_error ArgumentError
    lambda{ bar.method_with_many_required :x => :x, :y => :y }.should_not raise_error
  end
  
  it "should require one and only one of :oneof parameters" do
    bar = Bar.new
    lambda{ bar.method_with_one_oneof }.should raise_error ArgumentError
    lambda{ bar.method_with_one_oneof :a => :a }.should raise_error ArgumentError
    lambda{ bar.method_with_one_oneof :x => :x }.should_not raise_error
            
    lambda{ bar.method_with_many_oneof }.should raise_error ArgumentError
    lambda{ bar.method_with_many_oneof :a => :a }.should raise_error ArgumentError
    lambda{ bar.method_with_many_oneof :x => :x }.should_not raise_error
    lambda{ bar.method_with_many_oneof :y => :y }.should_not raise_error
    lambda{ bar.method_with_many_oneof :x => :x, :y => :y }.should raise_error ArgumentError
  end
  
  it "should reject parameters not declared in :required, :optional, or :oneof" do
    bar = Bar.new
    lambda{ bar.method_with_one_optional }.should_not raise_error
    lambda{ bar.method_with_one_optional :x => :x }.should_not raise_error
    lambda{ bar.method_with_one_optional :a => :a }.should raise_error ArgumentError
    lambda{ bar.method_with_one_optional :x => :x, :y => :y }.should raise_error ArgumentError
            
    lambda{ bar.method_with_many_optional }.should_not raise_error
    lambda{ bar.method_with_many_optional :x => :x }.should_not raise_error
    lambda{ bar.method_with_many_optional :y => :y }.should_not raise_error
    lambda{ bar.method_with_many_optional :x => :x, :y => :y }.should_not raise_error
    lambda{ bar.method_with_many_optional :x => :x, :y => :y, :z => :z }.should raise_error ArgumentError
            
    lambda{ bar.method_with_one_of_each_requirement }.should raise_error ArgumentError
    lambda{ bar.method_with_one_of_each_requirement :w => :w }.should raise_error ArgumentError
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :x => :x }.should_not raise_error
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :y => :y }.should_not raise_error
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :x => :x, :y => :y }.should raise_error ArgumentError
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :x => :x, :z => :z }.should_not raise_error
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :y => :y, :z => :z }.should_not raise_error
    lambda{ bar.method_with_one_of_each_requirement :w => :w, :x => :x, :z => :z, :a => :a }.should raise_error ArgumentError
  end
  
  it "should be able to supply the default values for optional parameters" do
    class Zoo
      has_named_parameters :method_with_one_optional_parameter, :optional => { :x => 1 }
      def method_with_one_optional_parameter opts = { }; opts[:x]; end
  
      has_named_parameters :method_with_many_optional_parameters, :optional => [ [ :x, 1 ], [ :y, 2 ] ]
      def method_with_many_optional_parameters opts = { }; opts[:x] + opts[:y]; end
  
      has_named_parameters :method_with_many_optional_parameters_too, :optional => [ { :x => 1 }, { :y => 2 } ]
      def method_with_many_optional_parameters_too opts = { }; opts[:x] + opts[:y]; end
    end
    
    zoo = Zoo.new
    zoo.method_with_one_optional_parameter.should eql 1
    zoo.method_with_one_optional_parameter(:x => 2).should eql 2
  
    zoo.method_with_many_optional_parameters.should eql 3
    zoo.method_with_many_optional_parameters(:x => 2).should eql 4
    zoo.method_with_many_optional_parameters(:x => 2, :y => 3).should eql 5
  
    zoo.method_with_many_optional_parameters_too.should eql 3
    zoo.method_with_many_optional_parameters_too(:x => 2).should eql 4
    zoo.method_with_many_optional_parameters_too(:x => 2, :y => 3).should eql 5
  end
  
  it "should be able to instrument the class method new" do
    class Quux
      has_named_parameters :'self.new', :required => :x
      class << self
        def new opts = { }; end
      end
      def initialize opts = { }; end
    end
    lambda { Quux.new }.should raise_error ArgumentError
    lambda { Quux.new :y => :y }.should raise_error ArgumentError
    lambda { Quux.new :x => :x, :y => :y }.should raise_error ArgumentError
    lambda { Quux.new :x => :x }.should_not raise_error
  end
  
  it "should be able to specify optional parameters using the recognizes method" do
    class Recognizes
      recognizes :x, :y
      def self.new opts = { }; end
      def initialize opts = { }; end
    end
    lambda { Recognizes.new }.should_not raise_error
    lambda { Recognizes.new :x => :x }.should_not raise_error
    lambda { Recognizes.new :y => :y }.should_not raise_error
    lambda { Recognizes.new :x => :x, :y => :y }.should_not raise_error
    lambda { Recognizes.new :z => :z }.should raise_error ArgumentError
  end
  
  it "should be able to specify required parameters using the recognizes method" do
    class Required
      requires :x, :y
      def self.new opts = { }; end
      def initialize opts = { }; end
    end
    lambda { Required.new }.should raise_error ArgumentError
    lambda { Required.new :x => :x }.should raise_error ArgumentError
    lambda { Required.new :y => :y }.should raise_error ArgumentError
    lambda { Required.new :x => :x, :y => :y }.should_not raise_error
  end
  
  it "should be able to list of recognized parameters" do
    class DeclaredParameters
      requires   :x, :y
      recognizes :a, :b, :c
      attr :parameters
      
      def initialize opts = { }
        @parameters = declared_parameters
      end
      
      has_named_parameters :'self.singleton', 
        :required => [ :w ], 
        :optional => [ :x, [ :y, 1 ], { :z => 1 } ],
        :oneof    => [ :a, :b, :c ]
      def self.singleton opts = { }
        declared_parameters
      end
    end
    
    o = DeclaredParameters.new(:x => :x, :y => :y)
    o.parameters.should eql [ :a, :b, :c, :x, :y ]
    DeclaredParameters.singleton(:w => :w, :a => :a).should eql [ :a, :b, :c, :w, :x, :y, :z ]
  end

  it "should not return nil when declared_parameters is called on uninstrumented method" do
    class DeclaredParameters
      has_named_parameters :'self.boogey', 
        :required => [ :w ], 
        :optional => [ :x, [ :y, 1 ], { :z => 1 } ],
        :oneof    => [ :a, :b, :c ]
      def self.boogey opts = { }
        declared_parameters
      end
      
      def boogey
        declared_parameters
      end
    end
    
    o = DeclaredParameters.new(:x => :x, :y => :y)
    o.boogey.should eql []
    DeclaredParameters.boogey(:w => :w, :a => :a).should eql [ :a, :b, :c, :w, :x, :y, :z ]
  end
  
  it "should be able to get the list of declared parameters for specified methods" do
    class DeclaredParameters
      has_named_parameters :'instance_method', 
        :required => [ :w ], 
        :optional => [ :x, [ :y, 1 ], { :z => 1 } ],
        :oneof    => [ :a, :b, :c ]
      def instance_method opts = { }
      end
      
      has_named_parameters :'self.singleton_method', 
        :required => [ :w ], 
        :optional => [ :x, [ :y, 1 ], { :z => 1 } ],
        :oneof    => [ :a, :b, :c ]
      def self.singleton_method opts = { }
      end
      
      def instance_method_parameters
        declared_parameters_for :instance_method
      end
      
      def singleton_method_parameters
        declared_parameters_for :'self.singleton_method'
      end
      
      class << self
        def instance_method_parameters
          declared_parameters_for :instance_method
        end
        
        def singleton_method_parameters
          declared_parameters_for :'self.singleton_method'  
        end
      end
    end
    o = DeclaredParameters.new(:x => 1, :y => 1)
    DeclaredParameters.singleton_method_parameters.should eql [ :a, :b, :c, :w, :x, :y, :z ]
    DeclaredParameters.instance_method_parameters.should eql [ :a, :b, :c, :w, :x, :y, :z ]
    o.instance_method_parameters.should eql [ :a, :b, :c, :w, :x, :y, :z ]
    o.singleton_method_parameters.should eql [ :a, :b, :c, :w, :x, :y, :z ]
  end
end
