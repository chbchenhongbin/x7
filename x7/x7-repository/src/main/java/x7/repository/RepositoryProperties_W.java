package x7.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("x7.db")
public class RepositoryProperties_W {

	private String driver;
	private String url;
	private String address;
	private String name;
	private String user;
	private String password;
	private long min;
	private long max;
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getMin() {
		return min;
	}
	public void setMin(long min) {
		this.min = min;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	@Override
	public String toString() {
		return "RepositoryProperties_W [driver=" + driver + ", url=" + url + ", address=" + address + ", name=" + name
				+ ", user=" + user + ", password=" + password + ", min=" + min + ", max=" + max + "]";
	}

}
