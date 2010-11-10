
module Hudson
  class Cloud

    def can_provision?(label)
      raise "must implement can_provision?"
    end

    def provision(label, excess_workload)
      raise "must implement provision()"
    end

    def self.inherited(cls)
      cls.extend ClassMethods
    end

    module ClassMethods
      def display_name(name = nil)
        @display_name = name.to_s unless name.nil?
        return @display_name
      end
    end

  end
end