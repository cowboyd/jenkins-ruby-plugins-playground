# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{named-parameters}
  s.version = "0.0.17"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Juris Galang"]
  s.date = %q{2010-12-07}
  s.description = %q{This gem simulates named-parameters in Ruby. 
    It's a complement to the common Ruby idiom of using Hash args to emulate 
    the use of named parameters. }
  s.email = %q{jurisgalang@gmail.com}
  s.extra_rdoc_files = ["README.md"]
  s.files = [".document", ".rspec", "GPL-LICENSE", "Gemfile", "Gemfile.lock", "MIT-LICENSE", "README.md", "RELEASENOTES", "Rakefile", "VERSION", "lib/named-parameters.rb", "lib/named-parameters/module.rb", "lib/named-parameters/object.rb", "named-parameters.gemspec", "spec/named-parameters_spec.rb", "spec/spec_helper.rb"]
  s.homepage = %q{http://github.com/jurisgalang/named-parameters}
  s.licenses = [["MIT", "GPL"]]
  s.require_paths = ["lib"]
  s.rubygems_version = %q{1.3.6}
  s.summary = %q{Poor man's named-parameters in Ruby}
  s.test_files = ["spec/named-parameters_spec.rb", "spec/spec_helper.rb"]

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 3

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<rspec>, ["~> 2.1.0"])
      s.add_development_dependency(%q<bundler>, ["~> 1.0.0"])
      s.add_development_dependency(%q<jeweler>, ["~> 1.5.1"])
      s.add_development_dependency(%q<rcov>, [">= 0"])
      s.add_development_dependency(%q<yard>, [">= 0"])
    else
      s.add_dependency(%q<rspec>, ["~> 2.1.0"])
      s.add_dependency(%q<bundler>, ["~> 1.0.0"])
      s.add_dependency(%q<jeweler>, ["~> 1.5.1"])
      s.add_dependency(%q<rcov>, [">= 0"])
      s.add_dependency(%q<yard>, [">= 0"])
    end
  else
    s.add_dependency(%q<rspec>, ["~> 2.1.0"])
    s.add_dependency(%q<bundler>, ["~> 1.0.0"])
    s.add_dependency(%q<jeweler>, ["~> 1.5.1"])
    s.add_dependency(%q<rcov>, [">= 0"])
    s.add_dependency(%q<yard>, [">= 0"])
  end
end
