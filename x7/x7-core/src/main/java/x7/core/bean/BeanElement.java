package x7.core.bean;

import java.lang.reflect.Method;

import x7.core.config.Configs;
import x7.core.repository.SqlFieldType;
import x7.core.util.BeanUtil;
import x7.core.util.StringUtil;

public class BeanElement {

	public String property;
	public String setter;
	public String getter;
	@SuppressWarnings("rawtypes")
	public Class clz;
	public int length;
	public String sqlType;

	public String mapper = "";

	public boolean isMobile;
	public boolean isEmail;
	public boolean notNull;

	public Method getMethod;
	public Method setMethod;

	public boolean isJson;
	public Class geneType;

	private String getPrefix() {
		try {
			String prefix = Configs.getString("x7.db.naming.prefix");
			if (StringUtil.isNotNull(prefix))
				return prefix;
		} catch (Exception e) {

		}
		return "";
	}

	public String getProperty() {
		
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public void initMaper(){
		mapper = BeanUtil.getMapper(property);
	}

	public String getMapper() {
		return mapper;
	}

	public String property() {
		return mapper.equals("") ? property : mapper;
	}

	public String getSqlType() {// FIXME
		String name = clz.getName();
		if (name.contains("Date")) {
			return SqlFieldType.DATE;
		} else if (name.contains("String")) {
			return SqlFieldType.VARCHAR;
		} else if (name.contains("int") || name.contains("Integer")) {
			return SqlFieldType.INT;
		} else if (name.contains("long") || name.contains("Long")) {
			return SqlFieldType.LONG;
		} else if (name.contains("double") || name.contains("Double")) {
			return SqlFieldType.DOUBLE;
		} else if (name.contains("float") || name.contains("Float")) {
			return SqlFieldType.FLOAT;
		} else if (name.contains("BigDecimal") ) {
			return SqlFieldType.DECIMAL;
		} else if (name.contains("boolean") || name.contains("Boolean")) {
			return SqlFieldType.BYTE;
		} 
		return sqlType;
	}

	@Override
	public String toString() {
		return "BeanElement [property=" + property + ", setter=" + setter + ", getter=" + getter + ", sqlField="
				+ sqlType + ", clz=" + clz + "]";
	}

	public boolean isPair() {
		if (setter == null)
			return false;
		if (getter == null)
			return false;
		if (getter.startsWith("is")) {
			return setter.substring(3).equals(getter.substring(2));
		}
		return BeanUtil.getProperty(setter).equals(BeanUtil.getProperty(getter));
	}
}
