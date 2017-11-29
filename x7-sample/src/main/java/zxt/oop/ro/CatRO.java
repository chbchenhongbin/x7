package zxt.oop.ro;

import java.util.HashMap;
import java.util.Map;

import x7.core.web.Fetched;
import x7.core.web.TokenedAndPagedRo;

public class CatRO extends TokenedAndPagedRo implements Fetched{

	private String catFriendName;
	
	private Map<String, Object> resultKeyMap = new HashMap<String,Object>();
	
	
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
	}
	public void setResultKeyMap(Map<String, Object> resultKeyMap) {
		this.resultKeyMap = resultKeyMap;
	}
	@Override
	public Map<String, Object> getResultKeyMap() {
		return resultKeyMap;
	}

}
