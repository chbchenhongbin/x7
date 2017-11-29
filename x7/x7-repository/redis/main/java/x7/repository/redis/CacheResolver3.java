package x7.repository.redis;

import x7.core.config.Configs;
import x7.core.util.VerifyUtil;

/**
 * 缓存解决，三级缓存
 * @author sim
 *
 */
public class CacheResolver3 {

	public final static String NANO_SECOND = ".N_S";
	
	private static CacheResolver3 instance = null;
	public static CacheResolver3 getInstance(){
		if (instance == null){
			instance = new CacheResolver3();
		}
		return instance;
	}
	
	/**
	 * 标记缓存要更新
	 * @param clz
	 * @return nanuTime_String
	 */
	@SuppressWarnings("rawtypes")
	public String markForRefresh(Class clz){
		
		String key = getNSKey(clz);
		String time = String.valueOf(System.nanoTime());
		JedisConnector_Cache3.getInstance().set(key.getBytes(), time.getBytes());
		return time;
	}
	
	
	/**
	 * 简单的，低效的缓存结果<br>
	 * 高效请调用setResultKeyList()方法， FIXME
	 * @param clz
	 * @param condition
	 * @param obj
	 */
	@SuppressWarnings("rawtypes")
	public void setResult(Class clz, String condition, Object obj){
		String key = getKey(clz, condition);
		System.out.println("save key: " + key);
		int validSecond = Configs.getIntValue("x7.cache.second") / 2;
		JedisConnector_Cache3.getInstance().set(key.getBytes(), ObjectUtil.toBytes(obj), validSecond);
	}
	
	/**
	 * 简单的，低效的获取缓存结果<br>
	 * 高效请调用getResultKeyList()方法， FIXME
	 * @param clz
	 * @param condition
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Object getResult(Class clz, String condition){
		String key = getKey(clz, condition);
		System.out.println("get key: " + key);
		byte[] bytes = JedisConnector_Cache3.getInstance().get(key.getBytes());
		
		if (bytes == null)
			return null;
		
		return ObjectUtil.toObject(bytes);

	}
	
	
	@SuppressWarnings("rawtypes")
	private String getNSKey(Class clz){
		return clz.getName()+"_"+ NANO_SECOND;
	}
	

	
	@SuppressWarnings("rawtypes")
	private String getKey(Class clz, String condition){
		return VerifyUtil.toMD5(getPrefix(clz) + condition);
	}

	
	/**
	 * 获取缓存KEY前缀
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getPrefix(Class clz){
		String key = getNSKey(clz);
		byte[] nsArr = JedisConnector_Cache3.getInstance().get(key.getBytes());
		if (nsArr == null){
			String str = markForRefresh(clz);
			return clz.getName() + str;
		}
		return clz.getName() + new String(nsArr);
	}


/////////// 以下是联合主键类专用的三级缓存， 用于缓存keyOne相关的/////////////////

	
	/**
	 * 标记缓存要更新
	 * @param clz
	 * @return nanuTime_String
	 */
	@SuppressWarnings("rawtypes")
	public String markForRefresh(Class clz, long idOne){
		
		markForRefresh(clz);
		
		String key = getNSKey(clz, idOne);
		String time = String.valueOf(System.nanoTime());
		JedisConnector_Cache3.getInstance().set(key.getBytes(), time.getBytes());
		return time;
	}
	
	
	/**
	 * 简单的，低效的缓存结果<br>
	 * 高效请调用setResultKeyList()方法， FIXME
	 * @param clz
	 * @param condition
	 * @param obj
	 */
	@SuppressWarnings("rawtypes")
	public void setResult(Class clz, long idOne, String condition, Object obj){
		String key = getKey(clz, idOne, condition);
		System.out.println("save key: " + key);
		int validSecond = Configs.getIntValue("x7.cache.second");
		JedisConnector_Cache3.getInstance().set(key.getBytes(), ObjectUtil.toBytes(obj), validSecond);
	}
	
	/**
	 * 简单的，低效的获取缓存结果<br>
	 * 高效请调用getResultKeyList()方法， FIXME
	 * @param clz
	 * @param condition
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Object getResult(Class clz, long idOne, String condition){
		String key = getKey(clz, idOne, condition);
		System.out.println("get key: " + key);
		byte[] bytes = JedisConnector_Cache3.getInstance().get(key.getBytes());
		
		if (bytes == null)
			return null;
		
		return ObjectUtil.toObject(bytes);

	}
	
	
	@SuppressWarnings("rawtypes")
	private String getNSKey(Class clz, long idOne){
		return clz.getName()+"_"+idOne+"_"+ NANO_SECOND;
	}

	
	@SuppressWarnings("rawtypes")
	private String getKey(Class clz, long idOne, String condition){
		return VerifyUtil.toMD5(getPrefix(clz, idOne) + condition);
	}

	
	/**
	 * 获取缓存KEY前缀
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getPrefix(Class clz,long idOne){
		
		String key = getNSKey(clz, idOne);
		byte[] nsArr = JedisConnector_Cache3.getInstance().get(key.getBytes());
		if (nsArr == null){
			String str = markForRefresh(clz, idOne);
			return clz.getName() +idOne+ str;
		}
		return clz.getName() +idOne+ new String(nsArr);
	}


}
