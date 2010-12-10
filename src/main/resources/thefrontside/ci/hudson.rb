
module Hudson
  class Cloud
    def initialize(name)
        @name = name
    end

    attr_reader :name    

    def check_permission(permission)
        # @todo thunk to java
    end


    # Provisions new {@link Node}s from this cloud.
    #
    # <p>
    # {@link NodeProvisioner} performs a trend analysis on the load,
    # and when it determines that it <b>really</b> needs to bring up
    # additional nodes, this method is invoked.
    #
    # <p>
    # The implementation of this method asynchronously starts
    # node provisioning.
    #
    # @param label
    #      The label that indicates what kind of nodes are needed now.
    #      Newly launched node needs to have this label.
    #      Only those {@link Label}s that this instance returned true
    #      from the {@link #canProvision(Label)} method will be passed here.
    #      This parameter is null if Hudson needs to provision a new {@link Node}
    #      for jobs that don't have any tie to any label.
    # @param excessWorkload
    #      Number of total executors needed to meet the current demand.
    #      Always >= 1. For example, if this is 3, the implementation
    #      should launch 3 slaves with 1 executor each, or 1 slave with
    #      3 executors, etc.
    #
    # @return
    #      {@link PlannedNode}s that represent asynchronous {@link Node}
    #      provisioning operations. Can be empty but must not be null.
    #      {@link NodeProvisioner} will be responsible for adding the resulting {@link Node}
    #      into Hudson via {@link Hudson#addNode(Node)}, so a {@link Cloud} implementation
    #      just needs to create a new node object.
    def provision(label, excess_workload)
      raise "must implement provision()"
    end

    def can_provision?(label)
      raise "must implement can_provision?"
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

  class Slave
    def provision(listener)
      raise "must implement provision()"
    end

    def attach(instanceId, listener)
      raise "must implement attach()"
    end
  end
end