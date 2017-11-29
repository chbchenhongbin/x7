package x7;

import x7.config.ConfigBuilder;

public class ConfigStarter {

	public ConfigStarter(boolean centralized, String configSpace, String localAddress, String remoteAddress){
		
		ConfigBuilder.build(centralized, configSpace, localAddress, remoteAddress);

	}
}
