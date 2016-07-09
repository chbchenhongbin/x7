package x7.repository.mysql.sharding;

/**
 * 
 * 数据库路由
 * @author sim
 *
 */
public class Route {
	private String key;
	private String ip;
	private int port;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString() {
		return "Route [key=" + key + ", ip=" + ip + ", port=" + port + "]";
	}

}
