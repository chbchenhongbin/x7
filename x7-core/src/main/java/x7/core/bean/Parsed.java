package x7.core.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x7.core.repository.Persistence;
import x7.core.search.TagParsed;


public class Parsed {	
	
	private boolean isChecked = false;
	
	private Class clz;
	private String tableName;
	private boolean isNoSpec = true;

	private final Map<Integer,String> keyMap = new HashMap<Integer,String>();
	private final Map<Integer,Field> keyFieldMap = new HashMap<Integer,Field>();
	
	private List<BeanElement> beanElementList;
	
	private Map<String, BeanElement> elementMap = new HashMap<String, BeanElement>();
	private Map<String,String> propertyMapperMap = new HashMap<String,String>();
	private Map<String,String> mapperPropertyMap = new HashMap<String,String>();

	private boolean isNotAutoIncreament;
	
	private boolean isNoCache;
	
	private List<String> keywordsList = new ArrayList<String>();
	
	private boolean isSearchable;
	
	private Map<String, TagParsed> tagMap = new HashMap();
	
	
	public Class getClz() {
		return clz;
	}

	public void setClz(Class clz) {
		this.clz = clz;
	}
	
	public Parsed(Class clz){
		this.clz = clz;
	}

	public String getId(){
		if (isCombinedKey()){
			return keyMap.get(Persistence.KEY_ONE)+"_"+keyMap.get(Persistence.KEY_TWO);
		}
		return String.valueOf(keyMap.get(Persistence.KEY_ONE));
	}
	
	public BeanElement getElement(String property){
		return elementMap.get(property);
	}

	public Map<String, BeanElement> getElementMap() {
		return elementMap;
	}

	public Map<Integer, String> getKeyMap() {
		return keyMap;
	}
	
	public boolean contains(String property) {
		return this.elementMap.containsKey(property);
	}

	public Map<Integer, Field> getKeyFieldMap() {
		return keyFieldMap;
	}
	
	public Field getKeyField(int index){
		return keyFieldMap.get(index);
	}

	public String getKey(int index){
		if (keyMap.isEmpty() && index == Persistence.KEY_ONE) //DEFAULT
			return "id";
		return keyMap.get(index);
	}
	
	public boolean isCombinedKey(){
		return keyMap.containsKey(Persistence.KEY_ONE) && keyMap.containsKey(Persistence.KEY_TWO);
	}

	public List<BeanElement> getBeanElementList() {
		return beanElementList;
	}

	public void setBeanElementList(List<BeanElement> beanElementList) {
		this.beanElementList = beanElementList;
		for (BeanElement e : this.beanElementList){
			String property = e.getProperty();
			String mapper = e.getMapper();
			this.elementMap.put(property, e);
			this.propertyMapperMap.put(property, mapper);
			this.mapperPropertyMap.put(mapper, property);
		}
		
	}
	
	public boolean isChecked(){
		return this.isChecked;
	}
	
	public void checked(){
		this.isChecked = true;
	}

	public boolean isNotAutoIncreament() {
		return isNotAutoIncreament;
	}

	public void setNotAutoIncreament(boolean isAutoIncreament) {
		this.isNotAutoIncreament = isAutoIncreament;
	}
	
	public boolean isSharding(){
		return keyMap.containsKey(Persistence.KEY_SHARDING);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getClzName() {
		return this.clz.getSimpleName();
	}

	public boolean isNoCache() {
		return isNoCache;
	}

	public void setNoCache(boolean isNoCache) {
		this.isNoCache = isNoCache;
	}

	public List<String> getKeywordsList() {
		return keywordsList;
	}

	public void setKeywordsList(List<String> keywordsList) {
		this.keywordsList = keywordsList;
	}
	
	public boolean isSearchable() {
		return isSearchable;
	}

	public void setSearchable(boolean isSearchable) {
		this.isSearchable = isSearchable;
	}

	public String[] getKeywardsArr(){

		String[] keywordsArr = new String[this.keywordsList.size()];
		this.keywordsList.toArray(keywordsArr);
		
		return keywordsArr;
	}

	public Map<String, TagParsed> getTagMap() {
		return tagMap;
	}

	public void setTagMap(Map<String, TagParsed> tagMap) {
		this.tagMap = tagMap;
	}


	public String getMapper(String property) {
		return propertyMapperMap.get(property);
	}
	
	public String getProperty(String mapper){
		return mapperPropertyMap.get(mapper);
	}

	public Map<String, String> getPropertyMapperMap() {
		return propertyMapperMap;
	}

	public Map<String, String> getMapperPropertyMap() {
		return mapperPropertyMap;
	}

	@Override
	public String toString() {
		return "Parsed [clz=" + clz + ", tableName=" + tableName + ", keyMap=" + keyMap + ", keyFieldMap=" + keyFieldMap
				+ ", beanElementList=" + beanElementList + ", elementMap=" + elementMap + ", isNotAutoIncreament="
				+ isNotAutoIncreament + ", isNoCache=" + isNoCache + ", keywordsList=" + keywordsList
				+ ", isSearchable=" + isSearchable + ", tagMap=" + tagMap +  "]";
	}

	public boolean isNoSpec() {
		return isNoSpec;
	}

	public void setNoSpec(boolean isNoSpec2) {
		this.isNoSpec = isNoSpec2;
		
	}

	
}
