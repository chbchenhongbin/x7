package x7.core.search;

import java.util.ArrayList;
import java.util.List;

public interface TagMaker {

	
	public static String makeTag(List<Long> tagIdList){
		if (tagIdList == null || tagIdList.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		int size = tagIdList.size();
		for (int i=0; i<size; i++){
			Long id = tagIdList.get(i);
			sb.append(id);
			if (i < size -1){
				sb.append("_");
			}
		}
		return sb.toString();
		
	}
	
	public static List<Long> listTagId(String str){
		List<Long> list = new ArrayList<Long>();
		if (str == null)
			return list;
		String[] arr = str.split("_");
		for (String ele : arr){
			list.add(Long.parseLong(ele));
		}
		return list;
	}
}
