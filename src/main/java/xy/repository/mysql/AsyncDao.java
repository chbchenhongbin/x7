package xy.repository.mysql;

/**
 * 
 * 
 * @author wyan
 * 
 */
public class AsyncDao implements IAsyncDao {
	private static AsyncDao instance;
	public static AsyncDao getInstance(){
		if (instance == null){
			instance = new AsyncDao();
		}
		return instance;
	}
	private IAsyncDao dao;
	protected void setDao(IAsyncDao dao){
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


}
