package x7.core.web;

public class PagedRo implements Paged{

	private boolean isScroll;
	private int page;
	private int rows;
	private String orderBy;
	private String sc;
	public boolean isScroll() {
		return isScroll;
	}
	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
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
	@Override
	public String toString() {
		return "PagedRo [isScroll=" + isScroll + ", page=" + page + ", rows=" + rows + ", orderBy=" + orderBy + ", sc="
				+ sc + "]";
	}
}
