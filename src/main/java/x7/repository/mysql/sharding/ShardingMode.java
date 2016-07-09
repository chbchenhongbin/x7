package x7.repository.mysql.sharding;

import java.util.ArrayList;
import java.util.List;

import x7.core.config.Configs;


public enum ShardingMode {

	/**
	 * 开发单服模式
	 */
	SINGLE {
		@Override
		public void initConfig() {

			ShardingConfig config = ShardingConfig.getInstance();
			
			String policy = Configs.getString("SHARDING_DB_POLICY");
			String keyStr = Configs.getString("SHARDING_DB_KEY");
			String keyTableStr = Configs.getString("SHARDING_TABLE_KEY");
			String ipStr = Configs.getString("SHARDING_DB_IP");
			int port = Configs.getIntValue("SHARDING_DB_PORT");
			String db = Configs.getString("SHARDING_DB_NAME");
			String user = Configs.getString("SHARDING_DB_USER");
			String password = Configs.getString("SHARDING_DB_PASSWORD");
			String url = Configs.getString("SHARDING_DB_URL");
			String driver = Configs.getString("SHARDING_DB_DRIVER");
			/*
			 * 同一IP,同样端口
			 */
			List<String> ipList = new ArrayList<String>();
			String[] ipArr = ipStr.split(",");
			for (String ip : ipArr){
				ipList.add(ip.trim());
			}
			String ip = ipList.get(0);
			
			config.setPolicy(policy);
			config.setDb(db);//不带后缀的名称
			config.setUser(user);
			config.setPassword(password);
			config.setUrl(url);
			config.setDriver(driver);
			

			String[] keyArr = keyStr.split(",");
			for (String key : keyArr){
				config.getKeyDBList().add(key.trim());
			}

			
			/*
			 * 初始化rooter
			 */
			for (String key : config.getKeyDBList()){
				Route route = new Route();
				route.setKey(key);
				route.setIp(ip);
				route.setPort(port);

				config.getMap().put(key, route);
			}
			
			/*
			 * keyTable
			 */
			String[] keyTableArr = keyTableStr.split(",");
			for (String key : keyTableArr){
				config.getKeyTableList().add(key.trim());
			}
			
			config.setKeySize(config.getKeyDBList().size());
			config.setKeyTableSize(config.getKeyTableList().size());
		
		}
	},
	/**
	 * 部署集群模式<BR>
	 * 配置要求，KEY和IP要一一对应
	 */
	CLUSTER {
		@Override
		public void initConfig() {
			
			ShardingConfig config = ShardingConfig.getInstance();
			
			String policy = Configs.getString("SHARDING_DB_POLICY");
			String keyStr = Configs.getString("SHARDING_DB_KEY");
			String keyTableStr = Configs.getString("SHARDING_TABLE_KEY");
			String ipStr = Configs.getString("SHARDING_DB_IP");
			int port = Configs.getIntValue("SHARDING_DB_PORT");
			String db = Configs.getString("SHARDING_DB_NAME");
			String user = Configs.getString("SHARDING_DB_USER");
			String password = Configs.getString("SHARDING_DB_PASSWORD");
			String url = Configs.getString("SHARDING_DB_URL");
			String driver = Configs.getString("SHARDING_DB_DRIVER");
			
			config.setPolicy(policy);
			config.setDb(db);//不带后缀的名称
			config.setUser(user);
			config.setPassword(password);
			config.setUrl(url);
			config.setDriver(driver);
			/*
			 * 不同IP,同样端口
			 */
			List<String> ipList = new ArrayList<String>();
			String[] ipArr = ipStr.split(",");
			for (String ip : ipArr){
				ipList.add(ip.trim());
			}
			

			String[] keyArr = keyStr.split(",");
			for (String key : keyArr){
				config.getKeyDBList().add(key.trim());
			}
			
			/*
			 * 初始化rooter
			 */
			int size = config.getKeyDBList().size();
			
			if (size != ipList.size()){
				throw new RuntimeException("SHARDING DB EXCEPTION: KEY LIST SIZE != IP LIST SIZE");
			}
			
			for (int i=0; i<size; i++){
				String key = config.getKeyDBList().get(i);
				Route route = new Route();
				route.setKey(key);
				route.setIp(ipList.get(i));
				route.setPort(port);

				config.getMap().put(key, route);
			}
			
			/*
			 * keyTable
			 */
			String[] keyTableArr = keyTableStr.split(",");
			for (String key : keyTableArr){
				config.getKeyTableList().add(key.trim());
			}
			
			config.setKeySize(config.getKeyDBList().size());
			config.setKeyTableSize(config.getKeyTableList().size());
		}
	};
	/**
	 * 初始化
	 */
	public abstract void initConfig();
	
	public static ShardingMode get(String key){
		if (key == null || key.equals(""))
			return SINGLE;
		for (ShardingMode value : values()){
			if (value.toString().equals(key)){
				return value;
			}
		}
		return SINGLE;
	}
}
