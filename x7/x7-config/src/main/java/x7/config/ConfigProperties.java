package x7.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("x7.config")
public class ConfigProperties {

	private boolean centralized;
	private String space;
	private String localAddress;
	private String remoteAddress;
	public boolean isCentralized() {
		return centralized;
	}
	public void setCentralized(boolean centralized) {
		this.centralized = centralized;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
}
