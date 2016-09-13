package x7.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import x7.config.ConfigBuilder;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.util.TimeUtil;
import x7.repository.bean.Dog;

public class Tester {

	public static void main(String[] args) {

		System.out.println("xxxxxxxxxxxxxxxxxxxx");

		ConfigBuilder.newInstance();

		RepositoryBooter.boot();

		Dog dog = new Dog();
		dog.setName("TEST");
		dog.setPetId(444);

		// baseRepository.create(dog);

		long start = TimeUtil.now();

		CriteriaBuilder.Joinable criteriaBuilder = CriteriaBuilder.buildJoinable(Dog.class, Criteria.Join.ALIAS_NAME);
		// criteria.x("[name.isNotNull.and.name.like.'*ST']");
		// criteria.and().lt("id", 3);
		double d = 4.0;
		criteriaBuilder
		.and().eq("id", 0.0)
		.and().like("name", "ST")
		.and().x().isNotNull("name").and().gte("name", new Date()).y()
		.and();



		criteriaBuilder.xAddKey("t->name.as.ne");

		long end = TimeUtil.now();

		System.out.println("--------- = " + (end - start));
		
		Criteria.Join criteria = (Criteria.Join) criteriaBuilder.get();
		List<Map<String, Object>> list = Repositories.getInstance().list(criteria);

		System.out.println("..." + list);
	}

}
