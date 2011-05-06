
module Jenkins
  module Model
    def self.included(cls)
      super(cls)
      cls.extend(ClassMethods)
      cls.send(:include, InstanceMethods)
    end
    
    module InstanceMethods
      def display_name
        self.class.display_name
      end
    end
    
    module ClassMethods
      def display_name(name = nil)
        name.nil? ? @display_name || self.name : @display_name = name.to_s
      end

      def attr_reader(*names)
        super(*names)
        add_properties names
      end

      def attr_accessor(*names)
        super(*names)
        add_properties names
      end

      def properties(all = true)
        @properties ||= []
        if all
          total = @properties.dup
          s = self
          while s = s.superclass 
            total = s.properties + total if s < Jenkins::Model
          end
          total
        else
          @properties
        end
      end
      
      def add_properties(names)
        names.each do |name|
          properties(false) << name unless properties(false).member?(name)
        end
      end
    end
  end
end