package x7.core.web;



import x7.core.search.Tag;

/**
 * 单一排序分页
 * @author sim
 *
 * @param <T>
 */
public class PaginationSorted<T> extends Pagination<T>{
	
	public final static String DESC = "DESC";
	public final static String ASC = "ASC";

	private static final long serialVersionUID = -3917421382413274341L;
	private String orderBy;
	private String sc = DESC;
	
	private Tag tag;
	
	public PaginationSorted(){
	}
	
	public PaginationSorted(int page, int rows, String orderBy){
		setPage(page);
		setRows(rows);
		this.orderBy = orderBy;
	}
	
	public PaginationSorted(int page, int rows, String orderBy, String sc){
		setPage(page);
		setRows(rows);
		this.orderBy = orderBy;
		this.sc = sc;
	}
	
	public PaginationSorted(Paged paged){
		setScroll(paged.isScroll());
		if (paged.getPage() > 0)
			setPage(paged.getPage());
		if (paged.getRows() > 0)
			setRows(paged.getRows());
		if (paged.getOrderBy() != null){
			this.orderBy = paged.getOrderBy();
		}
		if (paged.getSc() != null){
			this.sc = paged.getSc();
		}
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
	
	public void setRows(int rows) {
		super.setRows(rows);
	}
	
	public void setPage (int page){
		super.setPage(page);
	}
	
	public long getTotalRows(){
		return super.getTotalRows();
	}

	public void setTotalRows(long totalRows) {
		super.setTotalRows(totalRows);
	}


	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}


	@Override
	public String toString() {
		return "PaginationSorted [orderBy=" + orderBy + ", sc=" + sc + ", rows=" + getRows() + ", page="
				+ getPage() + "]";
	}

}
