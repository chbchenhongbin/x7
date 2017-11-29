package x7.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import x7.core.web.PaginationUtil;

/**
 * 
 * 
 * @author Sim
 *
 */
public class JsonX {

	public static String toJson(List list){
		if (list == null)
			return null;
		if (list.isEmpty()){
			return "[]";
		}
		return JSON.toJSONString(list);
	}
	
	public static String toJson(Map map){
		if (map == null)
			return null;
		if (map.isEmpty()){
			return "{}";
		}
		return JSON.toJSONString(map);
	}
	
	public static <T> List<T> toList(String json, Class<T> clz){
		if (json == null || json.equals(""))
			return new ArrayList<T>();
		return JSON.parseArray(json, clz);
	}
	
	public static Map toMap(String json){
		if (json == null || json.equals(""))
			return new HashMap();
		return (Map) JSON.parse(json);
	}
	
	public static String toJson(Object obj){
		if (obj == null)
			return null;
		if (obj instanceof PaginationSorted){
			PaginationSorted ps = (PaginationSorted) obj;
			PaginationUtil.toMap(ps);
			return JSON.toJSONString(ps);
		}else if (obj instanceof Pagination){
			Pagination p = (Pagination) obj;
			PaginationUtil.toMap(p);
			return JSON.toJSONString(p);
		}
		return JSON.toJSONString(obj);
	}
	
	public static <T> T toObject(String json, Class<T> clz){
		if (json == null || json.equals(""))
			return null;
		return JSON.parseObject(json, clz);
	}
	
	public static <T> T toObject(Object jsonObject, Class<T> clz){
		if (Objects.isNull(jsonObject))
			return null;

		return JSON.toJavaObject((JSON)jsonObject, clz);
	}
	
	public static Map<String,Object> toMap(Object obj){
		return (Map<String,Object>) JSON.toJSON(obj);
	}
	
}
