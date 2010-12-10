class EC2Cloud < Hudson::Cloud

  display_name "Amazon EC2"  

  def initialize(region, access_id, secret_key, private_key, instance_cap, templates)
    super("ec2-" + region)
    puts "EC2Cloud#initialize(#{region}, #{access_id}, #{secret_key}, #{private_key}, #{instance_cap}, #{templates})"
    @region = region
    @access_id = access_id
    @secret_key = secret_key
    @private_key = private_key
    @instance_cap = [1,[instance_cap.to_i,30].min].max
    @templates = templates.to_a
  end

  def get_template(label)
    puts "get_template(#{label})"
    @templates.each do |template|
      return template if template.ami == label
      return template if template.contains_label?(label)
    end

    nil    
  end

  def can_provision?(label)
    puts "can_provision?(#{label})"
    get_template(label) != nil
  end

  def do_provision(ami)
    puts "do_provision(#{ami})"
  end
  
  def provision(label, excess_workload)
    puts "provision(#{label}, #{excess_workload})"

    template = get_template(label)

    results = []
    excess_workload.times do
      break if active_slaves >= @instance_cap
    end
    
    results
  end
end
