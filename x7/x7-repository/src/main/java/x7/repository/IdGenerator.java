package x7.repository;

import java.io.Serializable;

import x7.core.repository.Persistence;


public class IdGenerator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4482390783954339652L;
	@Persistence(key = Persistence.KEY_ONE, isNotAutoIncrement = true)
	private String clzName;
	private long maxId;
	public String getClzName() {
		return clzName;
	}
	public void setClzName(String clzName) {
		this.clzName = clzName;
	}
	public long getMaxId() {
		return maxId;
	}
	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}
	@Override
	public String toString() {
		return "IdGenerator [clzName=" + clzName + ", maxId=" + maxId + "]";
	}
}
