package x7.core.bean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.config.Configs;
import x7.core.repository.Persistence;
import x7.core.repository.ReflectionCache;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;

public class Parser {

	@SuppressWarnings("rawtypes")
	private final static Map<Class, Parsed> map = new ConcurrentHashMap<Class, Parsed>();
	
	private final static Map<String, Parsed> simpleNameMap = new ConcurrentHashMap<String,Parsed>();

	private final static Map<Class, ReflectionCache> cacheMap = new ConcurrentHashMap<Class, ReflectionCache>();

	@SuppressWarnings("rawtypes")
	public static void put(Class clz, Parsed parsed) {
		map.put(clz, parsed);
		String key = BeanUtil.getByFirstLower(clz.getSimpleName());
		simpleNameMap.put(key, parsed);
	}

	@SuppressWarnings("rawtypes")
	public static Parsed get(Class clz) {
		Parsed parsed = map.get(clz);
		if (parsed == null) {
			parse(clz);
			parsed = map.get(clz);
		}
		return parsed;
	}
	
	public static Parsed get(String simpleName) {
		return simpleNameMap.get(simpleName);
	}

	@SuppressWarnings({ "rawtypes" })
	public static void parse(Class clz) {

		if (clz == Criteria.class || clz == Criteria.Fetch.class)
			throw new RuntimeException("parser unsupport Criteria, CriteriaJoinable, ....");

		List<BeanElement> elementList = BeanUtilX.getElementList(clz);
		Parsed parsed = new Parsed(clz);
		for (BeanElement element : elementList) {
			if (StringUtil.isNullOrEmpty(element.getMapper())) {
				element.initMaper();
			}
		}
		boolean isNoSpec = true;
		try{
			String str = Configs.getString("x7.db.naming.spec");
			if (StringUtil.isNotNull(str)){
				isNoSpec = false;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		parsed.setNoSpec(isNoSpec);
		parsed.setBeanElementList(elementList);
		BeanUtilX.parseKey(parsed, clz);

		/*
		 * tableName, 支持领域建表
		 */
		Persistence p = (Persistence) clz.getAnnotation(Persistence.class);
		if (p != null) {
			String tableName = p.mapper();
			if (!tableName.equals("")) {
				parsed.setTableName(tableName);
			} else {
				String name = BeanUtil.getByFirstLower(clz.getSimpleName());
				String mapper = BeanUtil.getMapper(name);
				String prefix = Configs.getString("x7.db.naming.prefix");
				if (StringUtil.isNotNull(prefix)) {
					if (!prefix.endsWith("_")) {
						prefix += "_";
					}
					mapper = prefix + mapper;
				}

				parsed.setTableName(mapper);
			}
		} else {
			String name = BeanUtil.getByFirstLower(clz.getSimpleName());
			String mapper = BeanUtil.getMapper(name);
			String prefix = Configs.getString("x7.db.naming.prefix");
			if (StringUtil.isNotNull(prefix)) {
				if (!prefix.endsWith("_")) {
					prefix += "_";
				}
				mapper = prefix + mapper;
			}

			parsed.setTableName(mapper);
		}

		/*
		 * 排序
		 */
		BeanElement one = null;
		BeanElement two = null;
		Iterator<BeanElement> ite = elementList.iterator();
		while (ite.hasNext()) {
			BeanElement be = ite.next();
			if (be.getProperty().equals(parsed.getKey(Persistence.KEY_ONE))) {
				one = be;
				ite.remove();
				continue;
			}
			if (parsed.isCombinedKey()) {
				if (be.getProperty().equals(parsed.getKey(Persistence.KEY_TWO))) {
					two = be;
					ite.remove();
				}
			}

		}

		elementList.add(0, one);

		if (parsed.isCombinedKey()) {
			elementList.add(1, two);
		}

		Iterator<BeanElement> beIte = parsed.getBeanElementList().iterator();
		while (beIte.hasNext()) {
			if (null == beIte.next()) {
				beIte.remove();
			}
		}

		/*
		 * parseCacheable
		 */
		BeanUtilX.parseCacheableAnno(clz, parsed);

		put(clz, parsed);

		System.out.println(elementList.toString());

		/*
		 * parse search
		 */
		BeanUtilX.parseSearch(parsed, clz);
	}

	public static ReflectionCache getReflectionCache(Class clz) {
		ReflectionCache cache = cacheMap.get(clz);
		if (cache == null) {
			cache = new ReflectionCache();
			cache.setClz(clz);
			cache.cache();
			cacheMap.put(clz, cache);
		}
		return cache;
	}
}
