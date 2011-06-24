

class FogCloud < Jenkins::Slaves::Cloud

  attr_reader :name, :aws_access_id, :aws_secret_key

  display_name "Fog"

  def initialize
    puts "FogCloud#initialize: #{self.inspect}"
  end

end

