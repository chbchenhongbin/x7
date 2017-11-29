package x7.tool.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanTemplate {

	private String packageName;
	private String clzName;
	private List<String> propList = new ArrayList<String>();
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClzName() {
		return clzName;
	}
	public void setClzName(String clzName) {
		this.clzName = clzName;
	}
	public List<String> getPropList() {
		return propList;
	}
	public void setPropList(List<String> propList) {
		this.propList = propList;
	}
	@Override
	public String toString() {
		return "BeanTemplate [packageName=" + packageName + ", clzName=" + clzName + ", propList=" + propList + "]";
	}
	
}
