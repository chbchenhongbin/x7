package x7.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.repository.dao.AsyncDaoWrapper;

/**
 * 
 * 
 * @author wyan
 * 
 */
public class AsyncRepository {

	public static void create(Object obj) {
		AsyncDaoWrapper.getInstance().create(obj);
	}

	public static void refresh(Object obj) {
		AsyncDaoWrapper.getInstance().refresh(obj);
	}

	public static void remove(Object obj) {
		AsyncDaoWrapper.getInstance().remove(obj);
	}

	public static <T> List<T> listSync(Class<T> clz) {
		List<T> list = AsyncDaoWrapper.getInstance().listSync(clz);
		CacheOne.put(clz, list);
		return list;
	}

	public static <T> List<T> list(Class<T> clz, long idOne) {
		return CacheOne.list(clz, idOne);
	}
	
	public static <T> T get(Class<T> clz, Object idOne) {
		return CacheOne.get(clz, idOne);
	}
	
	public static <T> T get(Class<T> clz, Object idOne, long idTwo) {
		return CacheOne.get(clz, idOne, idTwo);
	}

	static class CacheOne {
		private static Map<Class, Map<String, ? extends Object>> map = new HashMap<Class, Map<String, ? extends Object>>();

		protected static void put(Class clz, List list) {
			Parsed parsed = Parser.get(clz);
			Map<String, Object> map1 = new ConcurrentHashMap<String, Object>();
			map.put(clz, map1);
			if (parsed.isCombinedKey()) {

				try{
					for (Object obj : list) {
						Object idOne = parsed.getKeyField(1).get(Persistence.KEY_ONE);
						String key = getKey(clz, idOne);
						
						Object o =  map1.get(key);
						if (o == null) {
							o = new ConcurrentHashMap<Long, Object>(); 
							map1.put(key,o);
						}
						Map<Long,Object> map2 = (Map<Long,Object>)o;
						Long idTwo = parsed.getKeyField(Persistence.KEY_TWO).getLong(obj);
						map2.put(idTwo, obj);

					}
				}catch (Exception e){
					e.printStackTrace();
				}
				
			} else {
				try {
					for (Object obj : list) {
						long idOne = parsed.getKeyField(Persistence.KEY_ONE).getLong(obj);
						String key = getKey(clz, idOne);
						map1.put(key, obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		protected static <T> T get(Class<T> clz, Object idOne) {

			Map<String, ? extends Object> map0 = map.get(clz);
			if (map0 == null)
				return null;
			Object o = map0.get(getKey(clz, idOne));
			if (o == null)
				return null;
			return (T) o;
		}
		
		protected static <T> T get(Class<T> clz, Object idOne, long idTwo) {

			Map<String, ? extends Object> map1= map.get(clz);
			if (map1 == null)
				return null;
			Object obj2 = map1.get(getKey(clz, idOne));
			if (obj2 == null)
				return null;
			Map<Long, Object> map2 = (Map<Long, Object>) obj2;
			Object o = map2.get(idTwo);
			if (o == null)
				return null;
			return (T) o;
		}

		protected static <T> List<T> list(Class<T> clz, Object idOne) {
			Map<String, ? extends Object> map1 = map.get(clz);
			if (map1 == null)
				return null;
			Object o = map1.get(getKey(clz, idOne));
			if (o == null)
				return null;
			return (List<T>) o;
		}

		private static String getKey(Class clz, Object idOne) {
			return clz.getSimpleName() + "_" + idOne.toString();
		}
	}
}
