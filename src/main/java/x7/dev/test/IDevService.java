package x7.dev.test;

import java.util.List;
import java.util.Map;

import x7.core.bean.CriteriaJoinable;

public interface IDevService {

	List<Map<String,Object>> test(CriteriaJoinable criteriaJoinable);
	
	String getClassFullName(String simpleName);
}
