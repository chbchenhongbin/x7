package x7.dev.test;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.Criteria.Join;

public interface IDevService {

	List<Map<String,Object>> test(Criteria.Join criteriaJoinable);
	
	String getClassFullName(String simpleName);
}
