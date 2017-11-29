package x7.repository.dao;

import java.util.Map;

import javax.sql.DataSource;


public class DaoInitializer {
	
	public static void init(DataSource dsW, DataSource dsR){
		AsyncDaoImpl.getInstance().setDataSource(dsW);
		AsyncDaoWrapper.getInstance().setDao(AsyncDaoImpl.getInstance());;
		
		DaoImpl.getInstance().setDataSource(dsW);
		DaoImpl.getInstance().setDataSource_R(dsR);
		DaoWrapper.getInstance().setDao(DaoImpl.getInstance());
	}
	
	public static void init(Map<String,DataSource> dsWMap,Map<String,DataSource> dsRMap){
		ShardingDaoImpl.getInstance().setDsWMap(dsWMap);
		ShardingDaoImpl.getInstance().setDsRMap(dsRMap);
	}
}
