package x7.repository;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import x7.core.config.Configs;

public class C3P0Pool {

	private ComboPooledDataSource dsMaster;
	private ComboPooledDataSource dsSlaver;
	
	private static C3P0Pool instance;
	public static C3P0Pool getInstance(){
		if (instance == null){
			instance = new C3P0Pool();
		}
		return instance;
	}
	
	private C3P0Pool(){
		init();
	}
	
	private  void init(){
		dsMaster = new ComboPooledDataSource();  
		try{
			String url = Configs.getString("DB_URL");
			
			url = url.replace("${DB_IP}", Configs.getString("DB_IP"))
					.replace("${DB_PORT}", Configs.getString("DB_PORT"))
					.replace("${DB_NAME}", Configs.getString("DB_NAME"));
			
			System.err.println("DB_URL_MASTER: " + url);
			
			dsMaster.setDriverClass(Configs.getString("DB_DRIVER"));   
	    	dsMaster.setJdbcUrl(url);    
	    	dsMaster.setUser(Configs.getString("DB_USER")); 
	    	dsMaster.setPassword(Configs.getString("DB_PASSWORD"));  
	    	dsMaster.setMaxStatements(6000);  
	    	dsMaster.setMaxStatementsPerConnection(300);
	    	dsMaster.setMaxPoolSize(Configs.getIntValue("DB_MAX"));   
	    	dsMaster.setMinPoolSize(Configs.getIntValue("DB_MIN"));
	    	dsMaster.setInitialPoolSize(Configs.getIntValue("DB_MIN"));
	    	dsMaster.setAcquireIncrement(5);
	    	dsMaster.setTestConnectionOnCheckin(true);
	    	dsMaster.setIdleConnectionTestPeriod(60);
	    	dsMaster.setCheckoutTimeout(3000);
	    	dsMaster.setBreakAfterAcquireFailure(false);
	    	dsMaster.setNumHelperThreads(5);
	    	dsMaster.setMaxIdleTime(300);//second
		}catch (Exception e){
			e.printStackTrace();
		}
		
		initSlaver();
	}
	
	private void initSlaver() {
		
		int slaverNumber = 0;
		try {
			slaverNumber = Configs.getIntValue("DB_SLAVER_NUM");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (slaverNumber == 0)
			return;
		
		System.out.println("\nDB_SLAVER_NUM=" + slaverNumber + "\n");
		
		dsSlaver = new ComboPooledDataSource();  
		try{
			String url = Configs.getString("DB_URL");
			
			url = url.replace("${DB_IP}", Configs.getString("DB_IP_SLAVER"))
					.replace("${DB_PORT}", Configs.getString("DB_PORT_SLAVER"))
					.replace("${DB_NAME}", Configs.getString("DB_NAME"));
			
			System.err.println("DB_URL_SLAVER: " + url);
			
			dsSlaver.setDriverClass(Configs.getString("DB_DRIVER"));   
			dsSlaver.setJdbcUrl(url);    
			dsSlaver.setUser(Configs.getString("DB_USER")); 
			dsSlaver.setPassword(Configs.getString("DB_PASSWORD"));  
			dsSlaver.setMaxStatements(6000);  
			dsSlaver.setMaxStatementsPerConnection(300);
			dsSlaver.setMaxPoolSize(Configs.getIntValue("DB_MAX") * slaverNumber);   
			dsSlaver.setMinPoolSize(Configs.getIntValue("DB_MIN") * slaverNumber);
			dsSlaver.setInitialPoolSize(Configs.getIntValue("DB_MIN") * slaverNumber);
			dsSlaver.setAcquireIncrement(5);
			dsSlaver.setTestConnectionOnCheckin(true);
			dsSlaver.setIdleConnectionTestPeriod(60);
			dsSlaver.setCheckoutTimeout(3000);
			dsSlaver.setBreakAfterAcquireFailure(false);
			dsSlaver.setNumHelperThreads(5);
			dsSlaver.setMaxIdleTime(300);//second
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public DataSource get(){
		return dsMaster;
	}
	
	public DataSource getSlaver(){
		return dsSlaver;
	}
}
