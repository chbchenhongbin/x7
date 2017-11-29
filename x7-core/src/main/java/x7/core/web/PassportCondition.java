package x7.core.web;

import java.io.Serializable;

public class PassportCondition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3614725530323252344L;

	private long passportId;
	private long subId;
	private String name;
	private String token;
	private String passportType;
	private String device;
	public long getPassportId() {
		return passportId;
	}
	public void setPassportId(long id) {
		this.passportId = id;
	}
	public long getSubId() {
		return subId;
	}
	public void setSubId(long subId) {
		this.subId = subId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getType() {
		return passportType;
	}
	public void setType(String type) {
		this.passportType = type;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	@Override
	public String toString() {
		return "PassportCondition [id=" + passportId + ", subId=" + subId + ", name=" + name + ", token=" + token + ", type="
				+ passportType + ", device=" + device + "]";
	}
}
