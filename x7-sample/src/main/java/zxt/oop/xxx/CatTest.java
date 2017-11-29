package zxt.oop.xxx;

import x7.core.repository.Persistence;

public class CatTest {

	@Persistence(key = Persistence.KEY_ONE)
	private long id;
	private long dogId;
	private String catFriendName;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDogId() {
		return dogId;
	}
	public void setDogId(long dogId) {
		this.dogId = dogId;
	}
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
	}
	@Override
	public String toString() {
		return "CatTest [id=" + id + ", dogId=" + dogId + ", catFriendName=" + catFriendName + "]";
	}
}
