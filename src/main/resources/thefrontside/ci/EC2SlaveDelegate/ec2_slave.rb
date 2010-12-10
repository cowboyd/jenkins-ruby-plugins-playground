class EC2Slave < Hudson::Slave
  def initialize(instance_id, description, remote_fs, num_executors, labels, remote_admin)
    @instance_id = instance_id
    @description = description
    @remote_fs = remote_fs
    @num_executors = num_executors
    @labels = labels
    @remote_admin = remote_admin
  end
end