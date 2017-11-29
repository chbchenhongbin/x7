package x7.core.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TagCondition implements Serializable{

	private static final long serialVersionUID = 8909511910631113572L;
	private Map<String, Long> map = new HashMap<String,Long>();
	public Map<String, Long> getMap() {
		return map;
	}
	public void setMap(Map<String, Long> map) {
		this.map = map;
	}
	
	public Long getId(String tagKey){
		return this.map.get(tagKey);
	}
	@Override
	public String toString() {
		return "TagCondition [map=" + map + "]";
	}
}
