package x7.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import x7.core.bean.BeanElement;
import x7.core.util.JsonX;

public class ResultSetUtil {

	public static <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles)
			throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (BeanElement ele : eles) {
			Method method = ele.setMethod;
			String mapper = ele.getMapper();
			if (ele.isJson) {
				String str = rs.getString(mapper);
				if (ele.clz == Map.class) {
					tempEle = ele;
					method.invoke(obj, JsonX.toMap(str));
				} else if (ele.clz == List.class) {
					tempEle = ele;
					method.invoke(obj, JsonX.toList(str, ele.geneType));
				} else {
					tempEle = ele;
					method.invoke(obj, JsonX.toObject(str, ele.clz));
				}
			} else if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
				Object v = rs.getObject(mapper);
				if (v != null) {
					method.invoke(obj, Double.valueOf(String.valueOf(v)));
				}
			} else if(ele.clz == BigDecimal.class){

				Object v = rs.getObject(mapper);
				if (v != null) {
					method.invoke(obj, new BigDecimal((String.valueOf(v))));
				}
			}else {
				tempEle = ele;
				method.invoke(obj, rs.getObject(mapper));
			}
		}
	}
	
	

}
