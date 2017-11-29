package x7.repository;


import x7.core.config.Configs;
import x7.repository.dao.AsyncDaoImpl;
import x7.repository.dao.DaoInitializer;
import x7.repository.pool.HikariPool;


public class AsyncRepositoryBooter {
	
	private final static String MYSQL = "mysql";
	
	private final static String MongoDb = "mongodb";
	
	private final static String HBASE = "hbase";

	private static AsyncRepositoryBooter instance = null;
	
	
	public static void boot() {
		if (instance == null) {
			instance = new AsyncRepositoryBooter();
			init();
		}
	}
	
	private static void init(){
		
		String repository = Configs.getString(ConfigKey.REPOSITORY);
		repository = repository.toLowerCase();
		
		switch (repository){
		
		case MYSQL:
			HikariPool pool = HikariPool.getInstance();
			DaoInitializer.init(pool.get(), pool.getR());
			AsyncDaoImpl.getInstance().setDataSource(pool.get());
			break;
		
		}
		
	}
}
