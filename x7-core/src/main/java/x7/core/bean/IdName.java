package x7.core.bean;

import java.io.Serializable;

public class IdName implements Serializable{
	private static final long serialVersionUID = 1363888574477299967L;
	private long id;
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
	@Override
	public String toString() {
		return "IdName [id=" + id + ", name=" + name + "]";
	}
}
