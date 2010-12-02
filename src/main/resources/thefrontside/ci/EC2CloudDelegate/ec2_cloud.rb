require 'pp'

class EC2Cloud  < Hudson::Cloud

  display_name "Fog - Amazon EC2"  

  def initialize(region, access_id, secret_key, private_key, instance_cap, templates)
    puts "EC2Cloud#initialize(#{region}, #{access_id}, #{secret_key}, #{private_key}, #{instance_cap}, #{templates})"
    @region = region
    @access_id = access_id
    @secret_key = secret_key
    @private_key = private_key
    @instance_cap = [1,[instance_cap.to_i,30].min].max
    @templates = templates.to_a
  end

  def can_provision?(label)
    puts "can_provision?(#{label})"
    puts " --- > #{@templates.inspect}"
    true
  end

  def provision(label, excess_workload)
    puts "provision(#{label}, #{excess_workload})"
    []
  end
end
