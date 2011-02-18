
require 'hudson/plugin/cloud'

class FogCloud < Hudson::Plugin::Cloud

  attr_reader :name, :aws_access_id, :aws_secret_key

  def self.display_name
    "Fog"
  end

  def initialize
    puts "FogCloud#initialize: #{self.inspect}"
  end

end

