package x7.core.bean;

import java.util.ArrayList;
import java.util.List;

public class Para {

	private Class type;
	private List<Class> genericTypeList = new ArrayList<Class>();
	private String name;
	private Object value;
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public List<Class> getGenericTypeList() {
		return genericTypeList;
	}
	public void setGenericTypeList(List<Class> genericTypeList) {
		this.genericTypeList = genericTypeList;
	}
	@Override
	public String toString() {
		return "Para [type=" + type + ", genericTypeList=" + genericTypeList + ", name=" + name + ", value=" + value + "]";
	}

}
