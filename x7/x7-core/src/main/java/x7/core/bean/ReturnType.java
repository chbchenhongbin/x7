package x7.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReturnType implements Serializable{

	private Class type;
	private List<Class> genericTypeList = new ArrayList<Class>();
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}
	public List<Class> getGenericTypeList() {
		return genericTypeList;
	}
	public void setGenericTypeList(List<Class> genericTypeList) {
		this.genericTypeList = genericTypeList;
	}
	@Override
	public String toString() {
		return "ReturnType [type=" + type + ", genericTypeList=" + genericTypeList + "]";
	}
}
