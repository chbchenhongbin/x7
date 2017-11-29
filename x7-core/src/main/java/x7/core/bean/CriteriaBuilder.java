package x7.core.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.bean.Criteria.Fetch;
import x7.core.repository.Persistence;
import x7.core.util.BeanMapUtil;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.NumberUtil;
import x7.core.util.StringUtil;
import x7.core.util.TimeUtil;
import x7.core.web.Fetched;
import x7.core.web.RequestMapped;

/**
 * 简单的SQL拼接标准化, 仅仅支持简单的业务系统, 不支持报表和数据分析
 * 
 * @author sim
 *
 */
public class CriteriaBuilder {

	public final static String xReg = "$$";

	public final static String ASC = "asc";
	public final static String DESC = "desc";
	public final static String ALIAS = "${ALIAS}";
	public final static String space = " ";
	public final static String comma = ",";
	public final static String WHERE = " where ";
	public final static String AND = " and ";
	public final static String OR = " or ";

	public final static String X = "x";
	public final static String EQ = "eq";
	public final static String LT = "lt";
	public final static String GT = "gt";
	public final static String LTE = "lte";
	public final static String GTE = "gte";
	public final static String NOT = "not";
	public final static String LIKE = "like";
	public final static String ORDER_BY = "orderBy";
	public final static String SC = "sc";
	public final static String GROUP_BY = "groupBy";
	public final static String BETWEEN = "between";
	public final static String IN = "in";
	public final static String NOT_IN = "notIn";
	public final static String IS_NOT_NULL = "isNotNull";
	public final static String IS_NULL = "isNull";
	public final static String WILD_CARD = "*";
	public final static String CLASS_NAME = "class.name";
	public final static String LEFT_JOIN = "leftJoin";
	public final static String RIGHT_JOIN = "rightJoin";
	public final static String INNER_JOIN = "innerJoin";
	public final static String ON = "on";
	public final static String AS = ".as.";

	@SuppressWarnings("serial")
	public final static Map<String, String> expressionMap = new HashMap<String, String>() {
		{
			put(X, "x");
			put(EQ, "=");
			put(LT, "<");
			put(GT, ">");
			put(LTE, "<=");
			put(GTE, ">=");
			put(NOT, "<>");
			put(LIKE, "like");
			put(BETWEEN, "between");
			put(IN, "in");
			put(NOT_IN, "not in");
			put(IS_NOT_NULL, "is not null");
			put(IS_NULL, "is null");
			put(LEFT_JOIN, "left join");
			put(RIGHT_JOIN, "right join");
			put(INNER_JOIN, "inner join");
			put(AS, " ");
		}
	};

	@SuppressWarnings("serial")
	public final static Map<String, String> xMap = new HashMap<String, String>() {
		{
			put(EQ, "=");
			put(LT, "<");
			put(GT, ">");
			put(LTE, "<=");
			put(GTE, ">=");
			put(NOT, "<>");
			put(LIKE, "like");
		}
	};

	@SuppressWarnings("serial")
	public final static Map<String, String> nullMap = new HashMap<String, String>() {
		{
			put(IS_NOT_NULL, "is not null");
			put(IS_NULL, "is null");
		}
	};

	private Map<String, Object> tempMap = null;
	private Map<String, MinMax> tempBetweenMap = null;
	private Map<String, List<Object>> tempInMap = null;
	private Map<String, List<Object>> tempNotInMap = null;
	private List<String> tempList = null;

	private Criteria criteria;

 	private CriteriaBuilder instance;

	private Map<String, Object> getTempMap() {
		return this.tempMap;
	}

	private Map<String, MinMax> getTempBetweenMap() {
		return this.tempBetweenMap;
	}

	private Map<String, List<Object>> getTempInMap() {
		return this.tempInMap;
	}

	private Map<String, List<Object>> getTempNotInMap() {
		return this.tempNotInMap;
	}

	private List<String> getTempList() {
		return this.tempList;
	}

