package x7.repository.dao;

import java.util.List;

/**
 * 
 * 
 * @author wyan
 * 
 */
public class AsyncDaoWrapper implements AsyncDao {
	private static AsyncDaoWrapper instance;
	public static AsyncDaoWrapper getInstance(){
		if (instance == null){
			instance = new AsyncDaoWrapper();
		}
		return instance;
	}
	private AsyncDao dao;
	protected void setDao(AsyncDao dao){
		this.dao = dao;
	}
	
	@Override
	public void create(Object obj) {
		dao.create(obj);
		
	}

	@Override
	public void refresh(Object obj) {
		dao.refresh(obj);
		
	}

	@Override
	public void remove(Object obj) {
		dao.refresh(obj);
		
	}

	@Override
	public <T> List<T> listSync(Class<T> clz) {
		return dao.listSync(clz);
	}


}
