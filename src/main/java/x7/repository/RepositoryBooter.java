package x7.repository;


import x7.core.config.Configs;
import x7.repository.mysql.Daos;
import x7.repository.mysql.SyncDao;
import x7.repository.mysql.pool.HirakiPool;
import x7.repository.redis.CacheResolver;


public class RepositoryBooter {
	
	private final static String MYSQL = "mysql";
	
	private final static String MongoDb = "mongodb";
	
	private final static String HBASE = "hbase";

	private static RepositoryBooter instance = null;
	
	
	public static void boot() {
		if (instance == null) {
			instance = new RepositoryBooter();
			init();
		}
	}
	
	private static void init(){
		
		String repository = Configs.getString("REPOSITORY");
		repository = repository.toLowerCase();
		
		switch (repository){
		
		case MYSQL:
			HirakiPool pool = HirakiPool.getInstance();
			Daos.init(pool.get(), pool.getR());
			Repositories.getInstance().setSyncDao(SyncDao.getInstance());
			break;
		
		}
		
		if (Configs.isTrue("IS_CACHEABLE")){
			Repositories.getInstance().setCacheResolver(CacheResolver.getInstance());
		}
	}
}
