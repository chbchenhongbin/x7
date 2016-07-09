package x7.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import x7.core.repository.IIndexTyped;
import x7.core.repository.Persistence;

public class DogIndex implements Serializable, IIndexTyped{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5019078538697496581L;

	private Map<String,String> typeMap = new HashMap<String,String>(){
		{
			put("PET", "petId");
		}
	};
	
	@Persistence(key = Persistence.KEY_ONE, isNotAutoIncrement = true)
	private String keyOne; //KeyType.java , KeyType.USER
	private String type;
	@Persistence(key = Persistence.KEY_TWO) //一个人可以发展多个下线
	private long id;
	public String getKeyOne() {
		return keyOne;
	}
	public void setKeyOne(String keyOne) {
		this.keyOne = keyOne;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public Map<String, String> getTypeMap() {
		return this.typeMap;
	}
	@Override
	public String toString() {
		return "DogInex [typeMap=" + typeMap + ", keyOne=" + keyOne + ", type=" + type + ", id=" + id + "]";
	}

	
}
