package zxt.oop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import zxt.oop.ro.CatRO;
import zxt.oop.xxx.CatTest;
import zxt.oop.xxx.CatTestRepository;

@RestController
@RequestMapping("/xxx")
public class XxxController {

	@Autowired
	private CatTestRepository repository;// sample

	@RequestMapping("/testFetch")
	public Map<String, Object> testFetch(@RequestBody CatRO ro) {
		
		{// sample, send the json by ajax from web page
			Map<String, Object> catMap = new HashMap<>();
			catMap.put("id", "");
			catMap.put("catFriendName", "");

			Map<String, Object> dogMap = new HashMap<>();
			dogMap.put("number", "");
			dogMap.put("userName", "");

			ro.getResultKeyMap().put("catTest", catMap);
			ro.getResultKeyMap().put("dogTest", dogMap);
		}

		CriteriaBuilder.Fetchable builder = CriteriaBuilder.buildFetchable(CatTest.class, ro);
		builder.and().like("catTest.catFriendName", ro.getCatFriendName());
		builder.orderBy(ro.getOrderBy()).sc(ro.getSc());

		String sourceScript = "catTest LEFT JOIN dogTest on catTest.dogId = dogTest.id";

		Criteria.Fetch fetch = builder.get();
		fetch.setSourceScript(sourceScript);

		Pagination<CatTest> pagination = new PaginationSorted<CatTest>(ro);

		pagination = repository.list(fetch, pagination);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "OK");
		map.put("result", pagination);

		return map;
	}
}
