package x7.repository;

import x7.core.bean.CriteriaBuilder;
import x7.repository.bean.Cat;

public class XxxTester {

	public static void main(String[] args) {
		

		CriteriaBuilder.Joinable joinable = CriteriaBuilder.buildJoinable(Cat.class, "t");
		joinable.and().eq("name","pussyCat");
		joinable.xAddKey("t->name.as.n");
		
		CriteriaBuilder.parse(joinable.get());
	}
}
