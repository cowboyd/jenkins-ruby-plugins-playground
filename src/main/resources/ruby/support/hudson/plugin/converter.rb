require 'yaml'

module Hudson
  module Plugin
    class Converter < Java::Ruby::RubyXStreamConverter

      def initialize(plugin)
        super()
        @plugin = plugin
      end

      def marshal(source, writer, context)
        puts "marshal:(#{source.inspect})"
	    writer.setValue("\n" + source.unwrap.to_yaml)
	  end

	  def unmarshal(reader, context)
        puts "unmarshal()"
        @plugin.export YAML.load(reader.getValue())
      end

	end
  end
end