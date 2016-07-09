package x7.repository;

public class ManuRepository {

	public static <T> boolean execute(Object obj, String sql){
		
		return Repositories.getInstance().execute(obj, sql);
		
	}
}
