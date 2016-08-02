package x7.repository.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import x7.core.bean.Parsed;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtil;



public class SqlUtil {

	
	protected static void adpterSqlKey(PreparedStatement pstmt, String keyOne, String keyTwo, Object obj, int i) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Method method = null;
		try {
			method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
		} catch (NoSuchMethodException e) {
			method = obj.getClass().getSuperclass()
					.getDeclaredMethod(BeanUtil.getGetter(keyOne));
		}
		Object value = method.invoke(obj);
		pstmt.setObject(i++, value);

		if (keyTwo != null && !keyTwo.equals("")){
			try {
				method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(keyTwo));
			} catch (NoSuchMethodException e) {
				method = obj.getClass().getSuperclass()
						.getDeclaredMethod(BeanUtil.getGetter(keyTwo));
			}
			
			value = method.invoke(obj);
			pstmt.setObject(i++, value);
		}
	}
	
	protected static void adpterSqlKey(PreparedStatement pstmt, Field keyOneF, Field keyTwoF, Object obj, int i) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Object value = keyOneF.get(obj);
		pstmt.setObject(i++, value);

		if (keyTwoF != null ){
			value = keyTwoF.get(obj);
			pstmt.setObject(i++, value);
		}
	}

	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concat(String sql, Map<String,Object> queryMap){

		for (String key : queryMap.keySet()){

			sql = sql.concat(" AND " + key + " = ?");

		}
		
		System.out.println(sql);
		
		return sql;
	}
	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed,Map<String,Object> queryMap){

		sb.append(" SET ");
		int size = queryMap.size();
		int i = 0;
		for (String key : queryMap.keySet()){

			sb.append(key);
			sb.append(" = ?");
			if (i < size - 1){
				sb.append(",");
			}
			
			i++;
		}
		
		sb.append(" WHERE ");
		String keyOne = parsed.getKey(Persistence.KEY_ONE);
		sb.append(keyOne).append(" = ?");
		
		if (parsed.isCombinedKey()){
			String keyTwo = parsed.getKey(Persistence.KEY_TWO);
			sb.append(" AND ").append(keyTwo).append(" = ?");
		}
		
		return sb.toString();
	}
	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed,Map<String,Object> queryMap, Map<String,Object> conditionMap){

		sb.append(" SET ");
		int size = queryMap.size();
		int i = 0;
		for (String key : queryMap.keySet()){

			sb.append(key);
			sb.append(" = ?");
			if (i < size - 1){
				sb.append(",");
			}
			
			i++;
		}
		
		sb.append(" WHERE ");
		String keyOne = parsed.getKey(Persistence.KEY_ONE);
		sb.append(keyOne).append(" = ?");
		
		if (parsed.isCombinedKey()){
			String keyTwo = parsed.getKey(Persistence.KEY_TWO);
			sb.append(" AND ").append(keyTwo).append(" = ?");
		}
		
		if (conditionMap != null) {
			for (String key : conditionMap.keySet()) {
				sb.append(" AND ").append(key).append(" = ?");
			}
		}
		
		return sb.toString();
	}
	
	protected static void adpterRefreshCondition(PreparedStatement pstmt, Field keyOneF, Field keyTwoF, Object obj, int i, Map<String,Object> conditionMap) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Object value = keyOneF.get(obj);
		pstmt.setObject(i++, value);

		if (keyTwoF != null ){
			value = keyTwoF.get(obj);
			pstmt.setObject(i++, value);
		}
		
		if (conditionMap != null) {
			for (String key : conditionMap.keySet()) {
				Object v = conditionMap.get(key);
				pstmt.setObject(i++, v);
			}
		}
	}
	
	public static Object filter(Object value) {
		
		if (value instanceof String){
			String str = (String)value;
			value = str.replace("<", "&lt").replace(">", "&gt");
		}
		
		return value;
		
	}
	
}
