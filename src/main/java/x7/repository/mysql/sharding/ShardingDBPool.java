package x7.repository.mysql.sharding;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import x7.core.config.Configs;


/**
 * 多数据源
 * @author sim
 *
 */
public class ShardingDBPool {

	private static Map<String,Pool> poolMap = new ConcurrentHashMap<String,Pool>();
	
	private ShardingDBPool(){
	}
	
	private static ShardingDBPool instance;
	
	public static ShardingDBPool getInstance(){
		if (instance == null){
			instance = new ShardingDBPool();
			init();
		}
		return instance;
	}
	
	public DataSource get(String router){
		return poolMap.get(router).get();
	}
	
	private static void init(){
		String mode = Configs.getString("SHARDING_MODE");
		ShardingMode.get(mode).initConfig();
		initPool();
	}
	
	private static void initPool(){
		Map<String, Route> map = ShardingConfig.getInstance().getMap();
		String url = ShardingConfig.getInstance().getUrl();
		String db = ShardingConfig.getInstance().getDb();
		String user = ShardingConfig.getInstance().getUser();
		String password = ShardingConfig.getInstance().getPassword();
		String driver = ShardingConfig.getInstance().getDriver();
		for (String key : map.keySet()){
			Pool pool = instance.new Pool();
			pool.init(map.get(key), url, db, user, password, driver);
			
			poolMap.put(key, pool);
		}
	}
	
	class Pool{
		private ComboPooledDataSource ds;
		
		public Pool(){
			
		}
		
		private  void init(Route router, String url, String db, String user, String password, String driver){
			ds = new ComboPooledDataSource();  
			try{
				
				url = url.replace("${DB_IP}", router.getIp())
						.replace("${DB_PORT}", String.valueOf(router.getPort()))
						.replace("${DB_NAME}", ShardingConfig.getInstance().getDbFullName(router));
				
		
				
				System.err.println("DB_URL: " + url);
				
				ds.setDriverClass(driver);   
		    	ds.setJdbcUrl(url);    
		    	ds.setUser(user); 
		    	ds.setPassword(password);  
		    	ds.setMaxStatements(6000);    
		    	ds.setMaxPoolSize(20);   
		    	ds.setMinPoolSize(5);
		    	ds.setInitialPoolSize(5);
		    	ds.setAcquireIncrement(1);
		    	ds.setTestConnectionOnCheckin(true);
		    	ds.setIdleConnectionTestPeriod(60);
		    	ds.setCheckoutTimeout(3000);
		    	ds.setBreakAfterAcquireFailure(false);
		    	ds.setNumHelperThreads(5);
		    	
		    	
			}catch (Exception e){
				e.printStackTrace();
			}
			
		}
		
		public DataSource get(){
			return ds;
		}
	}
	
	
	
}
