package x7.repository;

import x7.config.ConfigBuilder;

public class Tester {
	
	public static void main(String[] args) {
		
		System.out.println("xxxxxxxxxxxxxxxxxxxx");
		
		ConfigBuilder.newInstance();
		
		RepositoryBooter.boot();
		
		Dog dog = new Dog();
		dog.setName("TEST");
		dog.setPetId(444);
		
		BaseRepository baseRepository = new BaseRepository();
		
		baseRepository.create(dog);

		
	}
	
}
