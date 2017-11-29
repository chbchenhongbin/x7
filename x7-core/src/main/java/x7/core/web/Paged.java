package x7.core.web;

public interface Paged {

	boolean isScroll();
	int getPage();
	int getRows();
	String getOrderBy();
	String getSc();
}
