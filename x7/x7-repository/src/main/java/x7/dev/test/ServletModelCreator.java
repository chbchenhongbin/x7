package x7.dev.test;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import x7.core.util.BeanMapUtil;
import x7.core.util.BeanUtil;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.core.util.TimeUtil;
import x7.core.web.PassportCondition;

public class ServletModelCreator {
	private static Logger logger = Logger.getLogger(ServletModelCreator.class.getName());

	public static <T> T createModel(HttpServletRequest request, Class<T> clz) {

		Object obj = null;
		try {
			obj = clz.newInstance();
		} catch (InstantiationException e1) {

			e1.printStackTrace();
		} catch (IllegalAccessException e1) {

			e1.printStackTrace();
		}

		Map<String, String[]> inputs = request.getParameterMap();

		List<Field> list = new ArrayList<Field>();

		Field[] fs = clz.getSuperclass().getDeclaredFields();
		for (Field f : fs) {
			list.add(f);
		}

		fs = clz.getDeclaredFields();
		for (Field f : fs) {
			list.add(f);
		}

		for (Field field : list) {
			String key = field.getName();
			String v = "";
			String[] value = null;

			if (inputs.containsKey(key)) {

				value = inputs.get(key);
				v = value[0].trim();

				if (StringUtil.isNullOrEmpty(v))
					continue;
				// Object v = null;

				Class<?> c = field.getType();
				// if ( c.getSimpleName().equals("String"))
				// v = ((String[])value)[0];
				String name = c.getSimpleName();

				try {
					if (name.equals("String")) {
						v = v.replace("<", "&lt").replace(">", "&gt");
						obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj, v);

					} else {
						if (!v.equals("")) {
							if (name.equals("int") || name.equals("Integer")) {
								obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
										Integer.parseInt(v));

							} else if (name.equals("long") || name.equals("Long")) {
								obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
										Long.parseLong(v));
							} else if (name.equals("double") || name.equals("Double")) {
								obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
										Double.parseDouble(v));
							} else if (name.equals("float") || name.equals("Float")) {
								obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
										Float.parseFloat(v));
							} else if (name.equals("boolean") || name.equals("Boolean")) {
								System.err.println(v + " true or false = " + Boolean.parseBoolean(v));
								obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
										Boolean.parseBoolean(v));
							} else if (name.contains("Date")) {
								if (v.contains("-")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											TimeUtil.getDate(v));
								} else {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											TimeUtil.getDate(Long.valueOf(v)));
								}
							}
						}
					}
				} catch (Exception e) {
					logger.log(Level.INFO,
							" *******  ServletModelCreator.createModel: getDeclaredMethod.invoke" + e.getMessage());
					throw new RuntimeException("ServletModelCreator.createModel: getDeclaredMethod().invoke() wrong"
							+ c.getName() + "xxx: " + key + "****** " + e.getMessage());
				}
			}

		}

		return (T) obj;
	}

	public static List createModelList(HttpServletRequest request, Class clz) {
		List list = new ArrayList();
		Map inputs = request.getParameterMap();
		Field[] fields = clz.getDeclaredFields();

		int row = 0;
		Iterator ite = inputs.values().iterator();
		while (ite.hasNext()) {
			String[] ss = (String[]) ite.next();
			row = ss.length;
			break;
		}
		for (int i = 0; i < row; i++) {
			int length = fields.length;

			Object obj = null;
			try {
				obj = clz.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}

			for (int l = 0; l < length; l++) {
				String key = "";
				String v = "";
				Object value = null;
				key = fields[l].getName();
				if (inputs.containsKey(key)) {

					value = inputs.get(key);

					v = ((String[]) value)[i].trim();
					logger.log(Level.INFO, "key: " + key + "     :      value: " + v);
					// Object v = null;

					Class<?> c = fields[l].getType();
					// if ( c.getSimpleName().equals("String"))
					// v = ((String[])value)[0];
					String name = c.getSimpleName();
					try {
						if (name.equals("String")) {
							obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj, v);
						} else {
							if (!v.equals("")) {
								if (name.equals("int") || name.equals("Integer")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											Integer.parseInt(v));

								} else if (name.equals("long") || name.equals("Long")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											Long.parseLong(v));
								} else if (name.equals("double") || name.equals("Double")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											Double.parseDouble(v));
								} else if (name.equals("float") || name.equals("Float")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											Float.parseFloat(v));
								} else if (name.equals("boolean") || name.equals("Boolean")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											Boolean.parseBoolean(v));
								} else if (name.contains("Date")) {
									obj.getClass().getDeclaredMethod(BeanUtil.getSetter(key), c).invoke(obj,
											TimeUtil.getDate(v));
								}
							}
						}
					} catch (Exception e) {
						logger.log(Level.INFO, " *******  ServletModelCreator.createModelList: getDeclaredMethod.invoke"
								+ e.getMessage());
						throw new RuntimeException(
								"ServletModelCreator.createModel: getDeclaredMethodList().invoke() wrong");
					}
				}
			}
			list.add(obj);
		}
		if (list.size() == 0)
			list = null;
		return list;
	}

	public static Map<String, String> createMap(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Map<String, String[]> inputs = request.getParameterMap();

		for (String key : inputs.keySet()) {

			String[] value = inputs.get(key);
			String v = value[0].trim();

			if (StringUtil.isNullOrEmpty(v))
				continue;

			key = key.replace("<", "&lt").replace(">", "&gt");
			v = v.replace("<", "&lt").replace(">", "&gt");

			map.put(key, v);
		}

		return map;
	}

	public static Map<String, Object> createMapBySimpleHttp(HttpServletRequest request) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		Map<String, Object> map = null;

		if (str.startsWith("{") && str.endsWith("}")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			map = JsonX.toMap(str);

			System.out.println("createMapBySimpleHttp, map:  " + map);

			return map;
		}

		map = new HashMap<String, Object>();

		if (str.contains("=")) {
			String[] arr = str.split("&");
			for (String kv : arr) {
				if (kv.contains("=")) {
					String[] kvArr = kv.split("=");
					String v = kvArr[1];
					v = v.replace("<", "&lt").replace(">", "&gt");
					map.put(kvArr[0], v);
				}
			}
		}

		System.out.println("createMapBySimpleHttp, map:  " + map);

		return map;
	}

	public static <T> T createModelByJson(HttpServletRequest request, Class<T> clz) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		if (str.startsWith("{") && str.endsWith("}")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			// map = JsonX.toMap(str);

			System.out.println("request str: " + str);

			T t = JsonX.toObject(str, clz);

			return t;

		}

		Map<String, Object> map = new HashMap<String, Object>();

		if (str.contains("=")) {
			String[] arr = str.split("&");
			for (String kv : arr) {
				if (kv.contains("=")) {
					String[] kvArr = kv.split("=");
					String v = kvArr[1];
					v = v.replace("<", "&lt").replace(">", "&gt");
					map.put(kvArr[0], v);
				}
			}
		}

		System.out.println("request map: " + map);

		T t = BeanMapUtil.toObject(clz, map);

		return t;
	}

	public static <T> List<T> createListByJson(HttpServletRequest request, Class<T> clz) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		if (str.startsWith("[") && str.endsWith("]")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			// map = JsonX.toMap(str);

			System.out.println("request str: " + str);

			List<T> list = JsonX.toList(str, clz);

			return list;

		}

		return null;
	}

	public static <T> T createModelByJson(HttpServletRequest request, Class<T> clz,
			PassportCondition passportCondition) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		if (str.startsWith("{") && str.endsWith("}")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			// map = JsonX.toMap(str);

			System.out.println("request str: " + str);

			T t = JsonX.toObject(str, clz);

			return t;

		}

		Map<String, Object> map = new HashMap<String, Object>();

		if (str.contains("=")) {
			String[] arr = str.split("&");
			for (String kv : arr) {
				if (kv.contains("=")) {
					String[] kvArr = kv.split("=");
					String v = kvArr[1];
					v = v.replace("<", "&lt").replace(">", "&gt");
					map.put(kvArr[0], v);
				}
			}
		}

		System.out.println("request map: " + map);

		T t = BeanMapUtil.toObject(clz, map);

		return t;
	}

	public static <T> List<T> createListByJson(HttpServletRequest request, Class<T> clz,
			PassportCondition passportCondition) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		if (str.startsWith("[") && str.endsWith("]")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			// map = JsonX.toMap(str);

			System.out.println("request str: " + str);

			List<T> list = JsonX.toList(str, clz);

			return list;

		}

		return null;
	}

}