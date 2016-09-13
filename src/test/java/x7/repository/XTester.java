package x7.repository;

import java.util.ArrayList;
import java.util.List;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.repository.bean.Cat;
import x7.repository.bean.Dog;

public class XTester {

	public static void main(String[] args) {
		
		List<Object> idList = new ArrayList<Object>();
		
		CriteriaBuilder criteriaBuilder = CriteriaBuilder.build(Dog.class);
		criteriaBuilder.and().in("id", idList).and().x().eq("type", "SPONSOR").or().eq("type", "OPERATION").y();
		Criteria criteria = criteriaBuilder.get();
		
		Criteria c1 = criteriaBuilder.get();
		
		System.out.println(c1);
		
		CriteriaBuilder buider2 = CriteriaBuilder.build(Cat.class);
		buider2.and().eq("name", "yyy");
		Criteria c2 = buider2.get();
		
		c1 = criteriaBuilder.get();
		
		System.out.println(c1);
	}
}
