package x7.core.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Tag implements Serializable{

	private static final long serialVersionUID = 7631076184961243538L;

	private Map<String, Object> map = new HashMap<String, Object>();

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "Tag [map=" + map + "]";
	}
	
}
