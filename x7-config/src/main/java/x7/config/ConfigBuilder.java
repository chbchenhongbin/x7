package x7.config;

import x7.config.ConfigKeeper;
import x7.config.TextParser;
import x7.config.zk.ZkBase;
import x7.core.config.Configs;

public class ConfigBuilder {

	private static ConfigBuilder instance;
	public static ConfigBuilder newInstance(){
		if (instance == null){
			instance = new ConfigBuilder();
		}
		return instance;
	}
	
	public static void build(boolean centralized, String configSpace, String localAddress, String remoteAddress){
		if (instance == null){
			instance = new ConfigBuilder();
			init(centralized, configSpace, localAddress, remoteAddress);
		}
	}
	
	private static void init(boolean centralized, String configSpace, String localAddress, String remoteAddress) {
		
		Configs.setConfigSpace(configSpace);
		
		if (centralized){
			ZkBase.getInstance().init(remoteAddress);
			ZkBase.getInstance().add(ConfigKeeper.getInstance());
		}else{
			TextParser.getInstance().load(localAddress, configSpace);
		}
		

	}
	
}
