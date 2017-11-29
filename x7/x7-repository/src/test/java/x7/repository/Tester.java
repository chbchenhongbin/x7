package x7.repository;

import x7.config.ConfigBuilder;
import x7.repository.key.Key;

public class Tester {

	public static void test(String a, Object o){
		
	}
	
	public static void main(String[] args) {
		ConfigBuilder.build(false, "test", "E:/x7/framework/config", null);
		RepositoryBooter.boot();
		
		Dog dog = new Dog();
		dog.setName("xxxxx");
		
		Repositories.getInstance().create(dog);
	}
}
