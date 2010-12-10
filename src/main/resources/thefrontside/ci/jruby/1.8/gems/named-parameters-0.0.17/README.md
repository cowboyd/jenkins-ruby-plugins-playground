This gem simulates named-parameters in Ruby. It's a complement to the common 
Ruby idiom of using `Hash` args to emulate the use of named parameters. 

Related: [Named Parameters in Ruby](http://www.jurisgalang.com/2010/11/09/named-parameters-in-ruby/)

Get It
------
You know you want it:

    gem install named-parameters

Use It
------
Make it available everywhere:

    require 'named-parameters'   
    
But if you want to be selective, do:
    
    require 'named-parameters/module'

Then include the `NamedParameters` module into your class:

    class YourClass
      include NamedParameters
    end
    
Either way, you would now be able to  use the **`has_named_parameters`** clause 
as needed:

    class YourClass
      has_named_parameters :your_method, :require => :param
      def your_method options
        puts options.inspect
      end
    end

So when you invoke `your_method`, its parameter requirements will now be
enforced:

    obj = YourClass.new
    obj.your_method :param => 'Et tu, Babe?'  # will spit out: 'Et tu, Babe?'
    obj.your_method                           # will raise an ArgumentError because the required :param was not specified
        
Abuse It
--------
Declare required parameters:

    has_named_parameters :send_mail, :required => :to
    has_named_parameters :send_mail, :required => [ :to, :subject ]
    
Declare optional parameters:

    has_named_parameters :send_mail, :optional => :subject
    has_named_parameters :send_mail, :optional => [ :subject, :bcc, :from ]
    
Declare one of a set of parameters as required (ie: require one and only
one from a list):

    has_named_parameters :send_mail, :oneof => [ :signature, :alias ]
    
Declare default values for optional parameters:
    
    has_named_parameters :send_mail, :optional => [ :subject, :bcc, { :from => 'yourself@example.org' } ]
    has_named_parameters :send_mail, :optional => [ :subject, :bcc, [ :from, 'yourself@example.org' ] ]

You can also declare default values for `:required` and `:oneof` parameters, 
but really, that's just silly.

With `has_named_parameters`, you can mix-and-match parameter requirements:

    has_named_parameters :send_mail, 
      :required => [ :to, :subject, ],
      :oneof    => [ :signature, :alias ],
      :optional => [ :subject, :bcc, [ :from, 'yourself@example.org' ] ]

And is applicable to both class and instance methods:

    require 'named-parameters'
    
    class Mailer
      has_named_parameters :send_mail, 
        :required => [ :to, :subject, ],
        :oneof    => [ :signature, :alias ],
        :optional => [ :subject, :bcc, [ :from, 'yourself@example.org' ] ]
      def send_mail options
        # ... do send mail stuff here ...
      end
      
      has_named_parameters :'self.archive', :optional => [ :method => 'zip' ]
      def self.archive options = { }
        # ... do mail archiving stuff here ...
      end
    end

Shortcuts
---------
In addition to the `has_named_parameters` method, `NamedParameters` also comes
with two convenience methods for implicitly applying parameter specs for 
constructors.

Use the `requires` clause to declare what parameters a class expects when it
is instantiated:

    class GoogleStorage
      requires :'access-key', :'secret-key'
      
      def initialize options
        # ... do googly stuff here ...
      end
    end

Use the `recognizes` clause to specify the optional parameters for a class
when it is instantiated:

    class GoogleStorage
      recognizes :'group-email', :'apps-domain'
      
      def initialize options
        # ... do googly stuff here ...
      end
    end

You may also specify default values for parameters when using these clauses:

    class GoogleStorage
      requires   :'access-key', :'secret-key'
      recognizes [ :'group-email', 'group@example.org' ], [ :'apps-domain', 'example.org' ]

      def initialize options
        # ... do googly stuff here ...
      end
    end

The `requires` and `recognizes` clause is equivalent to declaring the lines
in a class definition:

    has_named_parameters :'self.new', :required => params, :strict
    has_named_parameters :initialize, :required => params, :strict
    has_named_parameters :'self.new', :optional => params, :strict
    has_named_parameters :initialize, :optional => params, :strict

What Was Declared?
------------------
You can get a list of declared parameters via the `declared_parameters` 
method:

    class GoogleStorage
      requires   :'access-key', :'secret-key'
      recognizes [ :'group-email', 'group@example.org' ], [ :'apps-domain', 'example.org' ]

      def initialize options
        # list the parameters declared
        puts "#{declared_parameters.join(' ')}"
        
        # ... now do the googly stuff ...
      end
    end
    
    # create an instance of GoogleStorage
    # and print: [ :access-key, :secret-key, :group-email, :apps-domain ]
    GoogleStorage.new :'access-key' => '...', :'secret-key' => '...'

`declared_parameters` is also available from the class methods of user defined
classes.

You can also pass a list of parameter types to limit the result to specific 
parameter types:

    class GoogleStorage
      requires   :'access-key', :'secret-key'
      recognizes [ :'group-email', 'group@example.org' ], [ :'apps-domain', 'example.org' ]

      def initialize options
        # list the parameters declared
        puts "#{declared_parameters(:required).join(' ')}"
    
        # ... now do the googly stuff ...
      end
    end

    # create an instance of GoogleStorage
    # and print: [ :access-key, :secret-key ]
    GoogleStorage.new :'access-key' => '...', :'secret-key' => '...'

The method `declared_parameters` is context specific. It returns the list of 
parameters for the current method. To get a list of parameters for a specific
method, use `declared_parameters_for`:

    class GoogleStorage
      requires   :'access-key', :'secret-key'
      recognizes [ :'group-email', 'group@example.org' ], [ :'apps-domain', 'example.org' ]

      def initialize options
        # list the parameters declared
        puts "#{declared_parameters(:required).join(' ')}"

        # ... now do the googly stuff ...
      end
      
      def self.required_parameters
        declared_parameters_for :new, :required
      end

      def self.optional_parameters
        declared_parameters_for :new, :optional
      end

      def self.all_parameters
        declared_parameters_for :new
      end
    end

    # list the required parameters for the class
    GoogleStorage.required_parameters  # => [ :access-key, :secret-key ]

    # list the optional parameters for the class
    GoogleStorage.required_parameters  # => [ :group-email, :apps-domain ]

    # list all of the recognized parameters for the class
    GoogleStorage.all_parameters  # => [ :access-key, :secret-key, :group-email, :apps-domain ]
    
Notice that both methods may receive a filter of parameter types.

Filtering Arguments
-------------------
Sometimes you'll have a `Hash` object that will have a bunch of keys that may 
or may not comply to the parameter declaration of a method, but you may to use 
the same object everywhere without having to figure out what keys are 
applicable to a method call:

    options = { :x => 1, :y => :z, :a => 'somevalue' }

    has_named_parameters :foo, :required => [ :x ]
    def foo options = { }
      options.inspect
    end

    has_named_parameters :bar, :required => [ :y ], :optional => [ :a ]
    def bar options = { }
      options.inspect
    end
    
    foo options   # => ArgumentError! :y and :a not recognized
    bar options   # => ArgumentError! :x not recognized
    
Use the `filter_parameters` method against the options object to make it 
comply to the declaration:

    foo filter_parameters(options)   # => [ :x ]
    bar filter_parameters(options)   # => [ :a, :y ]

Permissive Mode
---------------
When a method is declared with `has_named_parameters` that method will only 
accept keys that were listed as `:required`, `:optional`, or `:oneof` - 
passing any other key to the `Hash` arg will raise an `ArgumentError` on
method call:

    has_named_parameters :exec, :required => :w, :optional => [ :x, :y ]
    def exec opts 
      # ...
    end
    
    # the following will raise an ArgumentError since :z was not declared
    exec :w => 1, :x => 2, :y => 3, :z => 4

But sometimes you need to be able to pass additional keys and you don't know 
what those keys are. Setting the optional `mode` parameter for 
`has_named_parameters` to `:permissive` will relax this restriction:

    has_named_parameters :exec, { :required => :w, :optional => [ :x, :y ] }, :permissive
    def exec opts 
      # ...
    end

    # the following will no longer raise an ArgumentError
    exec :w => 1, :x => 2, :y => 3, :z => 4

The `:required` and `:oneof` parameters will still be expected:

    # the following will still raise an ArgumentError since :w is required
    exec :x => 2, :y => 3, :z => 4

For clarity you should skip the `:optional` parameters list altogether when 
using the `:permissive` mode.

However, the `requires` and `recognizes` clauses does not accept the `mode` argument. 
If you need to make a constructor's optional parameter spec permissive, use
the `has_named_parameters` clause instead:

    has_named_parameters :'self.new', :required => params, :permissive
    has_named_parameters :initialize, :required => params, :permissive

For brevity, since the mode is `:permissive`, the `:optional` parameters list 
is skipped.

How It Works
------------
When the `has_named_parameters` is declared in a class, it instruments the 
class so that when the method in the declaration is invoked, a validation is 
performed on the last `Hash` argument that was received by the method.

It expects that the last argument is the the `Hash` args representing the 
named parameters when a method is invoked. If no `Hash` args was supplied
then it creates one.

So you can mix-and-match argument types in a method, and still declare that
it `has_named_parameters`:

    has_named_parameters :request, 
      :required => :key, 
      :optional => [ :etc, 'howl' ]
    def request path, options
      "path: #{path}, options: #{options.inspect}"
    end
    
    # invocation:
    request "/xxx", :key => '0925'  
    
    # result:
    # => path: /xxx, options: {:key => '0925', :etc => 'howl'}

Gotchas
-------    
It has no idea if the last argument really is the last argument. So be careful 
when you have something similar to the following:

    has_named_parameters :request, :optional => :key
    def request path = "/xxx", options = { }
      "path: #{path}, options: #{options.inspect}"
    end

    # invocation:
    request :key => '0925'  
    
    # expecting:
    # => path: /xxx, options: {:key => '0925'}
    
    # but actual result is:
    # => path: {:accesskey => '0925'}, options: {}

For the above case, it might be better to refactor:

    has_named_parameters :request, :optional => [ :key, [ :path, "/xxx" ] ]
    def request options = { }
      "path: #{options.delete :path}, options: #{options.inspect}"
    end

    # invocation:
    request :key => '0925'  

    # result:
    # => path: /xxx, options: {:key => '0925'}

    # invocation:
    request

    # result:
    # => path: /xxx, options: {}
    
Class vs Instance Methods
-------------------------
Parameter spec declarations are not shared between class and instance 
methods even if they share the same name. 

For example, the following `has_named_parameters` declaration below is only 
applicable to the instance method `exec`:

    class Command
      has_named_parameters :exec, :required => :x
      def self.exec opts
        # ...
      end

      def exec opts
        # ...
      end
    end

    # the following will *not* raise an ArgumentError because
    # the has_named_parameter declaration applies only to the
    # instance method exec...
    Command.exec      

    # the following will raise an ArgumentError (as expected)
    command = Command.new
    command.exec  

Prefix the method name with `self.` to apply parameter spec for class methods:

    class Command
      has_named_parameters :'self.exec', :required => :x
      def self.exec opts
        # ...
      end
    end

    # the following will now raise an ArgumentError (as expected)
    Command.exec

In general, however, when a class has an instance and a class method using
the same name, for most cases, one simply delegates to another and will share 
the same requirements. So the examples cited above can be refactored:

    class Command
      has_named_parameters :'self.exec', :required => :x
      def self.exec opts
        # ...
      end

      def exec opts
        Command.exec
      end
    end

    # the following will raise an ArgumentError (as expected)
    Command.exec

    # the following will also raise an ArgumentError as it delegates to the 
    # class method and violates the parameter requirements
    command = Command.new
    command.exec  

Dependencies
------------
Development:

* `yard >= 0`
* `rspec >= 1.2.9`

Download
--------
You can download this project in either
[zip](http://github.com/jurisgalang/named-parameters/zipball/master) or
[tar](http://github.com/jurisgalang/named-parameters/tarball/master") formats.

You can also clone the project with [Git](http://git-scm.com)
by running: 

    git clone git://github.com/jurisgalang/named-parameters

Note on Patches/Pull Requests
-----------------------------
* Fork the project.
* Make your feature addition or bug fix.
* Add tests for it. This is important so I don't break it in a future version 
  unintentionally.
* Commit, do not mess with rakefile, version, or history. (if you want to have 
  your own version, that is fine but bump version in a commit by itself I can 
  ignore when I pull)
* Send me a pull request. Bonus points for topic branches.

Releases
--------
Please read the `RELEASENOTES` file for the details of each release. 

In general: patch releases will not include breaking changes while releases 
that bumps the major or minor components of the version string may or may not 
include breaking changes.

Author
------
[Juris Galang](http://github.com/jurisgalang/)

License
-------
Dual licensed under the MIT or GPL Version 2 licenses.  
See the MIT-LICENSE and GPL-LICENSE files for details.

Copyright (c) 2010 Juris Galang. All Rights Reserved.
