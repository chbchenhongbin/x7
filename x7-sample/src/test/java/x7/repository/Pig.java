package x7.repository;

import x7.core.repository.Persistence;

public class Pig {

	@Persistence(key = Persistence.KEY_ONE)
	private long id;
	private String nickName;
	private String pigWeight;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPigWeight() {
		return pigWeight;
	}
	public void setPigWeight(String pigWeight) {
		this.pigWeight = pigWeight;
	}
}
