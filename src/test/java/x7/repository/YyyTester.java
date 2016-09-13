package x7.repository;

import x7.base.util.JsonX;
import x7.repository.bean.Dog;

public class YyyTester {

	public static void main(String[] args) {
		
		Dog dog = new Dog();
		dog.setId(1);
		dog.setPet(false);
		
		String str = JsonX.toJson(dog);
		
		str = "{\"id\":1,\"pet\":true,\"petId\":0}";
		
		System.out.println(str);
		
		Dog d = JsonX.toObject(str, Dog.class);
		
		System.out.println(d.isPet());
	}
}
