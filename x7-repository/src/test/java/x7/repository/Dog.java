package x7.repository;

import java.io.Serializable;

import x7.core.repository.Persistence;

public class Dog implements Serializable{

	@Persistence(key=Persistence.KEY_ONE)
	private long id;
	private long petId;
	private String name;
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
	@Override
	public String toString() {
		return "Dog [id=" + id + ", petId=" + petId + ", name=" + name + "]";
	}
}
