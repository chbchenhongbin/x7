package x7.core.web;

public class TokenedAndPagedRo implements Paged, Tokened{

	private long passportId;
	private String token;
	private String device;
	private boolean isScroll;
	private int page;
	private int rows;
	private String orderBy;
	private String sc;
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
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSc() {
		return sc;
	}
	public void setSc(String sc) {
		this.sc = sc;
	}
	public boolean isScroll() {
		return isScroll;
	}
	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}
	@Override
	public String toString() {
		return "BaseRo [passportId=" + passportId + ", token=" + token + ", device=" + device + ", page=" + page
				+ ", rows=" + rows + ", isScroll=" + isScroll +", orderBy=" + orderBy + ", sc=" + sc + "]";
	}
}
