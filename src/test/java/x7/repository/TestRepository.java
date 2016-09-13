package x7.repository;


import java.util.List;
import x7.repository.bean.Dog;

public class TestRepository<Dog> extends BaseRepository<Dog> {
	

	@SuppressWarnings("unchecked")
	public List<Dog> listDogByNameOrTitle(String name, String title){
		
		
		return (List<Dog>) preMapping("listDogByNameOrTitle", name, title);
	};
}
