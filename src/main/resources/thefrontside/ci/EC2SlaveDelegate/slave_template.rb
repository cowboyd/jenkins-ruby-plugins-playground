require 'fog'

class SlaveTemplate
  #include Hudson::Describable

  def initialize(ami, description, remote_fs, remote_admin, flavor, labels)
    @ami = ami
    @description = description
    @remote_fs = remote_fs
    @remote_admin = remote_admin
    @flavor = flavor
    @labels = labels.split
  end

  attr_reader :ami

  def provision(task_listener)
    puts "Launching #{@ami}"
    compute = Fog::Compute.new(:provider => 'AWS',
                               :aws_access_key_id => @access_id,
                               :aws_secret_access_key => @secret_key)

    compute.run_instances(@ami,                      # AMI id
                          1,                         # Min slaves
                          1,                         # Max slaves
                          "InstanceType" => @flavor) # options
    EC2Slave.new @ami, @description, @remote_fs, Fog::AWS::Compute::Flavor.get(@flavor)[:cores], @labels, @remote_admin 
  end

  def attach(instance_id, listener)
    puts "Attaching to #{@ami}"
  end

  def contains_label?(label)
    puts "contains_label(#{label}) @labels=#{@labels}"
    @labels.member? label
  end
end
