



class EC2Cloud  < Hudson::Cloud

  display_name "Fog"  

  def initialize(name)
    puts "EC2Cloud#initialize(#{name})"
    @name = name
  end

  def can_provision?(label)
    puts "can_provision?(#{label})"
    true
  end

  def provision(label, excess_workload)
    puts "provision(#{label}, #{excess_workload})"
    []
  end

end
