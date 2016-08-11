package x7.dev.test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import x7.core.bean.CriteriaJoinable;
import x7.core.config.Configs;
import x7.core.util.ClassFileReader;
import x7.repository.Repositories;



@Service
public class DevServiceImpl implements IDevService{
	
	private final Map<String,String> clzNameMap = new HashMap<String,String>();

	@Override
	public List<Map<String,Object>> test(CriteriaJoinable criteriaJoinable) {
		
		return Repositories.getInstance().list(criteriaJoinable);
	}

	@Override
	public String getClassFullName(String simpleName) {
		
		String testBeanPack = Configs.getString("TEST_BEAN_PACK");
		
		if (clzNameMap.isEmpty()) {
			Set<Class<?>> set = ClassFileReader.getClasses(testBeanPack);
			
			for (Class clz : set) {
				clzNameMap.put(clz.getSimpleName(), clz.getName());
			}
		}
		
		return clzNameMap.get(simpleName);
	}
	
	

}
