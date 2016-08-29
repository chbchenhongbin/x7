package x7.dev.test;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import x7.base.servlet.ServletModelCreator;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.Criteria.Join;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.core.web.ModelView;




@Controller
@RequestMapping("/dev")
public class DevController {
	
	@Autowired
	private IDevService service;

	@RequestMapping("test")
	public Map<String, Object> test(HttpServletRequest req) {
		
		boolean isDev = Configs.isTrue("IS_DEVELOPING");
		
		if (!isDev) {
			return ModelView.toast("NOT DEV OR NOT TEST");
		}
		
		Map<String,String> map = ServletModelCreator.createMap(req);
		
		String simpleName = map.get(CriteriaBuilder.CLASS_NAME);
		if (StringUtil.isNullOrEmpty(simpleName)){
			return ModelView.toast("lose express: class.name=");
		}
		
		String fullName = this.service.getClassFullName(simpleName);
		if (! StringUtil.isNullOrEmpty(fullName)){
			map.put(CriteriaBuilder.CLASS_NAME, fullName);
		}
		
		CriteriaBuilder.Joinable criteriaBuilder = CriteriaBuilder.buildJoinable(isDev, map);
		
		Criteria.Join criteriaJoinable = (Criteria.Join) criteriaBuilder.get();

		List<Map<String,Object>> list = this.service.test(criteriaJoinable);

		return ModelView.view(list);
	}
}
