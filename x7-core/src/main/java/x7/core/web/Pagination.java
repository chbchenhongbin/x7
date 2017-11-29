package x7.core.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pagination<T> implements Serializable{

	private static final long serialVersionUID = 5029894223695629135L;
	private int rows = 20;
	private int page = 1;
	private long totalRows = -1;
	private List<T> list = new ArrayList<T>();
	private List<String> keyList = new ArrayList<String>();
	private boolean isScroll;

	public Pagination(){
	}
	
	public Pagination(int page, int rows){
		this.page = page;
		this.rows = rows;
	}
	
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getPage() {
		if (isScroll){
			return page;
		}
		if (totalRows == -1)
			return page;
		if (totalRows == 0)
			return 1;
		int maxPage = (int) (totalRows / rows);
		if (totalRows % rows > 0)
			maxPage += 1;
		if (page > maxPage)
			page = maxPage;
		if (page < 1)
			return 1;
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public long getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}
	public int getTotalPages() {
		int totalPages = (int) (totalRows / rows);
		if (totalRows % rows > 0)
			totalPages += 1;
		return totalPages;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}

	public List<String> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}

	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}

	@Override
	public String toString() {
		return "Pagination [rows=" + rows + ", page=" + page + ", totalRows=" + totalRows
				+ ", isScroll=" + isScroll + "]";
	}
}
