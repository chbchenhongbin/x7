package x7.core.util;
/**
 * 布尔型数据转换 防止空指针
 * @author linweiyong
 * @date 2016年1月13日
 */
public class BooleanUtil {
	
	public static boolean getValue(Boolean b) {
		return b == null ? false : b;
	}

}
