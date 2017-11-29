package x7.core.util;

import java.util.HashMap;
import java.util.Map;

public class PropertyViewFormater {

	public final static String NULL_STRING = "";
	public final static String DEFAULT_REGEX = "_";
	
	
	public static Map<String,String> toPropertyMap(Map<String,String> map){
		
		return toPropertyMap(map, DEFAULT_REGEX);
	}
	
	public static Map<String,String> toPropertyMap(Map<String,String> map, String regex){
		
		Map<String,String> resultMap = new HashMap<String, String>();
		
		for (String key : map.keySet()){
			String k = toPropertyFormat(key, regex);
			resultMap.put(k, map.get(key));
		}
		return resultMap;
	}
	
	public static Map<String,String> toViewMap(Map<String,String> map){

		return toViewMap(map, DEFAULT_REGEX);
	}
	
	public static Map<String,String> toViewMap(Map<String,String> map, String regex){
		
		Map<String,String> viewMap = new HashMap<String, String>();
		
		for (String key : map.keySet()){
			String k = toViewFormat(key, regex);
			viewMap.put(k, map.get(key));
		}
		return viewMap;
	}
	
	public static String toPropertyFormat(String origin){
		if (origin == null || origin.equals(NULL_STRING)) return null;

		return toPropertyFormat(origin, DEFAULT_REGEX);
	}
	
	public static String toPropertyFormat(String origin, String regex){
		if (origin == null || origin.equals(NULL_STRING)) return null;
		
		String result = NULL_STRING;
		
		String[] arr = origin.split(regex);
		int length = arr.length;
		for (int i=0; i<length; i++){
			String seg = arr[i];
			if (i == 0){
				result = result.concat(seg);
			}else{
				String first = seg.substring(0, 1);
				first = first.toUpperCase();
				result = result.concat(first).concat(seg.substring(1));
			}
		}
		
		return result;
	}
	
	public static String toViewFormat(String origin){
		if (origin == null || origin.equals(NULL_STRING)) return null;
		
		return toViewFormat(origin,DEFAULT_REGEX);
	}
	
	public static String toViewFormat(String origin, String regex){
		if (origin == null || origin.equals(NULL_STRING)) return null;
		
		String result = NULL_STRING;
		
		char [] carr = origin.toCharArray(); 
		int length = carr.length;
		for (int i=0; i<length; i++){
			char c = carr[i];
			if (Character.isLowerCase(c)){
				result += c;
			}else{
				result += regex;
				result += Character.toLowerCase(c);
			}
		}
		
		return result;
	}
	
}
