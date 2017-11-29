package x7.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import x7.core.util.BeanUtil;


public class BatchUtil {

	
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
}
