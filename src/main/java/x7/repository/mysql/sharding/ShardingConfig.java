package x7.repository.mysql.sharding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 数据库路由配置, 基于MYSQL的分库分表
 * @author sim
 *
 */
public class ShardingConfig {
	
	public final static String MODE_KEY = "SHARDING_DB_MODE";

	private static ShardingConfig instance;
	
	public static ShardingConfig getInstance(){
		if (instance == null){
			instance = new ShardingConfig();
		}
		return instance;
	}
	
	private String policy;
	private String db;
	private String user;
	private String password;
	private String url;
	private String driver;
	private final List<String> keyTableList = new ArrayList<String>();//SHARDING_TABLE_KEY
	private final List<String> keyDBList = new ArrayList<String>();//SHARDING_DB_KEY
	private final Map<String, Route> map = new HashMap<String, Route>();
	private int keySize;
	private int keyTableSize;
	
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	/**
	 * 不带后缀的名称
	 */
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public int getKeySize() {
		return keySize;
	}
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	public int getKeyTableSize() {
		return keyTableSize;
	}
	public void setKeyTableSize(int keyTableSize) {
		this.keyTableSize = keyTableSize;
	}
	public List<String> getKeyTableList() {
		return keyTableList;
	}
	public List<String> getKeyDBList() {
		return keyDBList;
	}
	public Map<String, Route> getMap() {
		return map;
	}
	
	public String getDbFullName(Route router){
		return db+"_"+router.getKey();
	}
	
	public Route getRouter(String key){
		key = key.toLowerCase();
		return map.get(key);
	}
}