	private I c = new I() {

		@Override
		public CriteriaBuilder eq(String property, Object value) {

			// check(property);

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(EQ);

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder lt(String property, Object value) {

			// check(property);

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(LT);
			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder lte(String property, Object value) {

			// check(property);

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(LTE);

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder gt(String property, Object value) {

			check(property);

			if (value == null)
				return instance;
			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(GT);

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder gte(String property, Object value) {

			// check(property);

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(GTE);

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder not(String property, Object value) {

			// check(property);

			if (value == null)
				return instance;

			if (isBaseType_0(property, value))
				return instance;
			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(NOT);

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder like(String property, Object value) {

			check(property);

			if (value == null)
				return instance;

			if (isNullOrEmpty(value))
				return instance;

			property = getAliasPoint(property);
			String express = " " + expressionMap.get(LIKE);
			value = "%" + value + "%";

			getTempMap().put(property + express, value);

			return instance;
		}

		@Override
		public CriteriaBuilder between(String property, Object min, Object max) {

			// check(property);

			if (min == null || max == null)
				return instance;

			if (isBaseType_0(property, max))
				return instance;
			if (isNullOrEmpty(max))
				return instance;
			if (isNullOrEmpty(min))
				return instance;

			MinMax minMax = new MinMax();
			minMax.setMin(min);
			minMax.setMax(max);
			getTempBetweenMap().put(property, minMax);

			return instance;
		}

		@Override
		public CriteriaBuilder in(String property, List<Object> list) {

			check(property);

			if (list == null || list.isEmpty())
				return instance;
			property = getAliasPoint(property);
			Iterator<Object> ite = list.iterator();
			while (ite.hasNext()) {
				Object obj = ite.next();
				if (Objects.isNull(obj)) {
					ite.remove();
				}
			}
			if (list.isEmpty())
				return instance;
			getTempInMap().put(property, list);

			return instance;
		}

		@Override
		public CriteriaBuilder notIn(String property, List<Object> list) {

			check(property);

			if (list == null || list.isEmpty())
				return instance;
			property = getAliasPoint(property);
			Iterator<Object> ite = list.iterator();
			while (ite.hasNext()) {
				Object obj = ite.next();
				if (Objects.isNull(obj)) {
					ite.remove();
				}
			}
			if (list.isEmpty())
				return instance;
			getTempNotInMap().put(property, list);
			return instance;
		}

		public CriteriaBuilder x(String xExpression) {

			if (StringUtil.isNullOrEmpty(xExpression))
				return instance;
			xExpression = getX(xExpression);

			getTempList().add(xExpression);
			return instance;
		}

		@Override
		public X x() {

			XObject xo = new XObject("(" + xReg + ")");

			X x = new X() {

				@Override
				public X x() {

					xo.x("(" + xReg + ")");

					return this;
				}

				@Override
				public X and() {

					xo.x(" #and#" + xReg);

					return this;
				}

				@Override
				public X or() {

					xo.x(" #or#" + xReg);

					return this;
				}

				@Override
				public X isNull(String property) {

					check(property);

					xo.x(" " + property + " is null" + xReg);

					return this;
				}

				@Override
				public X isNotNull(String property) {

					check(property);

					xo.x(" " + property + " is not null" + xReg);

					return this;
				}

				@Override
				public X eq(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " = " + value + xReg + " ");
					} else {
						xo.x(" " + property + " = '" + value + "'" + xReg + " ");
					}
					return this;
				}

				@Override
				public X lt(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " < " + value + xReg + " ");
					} else {
						xo.x(" " + property + " < '" + value + "'" + xReg + " ");
					}

					return this;
				}

				@Override
				public X lte(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " <= " + value + xReg + " ");
					} else {
						xo.x(" " + property + " <= '" + value + "'" + xReg + " ");
					}

					return this;
				}

				@Override
				public X gt(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " > " + value + xReg + " ");
					} else {
						xo.x(" " + property + " > '" + value + "'" + xReg + " ");
					}

					return this;
				}

				@Override
				public X gte(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " >= " + value + xReg + " ");
					} else {
						xo.x(" " + property + " >= '" + value + "'" + xReg + " ");
					}

