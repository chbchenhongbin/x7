package x7.repository.bean;

import java.io.Serializable;

import x7.core.repository.Persistence;

public class Dog implements Serializable{

	@Persistence(key=Persistence.KEY_ONE)
	private long id;
	private long petId;
	private String name;
	private Boolean isPet;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getPetId() {
		return petId;
	}
	public void setPetId(long petId) {
		this.petId = petId;
	}
	public Boolean isPet() {
		return isPet;
	}
	public void setPet(Boolean isPet) {
		this.isPet = isPet;
	}
	
	@Override
	public String toString() {
		return "Dog [id=" + id + ", petId=" + petId + ", name=" + name + ", isPet=" + isPet + "]";
	}

}
