package x7.core.web;

public class TokenedRo implements Tokened{

	private long passportId;//groupId
	private String token;//登录后的我们自己的token
	private String device;// WX | ANDROID | IOS | PC
	public long getPassportId() {
		return passportId;
	}
	public void setPassportId(long passportId) {
		this.passportId = passportId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	@Override
	public String toString() {
		return "BaseRo [passportId=" + passportId + ", token=" + token + ", device=" + device + "]";
	}
}