					return this;
				}

				@Override
				public X not(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					value = convertDate(value);

					if (isNumber(value)) {
						xo.x(" " + property + " <> " + value + xReg + " ");
					} else {
						xo.x(" " + property + " <> '" + value + "'" + xReg + " ");
					}

					return this;
				}

				@Override
				public X like(String property, Object value) {

					check(property);

					if (value == null) {
						xo.isCancel = true;
						return this;
					}
					if (isBaseType_0(property, value)) {
						xo.isCancel = true;
						return this;
					}
					if (isNullOrEmpty(value)) {
						xo.isCancel = true;
						return this;
					}

					xo.x(" " + property + " = '%" + value + "%'" + xReg + " ");

					return this;
				}

				@Override
				public CriteriaBuilder y() {

					criteria.setXing(false);

					if (!xo.isCancel) {
						xo.x("");
						String str = xo.get();
						str = str.replace("#and#", "").replace("#or#", "");
						getTempList().add(str);
					}

					System.out.println("instance" + instance);
					return instance;
				}

			};

			criteria.setXing(true);

			return x;
		}

	};

	private CriteriaBuilder() {
		this.instance = this;
	}

	private CriteriaBuilder(Criteria criteria) {
		this.criteria = criteria;
		this.instance = this;
	}

	public static CriteriaBuilder build(Class<?> clz) {
		Criteria criteria = new Criteria();
		criteria.setClz(clz);
		CriteriaBuilder builder = new CriteriaBuilder(criteria);

		if (criteria.getParsed() == null) {
			Parsed parsed = Parser.get(clz);
			criteria.setParsed(parsed);
		}

		return builder;
	}

	public static CriteriaBuilder build(Class<?> clz, RequestMapped ro) {
		CriteriaBuilder builder = build(clz);
		if (ro != null) {
			ro.build("", builder, ro.getRequestMap());
		}
		return builder;
	}

	/**
	 * <br>
	 * 简单的,前端可定义的查询表达式<br>
	 * 此接口只适合前端按criteria语法传参数, 不支持连表查询, 不支持前端定义查询条件类<br>
	 * 时间必须是13位long型<br>
	 * <br>
	 * 
	 * @param clz
	 * @param map
	 */
	public CriteriaBuilder build(Class<?> clz, Map<String, String> expressMap) {

		CriteriaBuilder builder = build(clz);
		init(expressMap);
		return builder;
	}

	public static Fetchable buildFetchable(Class<?> clz, Fetched ro) {
		CriteriaBuilder b = new CriteriaBuilder();
		Fetchable builder = b.new Fetchable(clz, ro);

		if (ro != null) {
			if (ro instanceof RequestMapped) {
				RequestMapped requestMapped = (RequestMapped) ro;
				requestMapped.build("", builder, requestMapped.getRequestMap());
			}
		}

		return builder;
	}

	protected CriteriaBuilder(boolean isDev, Map<String, String> expressionMap) {

		if (!isDev) {
			throw new RuntimeException("Criteria(boolean isDev, Map<String, String> expressMap) only for DEV model");
		}

		String clzName = expressionMap.get("class.name");

		try {
			this.criteria.setClz(Class.forName(clzName));
		} catch (ClassNotFoundException e) {

			e.printStackTrace();

			throw new RuntimeException("criteria.express unexpected, check class.name");
		}

		init(expressionMap);
	}

	private void init(Map<String, String> expressionMap) {
		if (expressionMap == null)
			return;

		Parsed parsed = Parser.get(criteria.getClz());

		for (String key : expressionMap.keySet()) {

			String value = expressionMap.get(key);
			if (StringUtil.isNullOrEmpty(value))
				continue;

			if (!key.contains(".")) {
				if (key.equals(ORDER_BY))
					orderBy(value);
				else if (key.equals(SC))
					criteria.setSc(value);
				else if (key.equals(GROUP_BY))
					criteria.setGroupBy(value);
				continue;
			}

			String property = getProperty(key);
			String express = getExpress(key);

			BeanElement be = parsed.getElement(property);
			if (be == null)
				continue;

			Class<?> clz = be.clz;

			if (express.equals("in")) {

				List<Object> list = new ArrayList<Object>();

				String[] arr = value.split("_");
				for (String s : arr) {
					Object v = get(clz, s);
					list.add(v);
				}

				criteria.getInMap().put(property, list);

				continue;
			} else if (express.equals("not in")) {
				List<Object> list = new ArrayList<Object>();

				String[] arr = value.split("_");
				for (String s : arr) {
					Object v = get(clz, s);
					list.add(v);
				}
				criteria.getNotInMap().put(property, list);

				continue;
			}

			if ("like".equals(express)) {
				String v = "%" + value + "%";
				criteria.getAndMap().put(property + " " + express + " ", v);
			} else {

				if (express.endsWith(X)) {

					x(value);

					continue;
				}

				Object v = get(clz, value);
				criteria.getAndMap().put(property + " " + express + " ", v);
			}

		}
	}

	private Object get(Class<?> clz, String value) {
		Object v = null;
		if (clz == String.class) {
			v = value;
		} else if (clz == int.class || clz == Integer.class) {
			v = Integer.valueOf(value);
		} else if (clz == long.class || clz == Long.class) {
			v = Long.valueOf(value);
		} else if (clz == double.class || clz == Double.class) {
			v = Double.valueOf(value);
		} else if (clz == float.class || clz == Float.class) {
			v = Float.valueOf(value);
		} else if (clz == boolean.class || clz == Boolean.class) {
			if (value.equals("1") || value.equals("true") || value.equals("TRUE")) {
				v = true;
			} else {
				v = false;
			}
		} else if (clz == short.class || clz == Short.class) {
			v = Short.valueOf(value);
		} else if (clz == Date.class) {
			if (value.contains("-")) {
				v = TimeUtil.getDate(value);
			} else {
				long time = Long.valueOf(value);
				v = new Date(time);
			}
		}
		return v;
	}

	private String getExpress(String str) {
		String s = str.substring(str.indexOf(".") + 1);
		return expressionMap.get(s);
	}

	private String getProperty(String str) {
		return str.substring(0, str.indexOf("."));
	}

	protected String getAliasPoint(String property) {
		return property.replace("->", ".");
	}

	private void resetAnd() {
		this.tempMap = this.criteria.getAndMap();
		this.tempBetweenMap = this.criteria.getBetweenMap();
		this.tempInMap = this.criteria.getInMap();
		this.tempNotInMap = this.criteria.getNotInMap();
		this.tempList = this.criteria.getAndList();
	}

	private void resetOr() {
		this.tempMap = this.criteria.getOrMap();
		this.tempBetweenMap = this.criteria.getBetweenMap_Or();
		this.tempInMap = this.criteria.getInMap_Or();
		this.tempNotInMap = this.criteria.getNotInMap_Or();
		this.tempList = this.criteria.getOrList();
	}

	public I and() {

		if (this.criteria.isXing()) {
			throw new RuntimeException("isXing, end X expression by y()");
		}

		resetAnd();
		return c;
	}

	public I or() {

		if (this.criteria.isXing()) {
			throw new RuntimeException("isXing, end X expression by y()");
		}

		resetOr();
		return c;
	}

	public O orderBy(String property) {

		// check(property);

		property = getAliasPoint(property);
		criteria.getOrderByList().add(property);

		O o = new O() {

			@Override
			public void sc(String ascOrDesc) {
				criteria.setSc(ascOrDesc);
			}

			@Override
			public O orderBy(String property) {
				// check(property);
				property = getAliasPoint(property);
				if (!criteria.getOrderByList().contains(property)) {
					criteria.getOrderByList().add(property);
				}
				return this;
			}

		};

		return o;
	}

	public CriteriaBuilder groupBy(String property) {

		check(property);

		property = getAliasPoint(property);
		this.criteria.setGroupBy(property);

		return this;
	}

	public H having() {

		H h = new H() {

			@Override
			public CriteriaBuilder eq(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isBaseType_0(property, value))
					return instance;
				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(EQ);

				value = convertDate(value);

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

			@Override
			public CriteriaBuilder lt(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isBaseType_0(property, value))
					return instance;
				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(LT);
				criteria.getHavingMap().put(property + express, value);

				value = convertDate(value);

				return instance;
			}

			@Override
			public CriteriaBuilder lte(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isBaseType_0(property, value))
					return instance;
				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(LTE);

				value = convertDate(value);

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

			@Override
			public CriteriaBuilder gt(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(GT);

				value = convertDate(value);

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

			@Override
			public CriteriaBuilder gte(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isBaseType_0(property, value))
					return instance;
				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(GTE);

				value = convertDate(value);

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

			@Override
			public CriteriaBuilder not(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isBaseType_0(property, value))
					return instance;
				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(NOT);

				value = convertDate(value);

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

			@Override
			public CriteriaBuilder like(String property, Object value) {

				// check(property);

				if (value == null)
					return instance;

				if (isNullOrEmpty(value))
					return instance;

				property = getAliasPoint(property);
				String express = " " + expressionMap.get(LIKE);
				value = "%" + value + "%";

				criteria.getHavingMap().put(property + express, value);

				return instance;
			}

		};

		return h;
	}

	protected String getX(String xExpression) {
		if (xExpression.contains("(") || xExpression.contains(")") || xExpression.contains(" ")
				|| xExpression.contains("%"))
			throw new RuntimeException("unknow X-expression for x7 repository sql: " + xExpression);
		xExpression = xExpression.replace("[", "(");
		xExpression = xExpression.replace("]", ")");
		xExpression = xExpression.replace(".", " ");
		xExpression = xExpression.replace("->", ".");

		String v1 = xMap.get(LTE);
		xExpression = xExpression.replace(LTE, v1);
		String v2 = xMap.get(GTE);
		xExpression = xExpression.replace(GTE, v2);
		String v3 = xMap.get(NOT);
		xExpression = xExpression.replace(NOT, v3);

		for (String k : xMap.keySet()) {
			String v = xMap.get(k);
			if (xExpression.contains(k)) {
				if (LIKE.equals(k)) {
					xExpression = xExpression.replace("'*", "'%");
					xExpression = xExpression.replace("*'", "%'");
				} else {
					xExpression = xExpression.replace(k, v);
				}
			}
		}

		for (String k : nullMap.keySet()) {
			String v = nullMap.get(k);
			if (xExpression.contains(k)) {
				xExpression = xExpression.replace(k, v);
			}
		}
		return xExpression;
	}

	/**
	 * x express:<br>
	 * [name.isNotNull.and.name.like.'xxx*']
	 * 
	 * @param xExpression
	 */
	private void x(String xExpression) {
		if (StringUtil.isNullOrEmpty(xExpression))
			return;
		xExpression = getX(xExpression);

		criteria.getAndList().add(xExpression);
	}

	public Class<?> getClz() {
		return this.criteria.getClz();
	}

	public List<Object> getValueList() {
		return this.criteria.getValueList();
	}

	public List<String> listAllColumn() {
		List<String> list = new ArrayList<String>();
		Parsed parsed = Parser.get(this.criteria.getClz());

		for (BeanElement be : parsed.getBeanElementList()) {
			list.add(be.getMapper());
		}
		return list;
	}

	private static void appendWhere(StringBuilder sb, Criteria criteria, boolean isAnd) {
		if (criteria.isNotFirstCondition()) {
			if (isAnd)
				sb.append(AND);
			else
				sb.append(OR);
		} else {
			sb.append(WHERE);
			criteria.setNotFirstCondition(true);
		}
	}

	public static String[] parse(Criteria criteria) {

		StringBuilder sb = new StringBuilder();

		/*
		 * select column
		 */
		select(sb, criteria);

		/*
		 * from table
		 */
		boolean hasSourceScript = criteria.sourceScript(sb);

		/*
		 * join on
		 */
		// criteria.fetchScript(sb);

		/*
		 * in query
		 */
		between(sb, criteria, criteria.getBetweenMap(), true);

		/*
		 * in query
		 */
		inOrNotIn(sb, criteria, criteria.getInMap(), true);

		/*
		 * not in query
		 */
		inOrNotIn(sb, criteria, criteria.getNotInMap(), true);

		/*
		 * and
		 */
		andOr(sb, criteria, criteria.getAndMap(), true);

		/*
		 * StringList
		 */
		x(sb, criteria, criteria.getAndList(), true);

		/*
		 * or
		 */
		andOr(sb, criteria, criteria.getOrMap(), false);

		/*
		 * StringList
		 */
		x(sb, criteria, criteria.getOrList(), false);
		/*
		 * in query
		 */
		between(sb, criteria, criteria.getBetweenMap_Or(), false);

		/*
		 * in query
		 */
		inOrNotIn(sb, criteria, criteria.getInMap_Or(), false);

		/*
		 * not in query
		 */
		inOrNotIn(sb, criteria, criteria.getNotInMap_Or(), false);

		/*
		 * groupBy
		 */
		boolean isGrouped = groupBy(sb, criteria);

		having(sb, criteria);

		/*
		 * sort
		 */
		sort(sb, criteria);

		String sql = sb.toString();

		String column = criteria.resultAllScript();

		String[] sqlArr = new String[3];
		String str = sql.replace(Persistence.PAGINATION, column);
		sqlArr[1] = str;
		if (isGrouped) {
			String groupBy = criteria.getGroupBy();
			str = str.replaceAll(" +", " ");
			str = str.replace(") count", ") _count").replace(")count", ") _count");
			str = str.replace("count (", "count(");
			str = str.replace(" count ", " _count ");
			sqlArr[0] = "select count(tc." + groupBy + ") count from (" + str + ") tc";
		} else {
			sqlArr[0] = sql.replace(Persistence.PAGINATION, "COUNT(*) count");
		}
		sqlArr[2] = sql;

		if (hasSourceScript) {
			//sqlArr[1]: core sql			
			Map<String,List<String>> map = new HashMap<>();
			{
				String[] arr = sqlArr[1].split(" ");
				for (String ele : arr) {
					if (ele.contains(".")){
						ele = ele.replace(",", "");
						ele = ele.trim();
						String[] tc = ele.split("\\.");
						List<String> list = map.get(tc[0]);
						if (list == null){
							list = new ArrayList<>();
							map.put(tc[0], list);
						}
						list.add(tc[1]);
					}
				}
			}
			FetchMapper fetchMapper = new FetchMapper();
			criteria.setFetchMapper(fetchMapper);
			Map<String,String> clzTableMapper = new HashMap<String,String>();
			{
				Set<Entry<String,List<String>>> set = map.entrySet();
				for (Entry<String,List<String>> entry : set) {
					String key = entry.getKey();
					List<String> list = entry.getValue();
					Parsed parsed = Parser.get(key);
					String tableName = parsed.getTableName();
					clzTableMapper.put(key, tableName);//clzName, tableName
					for (String property : list){
						String mapper = parsed.getMapper(property);
						fetchMapper.put(key + "." +property, tableName + "." + mapper);
					}
				}
			}
			System.out.println(fetchMapper);
			for (int i=0;i<3;i++) {
				String temp = sqlArr[i];
				for (String property : fetchMapper.getPropertyMapperMap().keySet()){
					temp = temp.replace(property, fetchMapper.mapper(property));
				}
				for (String clzName : clzTableMapper.keySet()){
					String tableName = clzTableMapper.get(clzName);
					temp = BeanUtilX.mapperName(temp, clzName, tableName);
				}
				sqlArr[i] = temp;
			}
			
			
		} else {
			Parsed parsed = Parser.get(criteria.getClz());
			for (int i=0;i<3;i++) {
				sqlArr[i] = BeanUtilX.mapper(sqlArr[i], parsed);
			}
		}

		System.out.println(sqlArr[0]);
		System.out.println(sqlArr[1]);

		return sqlArr;
	}

	private static void select(StringBuilder sb, Criteria criteria) {
		sb.append("SELECT").append(space).append(Persistence.PAGINATION);
	}

	private static void sort(StringBuilder sb, Criteria criteria) {
		if (!criteria.getOrderByList().isEmpty()) {
			sb.append(" order by ");

			int ii = 0;
			int ss = criteria.getOrderByList().size();
			for (String orderBy : criteria.getOrderByList()) {
				ii++;
				sb.append(orderBy);
				if (ii < ss) {
					sb.append(comma);
				}

			}
			sb.append(space).append(criteria.getSc());
		}
	}

	private static boolean groupBy(StringBuilder sb, Criteria criteria) {
		String groupBy = criteria.getGroupBy();
		if (groupBy != null && !groupBy.trim().equals("")) {
			sb.append(" group by ").append(groupBy);
			return true;
		}
		return false;
	}

	private static void having(StringBuilder sb, Criteria criteria) {
		Map<String, Object> havingMap = criteria.getHavingMap();
		if (havingMap.isEmpty())
			return;
		sb.append(" having ");
		for (String key : havingMap.keySet()) {
			sb.append(key).append(havingMap.get(key));
		}
	}

	private static void andOr(StringBuilder sb, Criteria criteria, Map<String, Object> map, boolean isAnd) {
		List<Object> valueList = criteria.getValueList();
		for (String key : map.keySet()) {
			appendWhere(sb, criteria, isAnd);
			sb.append(key).append(space).append("?");
			if (key.contains("(")) {
				sb.append(")");
			}

			Object obj = map.get(key);
			valueList.add(obj);
		}
	}

	private static void x(StringBuilder sb, Criteria criteria, List<String> list, boolean isAnd) {
		for (String condition : list) {
			condition = condition.replace("drop", " ").replace("delete", " ").replace("insert", " ").replace(";", "");
			appendWhere(sb, criteria, isAnd);
			sb.append(space).append(condition);
		}
	}

	private static void between(StringBuilder sb, Criteria criteria, Map<String, MinMax> map, boolean isAnd) {
		for (String key : map.keySet()) {
			MinMax minMax = map.get(key);

			Object v = minMax.getMin();

			appendWhere(sb, criteria, isAnd);
			sb.append(key).append(space).append(" between ");

			sb.append(" ? ").append(" and ").append(" ? ");

			List<Object> valueList = criteria.getValueList();
			valueList.add(minMax.getMin());
			valueList.add(minMax.getMax());
		}
	}

	private static void inOrNotIn(StringBuilder sb, Criteria criteria, Map<String, List<Object>> map, boolean isAnd) {

		for (String key : map.keySet()) {
			List<Object> inList = map.get(key);
			if (inList == null || inList.isEmpty())
				continue;
			appendWhere(sb, criteria, isAnd);
			sb.append(key).append(space);

			Object v = inList.get(0);

			Class<?> vType = v.getClass();

			boolean isNumber = (vType == long.class || vType == int.class || vType == Long.class
					|| vType == Integer.class);

			sb.append(" in (");

			int length = inList.size();
			if (isNumber) {
				for (int j = 0; j < length; j++) {
					Object id = inList.get(j);
					if (id == null)
						continue;
					sb.append(id);
					if (j < length - 1) {
						sb.append(",");
					}
				}
			} else {
				for (int j = 0; j < length; j++) {
					Object id = inList.get(j);
					if (id == null || StringUtil.isNullOrEmpty(id.toString()))
						continue;
					sb.append("'").append(id).append("'");
					if (j < length - 1) {
						sb.append(",");
					}
				}
			}

			sb.append(")");

		}
	}

	protected static void fetchSql(StringBuilder sb, Criteria criteria) {

	}

	private boolean isNumber(Object v) {
		Class<?> vType = v.getClass();

		boolean isNumber = (vType == long.class || vType == int.class || vType == Long.class || vType == Integer.class
				|| vType == double.class || vType == Double.class || vType == float.class || vType == Float.class);

		return isNumber;
	}

	private void check(String property) {

		if (isFetchable) {
			String str = null;
			if (property.contains(" ")) {
				String[] arr = property.split(" ");
				str = arr[0];
			} else {
				str = property;
			}
			if (str.contains(".")) {
				str = str.replace(".", "->");
				String[] xxx = str.split("->");
				if (xxx.length == 1)
					property = xxx[0];
				else
					property = xxx[1];
			} else {
				property = str;
			}

		} else {

			BeanElement be = criteria.getParsed().getElement(property);

			if (be == null) {
				throw new RuntimeException("property = " + property + ", not in " + criteria.getClz());
			}

		}
	}

	private BeanElement getBeanElement(String property) {

		String str = null;
		if (property.contains(" ")) {
			String[] arr = property.split(" ");
			str = arr[0];
		} else {
			str = property;
		}
		if (str.contains(".")) {
			str = str.replace(".", "->");
			String[] xxx = str.split("->");
			if (xxx.length == 1)
				property = xxx[0];
			else
				property = xxx[1];
		} else {
			property = str;
		}

		BeanElement be = criteria.getParsed().getElement(property);

		return be;

	}

	private boolean isBaseType_0(String property, Object v) {

		BeanElement be = getBeanElement(property);

		if (be == null) {
			String s = v.toString();
			boolean isNumeric = NumberUtil.isNumeric(s);
			if (isNumeric) {
				if (s.contains(".")) {
					return Double.valueOf(s) == 0;
				}
				return Long.valueOf(s) == 0;
			}
			return false;
		}

		Class<?> vType = be.clz;

		String s = v.toString();

		if (vType == int.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Integer.valueOf(s) == 0;
		}
		if (vType == long.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Long.valueOf(s) == 0;
		}
		if (vType == float.class) {
			return Float.valueOf(s) == 0;
		}
		if (vType == double.class) {
			return Double.valueOf(s) == 0;
		}
		if (vType == boolean.class) {
			if (s.contains(".")) {
				s = s.substring(0, s.indexOf("."));
			}
			return Integer.valueOf(s) == 0;
		}

		return false;
	}

	private boolean isNullOrEmpty(Object v) {

		Class<?> vType = v.getClass();

		if (vType == String.class) {
			return StringUtil.isNullOrEmpty(v.toString());
		}

		return false;
	}

	private Object convertDate(Object v) {
		if (v instanceof Date) {
			Date d = (Date) v;
			return TimeUtil.format(d);
		}

		return v;
	}

	public class XObject {

		private String str;
		private boolean isCancel;

		public XObject(String x) {
			this.str = x;
		}

		public String get() {
			return str;
		}

		public void x(String str) {
			this.str = this.str.replace("#and#", "and").replace("#or#", "or");
			this.str = this.str.replace(xReg, str);
		}
	}

	public interface I {

		CriteriaBuilder eq(String property, Object value);

		CriteriaBuilder lt(String property, Object value);

		CriteriaBuilder lte(String property, Object value);

		CriteriaBuilder gt(String property, Object value);

		CriteriaBuilder gte(String property, Object value);

		CriteriaBuilder not(String property, Object value);

		CriteriaBuilder like(String property, Object value);

		CriteriaBuilder between(String property, Object min, Object max);

		CriteriaBuilder in(String property, List<Object> list);

		CriteriaBuilder notIn(String property, List<Object> list);

		/**
		 * x express:<br>
		 * [name.isNotNull.and.name.like.'xxx*']<br>
		 * "refreshTime.lte.endTime"<br>
		 * 
		 * @param xExpression
		 */
		CriteriaBuilder x(String xExpression);

		X x();
	}

	/**
	 * x 表达式<br>
	 * String expression = "[name.isNotNull.and.name.like.'xxx*']";<br>
	 * criteria.and().x(expression); <br>
	 * //或者 criteria.and().x().isNotNull("name").and().like("name","xxx").y();
	 * <br>
	 * x(), x表达式开头<br>
	 * y(), x表达式结束<br>
	 * 
	 * 局限: 如果基本类型参数值为0，或非基本类型参数值为null, 整个x表达式会失效<br>
	 * 如果在应用场景下无法满足需求，请使用x(expression)<br>
	 * 
	 * @author Sim
	 *
	 */
	public interface X {
		X x();

		X and();

		X or();

		X isNull(String property);

		X isNotNull(String property);

		X eq(String property, Object value);

		X lt(String property, Object value);

		X lte(String property, Object value);

		X gt(String property, Object value);

		X gte(String property, Object value);

		X not(String property, Object value);

		X like(String property, Object value);

		/**
		 * finish
		 */
		CriteriaBuilder y();
	}

	public interface O {
		void sc(String sc);

		O orderBy(String orderBy);
	}

	/**
	 * 
	 * simple having api<BR>
	 * 
	 * @author Sim
	 *
	 */
	public interface H {
		CriteriaBuilder eq(String property, Object value);

		CriteriaBuilder lt(String property, Object value);

		CriteriaBuilder lte(String property, Object value);

		CriteriaBuilder gt(String property, Object value);

		CriteriaBuilder gte(String property, Object value);

		CriteriaBuilder not(String property, Object value);

		CriteriaBuilder like(String property, Object value);
	}

	public Criteria get() {
		return this.criteria;
	}

	private boolean isFetchable = false;

	public class Fetchable extends CriteriaBuilder {

		@Override
		public Fetch get() {
			return (Fetch) super.get();
		}

		private void init() {
			super.isFetchable = true;
			super.instance = this;
			Criteria c = new Criteria();
			Criteria.Fetch join = c.new Fetch();
			super.criteria = join;
		}

		private void init(Class<?> clz) {
			Criteria.Fetch cj = (Criteria.Fetch) super.criteria;
			cj.setClz(clz);
			Parsed parsed = Parser.get(clz);
			cj.setParsed(parsed);
		}

		public Fetchable(Class<?> clz) {
			init();
			init(clz);
		}

		public Fetchable(Class<?> clz, Fetched fetchResult) {

			init();
			init(clz);

			xAddResultKey(fetchResult);

		}

		protected Fetchable(Map<String, String> expressMap) {

			init();

			String clzName = expressMap.get("class.name");

			try {
				Class<?> clz = Class.forName(clzName);
				init(clz);
			} catch (ClassNotFoundException e) {

				e.printStackTrace();

				throw new RuntimeException("criteria.express unexpected, check class.name");
			}

			super.init(expressMap);
		}

		private Criteria.Fetch getCriteriaFetch() {
			return (Criteria.Fetch) super.criteria;
		}

		/**
		 * t->name<br>
		 * t->name.as.ne<br>
		 * 
		 * @param xExpression
		 */
		public void xAddResultKey(String xExpression) {
			getCriteriaFetch().getResultList().add(xExpression);
		}

		/**
		 * Not on Alia Name
		 * 
		 * @param xExpressionList
		 */
		public void xAddResultKey(List<String> xExpressionList) {
			for (String xExpression : xExpressionList) {
				getCriteriaFetch().getResultList().add(xExpression);
			}
		}

		private void xAddResultKey(Fetched fetchResutl) {
			if (fetchResutl == null)
				return;
			Map<String, Object> resultObjMap = fetchResutl.getResultKeyMap();
			if (resultObjMap == null || resultObjMap.isEmpty())
				return;
			List<String> xExpressionList = BeanMapUtil.toStringKeyList(resultObjMap);
			xAddResultKey(xExpressionList);
		}

	}

	///////////////////////////////////////////////////////////////////// <BR>
	/////////////////////////// REPOSITORY DEV WEB IO//////////////////// <BR>
	///////////////////////////////////////////////////////////////////// <BR>
	///////////////////////////////////////////////////////////////////// <BR>
	public static Fetchable buildFetchable(boolean isDev, Map<String, String> map) {
		if (!isDev)
			throw new RuntimeException("REPOSITORY WEB IO ONLY FOR DEV");

		CriteriaBuilder builder = new CriteriaBuilder();

		return builder.new Fetchable(map);
	}

	public static class FetchMapper {
		private Map<String,String> propertyMapperMap = new HashMap<String,String>();
		private Map<String,String> mapperPropertyMap = new HashMap<String,String>();
		public Map<String, String> getPropertyMapperMap() {
			return propertyMapperMap;
		}
		public Map<String, String> getMapperPropertyMap() {
			return mapperPropertyMap;
		}
		public void put(String property,String mapper){
			this.propertyMapperMap.put(property, mapper);
			this.mapperPropertyMap.put(mapper, property);	
		}
		
		public String mapper(String property){
			return this.propertyMapperMap.get(property);
		}
		
		public String property(String mapper){
			return this.mapperPropertyMap.get(mapper);
		}
		@Override
		public String toString() {
			return "FetchMapper [propertyMapperMap=" + propertyMapperMap + ", mapperPropertyMap=" + mapperPropertyMap
					+ "]";
		}
	}
}