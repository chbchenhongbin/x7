package xy.repository.mysql;

import javax.sql.DataSource;


public class Daos {
	
	public static void init(DataSource dsMaster, DataSource dsSlaver){
		AsyncDaoSQL.getInstance().setDataSource(dsMaster);
		AsyncDao.getInstance().setDao(AsyncDaoSQL.getInstance());;
		
		SyncDaoSQL.getInstance().setDataSource(dsMaster);
		SyncDaoSQL.getInstance().setDataSource_Slaver(dsSlaver);
		SyncDao.getInstance().setDao(SyncDaoSQL.getInstance());
	}
}
