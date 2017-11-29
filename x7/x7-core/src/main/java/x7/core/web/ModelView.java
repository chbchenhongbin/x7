package x7.core.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 显示对象
 * 
 * @author Sim
 *
 */
public interface ModelView {

	String OK = "OK";
	String FAIL = "FAIL";
	String STATUS = "status";
	String MESSAGE = "message";
	String MODEL = "model";
	String RESULT = "result";
	
	/**
	 * 适合在service里调用或controller调用
	 * @param message
	 * @return message 到controller，或前端
	 */
	public static Map<String,Object> toast(String message){
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put(STATUS, FAIL);
		map.put(MESSAGE, message);
		return map;
	}
	
	/**
	 * 
	 * @param obj
	 * @return model 到 controller
	 */
	public static Map<String,Object> model(Object obj){
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put(STATUS, OK);
		map.put(MODEL, obj);
		return map;
	}
	
//	/**
//	 * 
//	 * @param objList
//	 * @return objList 到controller
//	 */
//	public static Map<String,Object> model(List<Object> objList){
//		Map<String,Object> map = new HashMap<String,Object>(); 
//		map.put(STATUS, OK);
//		map.put(MODEL_LIST, objList);
//		return map;
//	}
	
	/**
	 * 仅仅可调用一次获得从service层带来的对象
	 * @param map
	 * @return object 
	 */
	public static <T> T object(Map<String,Object> map){
		return (T) map.remove(MODEL);
	}
	
	/**
	 * 
	 * @param obj
	 * @return view 到前端
	 */
	public static Map<String,Object> view(Object obj){
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put(STATUS, OK);
		map.put(RESULT, obj);
		
		System.out.println("response map: " + map);
		return map;
	}
	
	/**
	 * 
	 * @param list
	 * @return view 到前端
	 */
	public static Map<String,Object> view(List<Object> list){
		
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put(STATUS, OK);
		map.put(RESULT, list);
		return map;
	}
	
	public static Map<String,Object> view(){
		
		Map<String,Object> map = new HashMap<String,Object>(); 
		map.put(STATUS, OK);
		return map;
	}
	
	public static boolean isFail(Map<String,Object> map){
		Object status = map.get(STATUS);
		return status != null && status.equals(FAIL);
	}
	
	
}
