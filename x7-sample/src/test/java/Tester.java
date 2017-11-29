

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x7.config.ConfigBuilder;
import x7.core.bean.Criteria;
import x7.core.bean.Criteria.Fetch;
import x7.core.bean.CriteriaBuilder;
import x7.core.web.PaginationSorted;
import x7.repository.Pig;
import x7.repository.Repositories;
import x7.repository.RepositoryBooter;
import zxt.oop.ro.CatRO;
import zxt.oop.xxx.CatTest;
import zxt.oop.xxx.DogTest;

public class Tester {

	public static void test(String a, Object o){
		
	}
	
	public static void main(String[] args) {
		ConfigBuilder.build(false, "test", "E:/x7/framework/config", null);
		RepositoryBooter.boot();
		
//		DogTest dog = new DogTest();
//		dog.setUserName("yyyyy");
//		dog.setNumber(new BigDecimal(100));
//		
//		Repositories.getInstance().create(dog);
//		
//		System.out.println("__________ " + dog);
		
		 Repositories.getInstance().list(CatTest.class);
		 Repositories.getInstance().list(DogTest.class);
		
		Map<String,Object> catMap = new HashMap<>();
		catMap.put("id", "");
		catMap.put("catFriendName", "");
		
		Map<String,Object> dogMap = new HashMap<>();
		dogMap.put("number", "");
		dogMap.put("userName", "");
		
		CatRO ro = new CatRO();
		
		ro.getResultKeyMap().put("catTest", catMap);
		ro.getResultKeyMap().put("dogTest", dogMap);
		
		List<Object> idList = new ArrayList<>();
		idList.add(1L);
		idList.add(2L);
		CriteriaBuilder.Fetchable builder = CriteriaBuilder.buildFetchable(CatTest.class, ro);
		builder.and().in("catTest.id", idList);
		
		Fetch fetch = builder.get();
		
		String sourceScript = "catTest LEFT JOIN dogTest ON catTest.dogId = dogTest.id";
		fetch.setSourceScript(sourceScript);
		
		PaginationSorted<Map<String,Object>> pagination = new PaginationSorted<>();
		pagination.setPage(1);
		pagination.setRows(10);
		pagination.setOrderBy("catTest.id");
		pagination.setSc("DESC");
		
		pagination = (PaginationSorted<Map<String, Object>>) Repositories.getInstance().list(fetch, pagination);
		
		CriteriaBuilder criteriaBuilder = CriteriaBuilder.build(DogTest.class);
		criteriaBuilder.and().eq("number", 10);
		Criteria criteria = criteriaBuilder.get();
		
		Object obj = Repositories.getInstance().getSum("petId", criteria);
		
		System.out.println(pagination.getList());
		
		System.err.println("count = " + obj);
		
		DogTest d = new DogTest();
		d.setId(1);
		d.setUserName("XXTYU");
		d.setNumber(new BigDecimal(10000.45));
		
		Repositories.getInstance().remove(d);
		
		Pig pig = new Pig();
		pig.setNickName("bIGpig");
		pig.setPigWeight("100今");
		Repositories.getInstance().create(pig);
	}
}
