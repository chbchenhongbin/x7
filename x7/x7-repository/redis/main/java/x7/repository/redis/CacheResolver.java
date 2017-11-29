package x7.repository.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import x7.core.config.Configs;
import x7.core.repository.CacheException;
import x7.core.repository.ICacheResolver;
import x7.core.util.VerifyUtil;
import x7.core.web.Pagination;



/**
 * 缓存解决, 二级缓存<br>
 * FIXME mget 相关的要重新设计， 单个对象缓存缺少{hash tag}
 * @author sim
 *
 */
public class CacheResolver implements ICacheResolver{

	public final static String NANO_SECOND = ".N_S";
	
	private static CacheResolver instance = null;
	public static CacheResolver getInstance(){
		if (instance == null){
			instance = new CacheResolver();
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
		boolean flag = JedisConnector_Cache.getInstance().set(key.getBytes(), time.getBytes());
		if (!flag)
			throw new CacheException("markForRefresh failed");
		return time;
	}
	
	/**
	 * 
	 * FIXME {hash tag}
	 */
	@SuppressWarnings("rawtypes")
	public void remove(Class clz, String key){
		key = getSimpleKey(clz, key);
		boolean flag = JedisConnector_Cache.getInstance().delete(key.getBytes());
		if (!flag)
			throw new CacheException("remove failed");
	}
	
	@SuppressWarnings("rawtypes")
	private String getNSKey(Class clz){
		return clz.getName()+ NANO_SECOND;
	}
	
	@SuppressWarnings("unused")
	private String getNS(String nsKey){
		return JedisConnector_Cache.getInstance().get(nsKey);
	}
	
	@SuppressWarnings("rawtypes")
	private byte[][] getKeyList(Class clz, List<String> conditionList){
		if (conditionList == null || conditionList.isEmpty())
			return null;
		List<byte[]> keyList = new ArrayList<byte[]>();
		for (String condition : conditionList){
			String key = getSimpleKey(clz, condition);
			keyList.add(key.getBytes());
		}
		if (keyList.isEmpty())
			return null;
		byte[][] arrList= new byte[keyList.size()][];
//		keyList.toArray(arrList);
		int i = 0;
		for (byte[] keyB : keyList){
			arrList[i++] = keyB;
		}
		return arrList;
	}
	
	/**
	 * FIXME 有简单simpleKey的地方全改成字符串存储, value为bytes, new String(bytes)
	 * @param clz
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getSimpleKey(Class clz, String condition){
		return "{"+clz.getName()+"}." + condition;
	}
	
	
	@SuppressWarnings("rawtypes")
	private String getKey(Class clz, String condition){
		long startTime = System.currentTimeMillis();
		String key =  VerifyUtil.toMD5(getPrefix(clz) + condition);
		long endTime = System.currentTimeMillis();
		System.out.println("time_getKey = "+(endTime - startTime));
		return key;
	}

	
	/**
	 * 获取缓存KEY前缀
	 * @param clz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getPrefix(Class clz){
		String key = getNSKey(clz);
		byte[] nsArr = JedisConnector_Cache.getInstance().get(key.getBytes());
		if (nsArr == null){
			String str = markForRefresh(clz);
			return clz.getName() + str;
		}
		return clz.getName() + new String(nsArr);
	}

	/**
	 * FIXME {hash tag}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void set(Class clz, String key, Object obj) {
		key = getSimpleKey(clz, key);
		int validSecond =  getValidSecondAdjusted();
		JedisConnector_Cache.getInstance().set(key.getBytes(), PersistenceUtil.toBytes(obj), validSecond);
	}

	
	private int getValidSecondAdjusted(){
		return  Configs.getIntValue("x7.cache.second") * 700;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setResultKeyList(Class clz, String condition, List<String> keyList) {
		String key = getKey(clz, condition);
		int validSecond = Configs.getIntValue("x7.cache.second");
		JedisConnector_Cache.getInstance().set(key.getBytes(), ObjectUtil.toBytes(keyList), validSecond);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T> void setResultKeyListPaginated(Class<T> clz, String condition, Pagination<T> pagination) {
		
		int validSecond = Configs.getIntValue("x7.cache.second");
		setResultKeyListPaginated(clz, condition, pagination, validSecond);
	}
	
	@Override
	public <T> void setResultKeyListPaginated(Class<T> clz, String condition, Pagination<T> pagination, int second) {
		
		String key = getKey(clz, condition);
		JedisConnector_Cache.getInstance().set(key.getBytes(), ObjectUtil.toBytes(pagination), second);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> getResultKeyList(Class clz, String condition) {
		String key = getKey(clz, condition);
		System.out.println("get key: " + key);
		long startTime = System.currentTimeMillis();
		byte[] bytes = JedisConnector_Cache.getInstance().get(key.getBytes());
		long endTime = System.currentTimeMillis();
		System.out.println("time_getResultKeyList = "+(endTime - startTime));
		if (bytes == null)
			return new ArrayList<String>();
		
		return (List<String>) ObjectUtil.toObject(bytes);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Pagination<String> getResultKeyListPaginated(Class clz, String condition) {
		String key = getKey(clz, condition);
		System.out.println("get key: " + key);
		byte[] bytes = JedisConnector_Cache.getInstance().get(key.getBytes());
		
		if (bytes == null)
			return null;
		
		return (Pagination<String>) ObjectUtil.toObject(bytes);
	}

	@Override
	public <T> List<T> list(Class<T> clz, List<String> keyList) {
		byte[][] bytesArr = getKeyList(clz, keyList);//转换成缓存需要的keyList
		
		List<byte[]> bytesList = JedisConnector_Cache.getInstance().mget(bytesArr);
		
		if (bytesList == null)
			return new ArrayList<T>();
		
		List<T> objList = new ArrayList<T>();
		for (byte[] bytes : bytesList){
			if (bytes == null)
				continue;
			T t = PersistenceUtil.toObject(clz, bytes);
			if (t == null)
				continue;
			objList.add(t);
		}
		
		return objList;
	}

	/**
	 * FIXME {hash tag}
	 */
	@Override
	public <T> T get(Class<T> clz, String key) {
		key = getSimpleKey(clz,key);
		byte[] bytes = JedisConnector_Cache.getInstance().get(key.getBytes());
		if (bytes == null)
			return null;
		T obj = PersistenceUtil.toObject(clz, bytes);
		return obj;
	}

	@Override
	public void setMapList(Class clz, String key, List<Map<String, Object>> mapList) {
		key = getSimpleKey(clz, key);
		int validSecond =  getValidSecondAdjusted();
		
		JedisConnector_Cache.getInstance().set(key.getBytes(), PersistenceUtil.toBytes(mapList), validSecond);
	}

	@Override
	public List<Map<String, Object>> getMapList(Class clz, String key) {
		
		key = getSimpleKey(clz,key);
		byte[] bytes = JedisConnector_Cache.getInstance().get(key.getBytes());
		if (bytes == null)
			return null;
		List<Map<String, Object>> mapList = PersistenceUtil.toObject(List.class, bytes);
		return mapList;
	}



}
