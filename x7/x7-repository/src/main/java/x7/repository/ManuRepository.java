package x7.repository;

import java.util.List;
import java.util.Map;


public class ManuRepository {

	public static <T> boolean execute(Object obj, String sql){
		
		return Repositories.getInstance().execute(obj, sql);
		
	}
	
	
	public static List<Map<String,Object>> list(Class clz, String sql, List<Object> conditionList){
		
		return Repositories.getInstance().list(clz, sql, conditionList);
	}
}
