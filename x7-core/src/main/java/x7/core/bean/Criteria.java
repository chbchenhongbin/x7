package x7.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x7.core.bean.CriteriaBuilder.FetchMapper;
import x7.core.util.BeanUtil;

/**
 * 简单的SQL拼接标准化
 * 
 * @author sim
 *
 */
public class Criteria implements Serializable {

	private static final long serialVersionUID = 7088698915888081349L;

	private transient Parsed parsed;
	private transient boolean isNotFirstCondition = false;
	private transient boolean isXing = false;
	private String sc = "DESC";
	private String groupBy;
	private Class<?> clz;
	private Map<String, MinMax> betweenMap = new HashMap<String, MinMax>();
	private Map<String, List<Object>> inMap = new HashMap<String, List<Object>>();
	private Map<String, List<Object>> notInMap = new HashMap<String, List<Object>>();
	private Map<String, MinMax> betweenMap_Or = new HashMap<String, MinMax>();
	private Map<String, List<Object>> inMap_Or = new HashMap<String, List<Object>>();
	private Map<String, List<Object>> notInMap_Or = new HashMap<String, List<Object>>();
	private Map<String, Object> andMap = new HashMap<String, Object>();
	private Map<String, Object> orMap = new HashMap<String, Object>();
	private List<String> andList = new ArrayList<String>();
	private List<String> orList = new ArrayList<String>();
	private List<Object> valueList = new ArrayList<Object>();
	private List<String> orderByList = new ArrayList<String>();
	private Map<String, Object> havingMap = new HashMap<String, Object>();

	private FetchMapper fetchMapper;
	
	public boolean isXing() {
		return isXing;
	}

	public void setXing(boolean isXing) {
		this.isXing = isXing;
	}

	public Map<String, MinMax> getBetweenMap() {
		return betweenMap;
	}

	public void setBetweenMap(Map<String, MinMax> betweenMap) {
		this.betweenMap = betweenMap;
	}

	public Map<String, List<Object>> getInMap() {
		return inMap;
	}

	public void setInMap(Map<String, List<Object>> inMap) {
		this.inMap = inMap;
	}

	public Map<String, List<Object>> getNotInMap() {
		return notInMap;
	}

	public void setNotInMap(Map<String, List<Object>> notInMap) {
		this.notInMap = notInMap;
	}

	public Map<String, MinMax> getBetweenMap_Or() {
		return betweenMap_Or;
	}

	public void setBetweenMap_Or(Map<String, MinMax> betweenMap_Or) {
		this.betweenMap_Or = betweenMap_Or;
	}

	public Map<String, List<Object>> getInMap_Or() {
		return inMap_Or;
	}

	public void setInMap_Or(Map<String, List<Object>> inMap_Or) {
		this.inMap_Or = inMap_Or;
	}

	public Map<String, List<Object>> getNotInMap_Or() {
		return notInMap_Or;
	}

	public void setNotInMap_Or(Map<String, List<Object>> notInMap_Or) {
		this.notInMap_Or = notInMap_Or;
	}

	public Map<String, Object> getAndMap() {
		return andMap;
	}

	public void setAndMap(Map<String, Object> andMap) {
		this.andMap = andMap;
	}

	public Map<String, Object> getOrMap() {
		return orMap;
	}

	public void setOrMap(Map<String, Object> orMap) {
		this.orMap = orMap;
	}

	public List<String> getAndList() {
		return andList;
	}

	public void setAndList(List<String> andList) {
		this.andList = andList;
	}

	public List<String> getOrList() {
		return orList;
	}

	public void setOrList(List<String> orList) {
		this.orList = orList;
	}

