package x7.core.search;

import java.io.Serializable;

public class TagVo implements Serializable{

	private static final long serialVersionUID = -2146634730861706231L;
	private long id;
	private String name;
	private String imgUrl;
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

	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	@Override
	public String toString() {
		return "TagVo [id=" + id + ", name=" + name  + ", imgUrl=" + imgUrl + "]";
	}
}
