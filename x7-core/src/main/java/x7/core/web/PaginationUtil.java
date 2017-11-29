package x7.core.web;

import java.util.HashMap;
import java.util.Map;

public class PaginationUtil {

	public static Map<String,Object> toMap(PaginationSorted po){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page", po.getPage());
		map.put("rows", po.getRows());
		map.put("totalPages", po.getTotalPages());
		map.put("totalRows", po.getTotalRows());
		map.put("orderBy", po.getOrderBy());
		map.put("sc", po.getSc());
		map.put("list", po.getList());
		map.put("tag", po.getTag());
		return map;
	}
	
	public static Map<String,Object> toMap(Pagination po){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page", po.getPage());
		map.put("rows", po.getRows());
		map.put("totalPages", po.getTotalPages());
		map.put("totalRows", po.getTotalRows());
		map.put("list", po.getList());
		return map;
	}
}
