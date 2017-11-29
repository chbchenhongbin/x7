package x7.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyUtil {

	public static List<String> getKeyList(String str) {

		List<String> list = null;
		if (str.contains(".")) {
			String[] arr = str.split("\\.");
			list = Arrays.asList(arr);

		} else if (str.contains("/")) {
			if (str.startsWith("/")) {
				str = str.substring(1);
			}
			String[] arr = str.split("\\/");
			list = Arrays.asList(arr);
		} else {
			list = new ArrayList<String>();
			list.add(str);
		}

		return list;
	}

	/**
	 * 仅仅用于JAVA类名做为KEY时的转换
	 * @param key
	 * @return
	 */
	public static String getKey(Class clz) {

		String key = clz.getName();
		if (key.contains(".")) {
			key = key.replace(".", "_");
		}

		return key;
	}
}
