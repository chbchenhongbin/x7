package x7.core.web;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import x7.core.bean.CriteriaBuilder;
import x7.core.bean.CriteriaBuilder.X;
import x7.core.util.StringUtil;
import x7.core.util.TimeUtil;

public interface RequestMapped {

	interface CriteriaGrammar {
		String ASC = "asc";
		String DESC = "desc";
		String WHERE = "$where";
		String AND = "$and";
		String OR = "$or";

		String EQ = "$eq";
		String NE = "$ne";
		String LT = "$lt";
		String GT = "$gt";
		String LTE = "$lte";
		String GTE = "$gte";
		String NOT = "$not";
		String LIKE = "$like";
		String SORT = "$sort";
		String EXISTS = "$exists";
		String ORDER_BY = "order by";
		String SC = "sc";
		String GROUP = "$group";
		String BETWEEN = "$between";
		String IN = "$in";
		String NOT_IN = "$nin";
		String IS_NOT_NULL = "is not null";
		String IS_NULL = "is null";
		String WILD_CARD = "*";
		String LEFT_JOIN = "left join";
		String RIGHT_JOIN = "right join";
		String INNER_JOIN = "inner join";
		String ON = "on";
	}

	Map<String, Object> getRequestMap();

	public default void build(String prefix, CriteriaBuilder builder, Map<String, Object> map) {

		for (Entry<String, Object> entry : map.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			/*
			 * 过滤复杂结构
			 */
			if (!key.contains("$")) {
				if (value instanceof String) {
					if (StringUtil.isNotNull(prefix)) {
						key = prefix + "." + key;
					}
					value = TimeUtil.parseDateForJson(value);
					builder.and().eq(key, value);
				} else if (value instanceof Map) {
					Map<String, Object> innerMap = (Map<String, Object>) value;
					if (StringUtil.isNotNull(prefix)) {
						key = prefix + "." + key;
					}
					build(key, builder, innerMap);
					// boolean isNotFirst = false;
					// X x = builder.and().x();
					// for (Entry<String,Object> en : innerMap.entrySet()){
					//
					// String k = en.getKey();
					// Object v = en.getValue();
					// if (v instanceof String) {
					// v = TimeUtil.parseDateForJson(v);
					// }
					// String kx = prefix + "." + k;
					// switch (k) {
					// case CriteriaGrammar.EQ:
					// if (isNotFirst){
					// x.and().eq(kx, v);
					// }else{
					// x.eq(kx, v);
					// }
					// break;
					// case CriteriaGrammar.GT:
					// if (isNotFirst){
					// x.and().gt(kx, v);
					// }else{
					// x.gt(kx, v);
					// }
					// break;
					// case CriteriaGrammar.LT:
					// if (isNotFirst){
					// x.and().lt(kx, v);
					// }else{
					// x.lt(kx, v);
					// }
					// break;
					// case CriteriaGrammar.GTE:
					// if (isNotFirst){
					// x.and().gte(kx, v);
					// }else{
					// x.gte(kx, v);
					// }
					// break;
					// case CriteriaGrammar.LTE:
					// if (isNotFirst){
					// x.and().lte(kx, v);
					// }else{
					// x.lte(kx, v);
					// }
					// break;
					// case CriteriaGrammar.NE:
					// if (isNotFirst){
					// x.and().not(kx, v);
					// }else{
					// x.not(kx, v);
					// }
					// break;
					// case CriteriaGrammar.LIKE:
					// if (isNotFirst){
					// x.and().like(kx, v);
					// }else{
					// x.like(kx, v);
					// }
					// break;
					// case CriteriaGrammar.OR:
					// // FIXME
					// break;
					// }
					// isNotFirst = true;
					// }
					//
					// x.y();

				} else if (value instanceof List) {
					List<Object> list = (List<Object>) value;
					builder.and().in(key, list);
				}
			} else {

				switch (key) {
				case CriteriaGrammar.AND:

					if (value instanceof String) {
						value = TimeUtil.parseDateForJson(value);
						builder.and().eq(prefix, value);
					} else if (value instanceof List) {

						System.out.println("____ LIST" + value);
						List list = (List) value;
						boolean isNotFirst = false;
						X x = builder.and().x();
						for (Object obj : list) {

							
							if (obj instanceof String){
								System.out.println("__________String JSON");
							}
							
							if (obj instanceof Map) {
								
								System.out.println("___________Map");
								
								Map<String,Object> m = (Map<String,Object>) obj;
								

//								String k = en.getKey();
//								Object v = en.getValue();
//								if (v instanceof String) {
//									v = TimeUtil.parseDateForJson(v);
//								}
//								String kx = prefix + "." + k;
//								switch (k) {
//								case CriteriaGrammar.EQ:
//									if (isNotFirst) {
//										x.and().eq(kx, v);
//									} else {
//										x.eq(kx, v);
//									}
//									break;
//								case CriteriaGrammar.GT:
//									if (isNotFirst) {
//										x.and().gt(kx, v);
//									} else {
//										x.gt(kx, v);
//									}
//									break;
//								case CriteriaGrammar.LT:
//									if (isNotFirst) {
//										x.and().lt(kx, v);
//									} else {
//										x.lt(kx, v);
//									}
//									break;
//								case CriteriaGrammar.GTE:
//									if (isNotFirst) {
//										x.and().gte(kx, v);
//									} else {
//										x.gte(kx, v);
//									}
//									break;
//								case CriteriaGrammar.LTE:
//									if (isNotFirst) {
//										x.and().lte(kx, v);
//									} else {
//										x.lte(kx, v);
//									}
//									break;
//								case CriteriaGrammar.NE:
//									if (isNotFirst) {
//										x.and().not(kx, v);
//									} else {
//										x.not(kx, v);
//									}
//									break;
//								case CriteriaGrammar.LIKE:
//									if (isNotFirst) {
//										x.and().like(kx, v);
//									} else {
//										x.like(kx, v);
//									}
//									break;
//								case CriteriaGrammar.OR:
//									// FIXME
//									break;
//								}
							}
							isNotFirst = true;
						}

						x.y();

					} else if (value instanceof Map) {
						Map<String, Object> valueMap = (Map<String, Object>) value;
						for (Entry<String, Object> et : valueMap.entrySet()) {

							String k = et.getKey();
							Object v = et.getValue();

							/*
							 * 过滤复杂结构
							 */
							if (!k.contains("$")) {
								if (v instanceof Map) {
									Map<String, Object> innerMap = (Map<String, Object>) v;
									if (StringUtil.isNotNull(prefix)) {
										k = prefix + "." + k;
									}
									build(k, builder, innerMap);
								}
							} else {

								v = TimeUtil.parseDateForJson(v);

								switch (k) {
								case CriteriaGrammar.EQ:
									builder.and().eq(prefix, v);
									break;
								case CriteriaGrammar.GT:
									builder.and().gt(prefix, v);
									break;
								case CriteriaGrammar.LT:
									builder.and().lt(prefix, v);
									break;
								case CriteriaGrammar.GTE:
									builder.and().gte(prefix, v);
									break;
								case CriteriaGrammar.LTE:
									builder.and().lte(prefix, v);
									break;
								case CriteriaGrammar.NE:
									builder.and().not(prefix, v);
									break;
								case CriteriaGrammar.LIKE:
									builder.and().like(prefix, v);
									break;
								case CriteriaGrammar.OR:
									// FIXME
									break;
								}
							}
						}
					}

					break;
				case CriteriaGrammar.OR:
					if (value instanceof String) {
						value = TimeUtil.parseDateForJson(value);
						builder.or().eq(prefix, value);
					} else if (value instanceof Map) {
						Map<String, Object> valueMap = (Map<String, Object>) value;
						for (Entry<String, Object> et : valueMap.entrySet()) {

							String k = et.getKey();
							Object v = et.getValue();

							/*
							 * 过滤复杂结构
							 */
							if (!k.contains("$")) {
								if (v instanceof Map) {
									Map<String, Object> innerMap = (Map<String, Object>) v;
									if (StringUtil.isNotNull(prefix)) {
										k = prefix + "." + k;
									}
									build(k, builder, innerMap);
								}
							} else {

								v = TimeUtil.parseDateForJson(v);

								switch (k) {
								case CriteriaGrammar.EQ:
									builder.or().eq(prefix, v);
									break;
								case CriteriaGrammar.GT:
									builder.or().gt(prefix, v);
									break;
								case CriteriaGrammar.LT:
									builder.or().lt(prefix, v);
									break;
								case CriteriaGrammar.GTE:
									builder.or().gte(prefix, v);
									break;
								case CriteriaGrammar.LTE:
									builder.or().lte(prefix, v);
									break;
								case CriteriaGrammar.NE:
									builder.or().not(prefix, v);
									break;
								case CriteriaGrammar.LIKE:
									builder.or().like(prefix, v);
									break;
								case CriteriaGrammar.OR:
									// FIXME
									break;
								}
							}
						}
					}
					break;
				default:
					value = TimeUtil.parseDateForJson(value);

					switch (key) {
					case CriteriaGrammar.EQ:
						builder.and().eq(prefix, value);
						break;
					case CriteriaGrammar.GT:
						builder.and().gt(prefix, value);
						break;
					case CriteriaGrammar.LT:
						builder.and().lt(prefix, value);
						break;
					case CriteriaGrammar.GTE:
						builder.and().gte(prefix, value);
						break;
					case CriteriaGrammar.LTE:
						builder.and().lte(prefix, value);
						break;
					case CriteriaGrammar.NE:
						builder.and().not(prefix, value);
						break;
					case CriteriaGrammar.LIKE:
						builder.and().like(prefix, value);
						break;
					case CriteriaGrammar.OR:
						// FIXME
						break;
					}
					break;
				}
			}
		}
	}

}