	public List<Object> getValueList() {
		return valueList;
	}

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}

	public List<String> getOrderByList() {
		return orderByList;
	}

	public void setOrderByList(List<String> orderByList) {
		this.orderByList = orderByList;
	}

	public Map<String, Object> getHavingMap() {
		return havingMap;
	}

	public void setHavingMap(Map<String, Object> havingMap) {
		this.havingMap = havingMap;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public boolean isNotFirstCondition() {
		return isNotFirstCondition;
	}

	public void setNotFirstCondition(boolean isNotFirstCondition) {
		this.isNotFirstCondition = isNotFirstCondition;
	}

	public Parsed getParsed() {
		return parsed;
	}

	public void setParsed(Parsed parsed) {
		this.parsed = parsed;
	}

	protected boolean sourceScript(StringBuilder sb) {
		sb.append(" ").append(" FROM ").append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
		return false;
	}


	protected String resultAllScript() {
		return "*";
	}

	public FetchMapper getFetchMapper() {
		return fetchMapper;
	}

	public void setFetchMapper(FetchMapper fetchMapper) {
		this.fetchMapper = fetchMapper;
	}

	@Override
	public String toString() {
		return "Criteria [isXing=" + isXing + ", betweenMap=" + betweenMap + ", inMap=" + inMap + ", notInMap="
				+ notInMap + ", betweenMap_Or=" + betweenMap_Or + ", inMap_Or=" + inMap_Or + ", notInMap_Or="
				+ notInMap_Or + ", andMap=" + andMap + ", orMap=" + orMap + ", andList=" + andList + ", orList="
				+ orList + ", valueList=" + valueList  + ", orderByList=" + orderByList
				+ ", havingMap=" + havingMap + ", sc=" + sc + ", groupBy=" + groupBy + ", clz=" + clz + "]";
	}

	/**
	 * 可以连表的SQL拼接标准化, 不支持缓存<br>
	 * 业务系统尽量避免使用连表查询<br>
	 * 互联网业务系统中后期开发必须避免<br>
	 * 适合简单的报表和记录查询<br>
	 * <br>
	 * <br>
	 * xAddKey(String x)<br>
	 * <hr>
	 * <br>
	 * <li>Sample:</li><br>
	 * CriteriaFetchable builder = new
	 * CriteriaFetchable(Cat.class,null);<br>
	 * builder.xAddKey("t->id");<br>
	 * builder.xAddKey("t->name.as.catName");<br>
	 * builder.xAddKey("dog->age.as.dogAge");<br>
	 * builder.orderBy("t->id");<br>
	 * <br>
	 * List<Map<String,Object>> list =
	 * Repositories.getInstance().list(criteria);<br>
	 * <br>
	 * 
	 * @author Sim
	 */
	public class Fetch extends Criteria {

		private List<String> resultList = new ArrayList<String>();
		private String sourceScript;

		public String getResultScript() {
			if (resultList.isEmpty()){
				return "*";
			}else{
				StringBuilder sb = new StringBuilder();
				int i = 0;
				int size = resultList.size() - 1;
				for (String str : resultList){
					String mapper = getFetchMapper().mapper(str);
					sb.append(mapper);
					if (i < size){
						sb.append(",");
					}
					i++;
				}
				return sb.toString();
			}
		}

		public void setSourceScript(String sourceScript) {
			this.sourceScript = sourceScript;
		}
		

		public List<String> getResultList() {
			return resultList;
		}

		public void setResultList(List<String> columnList) {
			this.resultList = columnList;
		}


		@Override
		protected boolean sourceScript(StringBuilder sb) {
			if (sourceScript == null) {
				sb.append(" ").append(" FROM ").append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
				return false;
			} else {
				String temp = sourceScript.trim();
				if (temp.startsWith("FROM") || temp.startsWith("from")) {
					sb.append(sourceScript);
				} else {
					sb.append(" ").append(" FROM ").append(sourceScript);
				}
				return true;
			}
		}


		@Override
		protected String resultAllScript() {
			int size = 0;
			String column = "";
			if (resultList.isEmpty()) {
				column += " * ";
			} else {
				size = resultList.size();
				for (int i = 0; i < size; i++) {
					column = column + " " + resultList.get(i);
					if (i < size - 1) {
						column += ",";
					}
				}
			}
			return column;
		}

		public List<String> listAllResultKey() {
			List<String> list = new ArrayList<String>();
			Parsed parsed = Parser.get(getClz());

			for (BeanElement be : parsed.getBeanElementList()) {
				list.add(be.getMapper());
			}
			return list;
		}

		@Override
		public String toString() {
			return "Fetch [columnList=" + resultList +   ", isXing()="
					+ isXing() + ", getBetweenMap()=" + getBetweenMap() + ", getInMap()=" + getInMap()
					+ ", getNotInMap()=" + getNotInMap() + ", getBetweenMap_Or()=" + getBetweenMap_Or()
					+ ", getInMap_Or()=" + getInMap_Or() + ", getNotInMap_Or()=" + getNotInMap_Or() + ", getAndMap()="
					+ getAndMap() + ", getOrMap()=" + getOrMap() + ", getAndList()=" + getAndList() + ", getOrList()="
					+ getOrList() + ", getValueList()=" + getValueList() 
					+ ", getOrderByList()=" + getOrderByList() + ", getHavingMap()=" + getHavingMap() + ", getSc()="
					+ getSc() + ", getGroupBy()=" + getGroupBy() + ", getClz()=" + getClz() + ", toString()="
					+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
		}

	}
}