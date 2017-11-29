package x7.core.template;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.bean.KV;

/**
 * 
 * 模板管理器<br>
 * @author wyan
 *
 */
public class Templates {

	private final static Map<Class<? extends ITemplateable>, Map<Object, ITemplateable>> templatesMap = new ConcurrentHashMap<Class<? extends ITemplateable>, Map<Object, ITemplateable>>();
	
	private final static Map<Class<? extends ITemplateable>, List<KV>> schemaMap  = new ConcurrentHashMap<>();
//	public static ITemplateable get(Class<? extends ITemplateable> clz, Integer templateId){
//		Map<Integer, ? extends ITemplateable> templateMap = templatesMap.get(clz);
//		if (templateMap == null)
//			return null;
//		return templateMap.get(templateId);
//	}
	
	public static <T> T get(Class<T> clz, Object templateId){
		Map<Object, ? extends ITemplateable> templateMap = templatesMap.get(clz);
		if (templateMap == null)
			return null;
		return (T) templateMap.get(templateId);
	}
	
//	public static Map<Integer, ITemplateable> get(Class<? extends ITemplateable> clz){
//		return templatesMap.get(clz);
//	}
	
	public static <T> Map<Object, T> get(Class<T> clz){
		return (Map<Object, T>) templatesMap.get(clz);
	}

	public static void clear() {
		templatesMap.clear();
	}
	
	/**
	 * 加载模板时调用，或热更新时调用
	 * @param clz
	 */
	public static Map<Object, ITemplateable> createOrGet(Class<? extends ITemplateable> clz){

		Map<Object, ITemplateable> map = templatesMap.get(clz);
		if (map == null){
			map = new HashMap<Object, ITemplateable>();
			templatesMap.put(clz, map);
		}
		
		return map;
	}
	
	public static void put(Class<? extends ITemplateable> clz, Map<Object, ITemplateable> map ){
		templatesMap.put(clz, map);
	}
	
	public static void put(Class<? extends ITemplateable> key,List<KV> value){
		schemaMap.put(key, value);
	}
	
	public static List<KV> getSchema(Class<? extends ITemplateable> key){
		return schemaMap.get(key);
	}
	
}
