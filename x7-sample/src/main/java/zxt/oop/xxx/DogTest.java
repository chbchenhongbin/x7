package zxt.oop.xxx;

import java.io.Serializable;
import java.math.BigDecimal;

import x7.core.repository.Persistence;

public class DogTest implements Serializable{

	@Persistence(key=Persistence.KEY_ONE)
	private long id;
	private long petId;
	private String userName;
	private BigDecimal number;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPetId() {
		return petId;
	}
	public void setPetId(long petId) {
		this.petId = petId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getNumber() {
		return number;
	}
	public void setNumber(BigDecimal number) {
		this.number = number;
	}
	@Override
	public String toString() {
		return "DogTest [id=" + id + ", petId=" + petId + ", userName=" + userName + ", number=" + number + "]";
	}

}
