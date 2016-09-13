package x7.repository.bean;

import java.io.Serializable;

import x7.core.repository.Persistence;

public class Pig implements Serializable{

	private static final long serialVersionUID = 6042927018830135959L;

	@Persistence(key = Persistence.KEY_ONE, isNotAutoIncrement = true)
	private long playerId;
	@Persistence(key = Persistence.KEY_TWO)
	private long id;
	private int age;
	private int weight;
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	@Override
	public String toString() {
		return "Pig [playerId=" + playerId + ", id=" + id + ", age=" + age + ", weight=" + weight + "]";
	}
}
