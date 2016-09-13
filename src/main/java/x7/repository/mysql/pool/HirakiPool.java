package x7.repository.mysql.pool;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import x7.core.config.Configs;

public class HirakiPool {

	private HikariDataSource dsW;
	private HikariDataSource dsR;
	
	private static HirakiPool instance;
	
	public static HirakiPool getInstance(){
		if (instance == null){
			instance = new HirakiPool();
		}
		return instance;
	}
	
	private HirakiPool(){
		initW();
		initR();
	}

	private void initW() {
		
		dsW = new HikariDataSource();
		
		try{
			String url = Configs.getString("DB_URL");
			
			url = url.replace("${DB_IP}", Configs.getString("DB_IP"))
					.replace("${DB_PORT}", Configs.getString("DB_PORT"))
					.replace("${DB_NAME}", Configs.getString("DB_NAME"));
			
			System.err.println("DB_URL_W: " + url);
			dsW.setReadOnly(false);
			dsW.setJdbcUrl(url);
			dsW.setUsername(Configs.getString("DB_USER"));
			dsW.setPassword(Configs.getString("DB_PASSWORD"));
			dsW.setConnectionTimeout(30000);
			dsW.setIdleTimeout(600000);
			dsW.setMaxLifetime(1800000);
			dsW.setMaximumPoolSize(Configs.getIntValue("DB_MAX"));
			
		}catch (Exception e){
			e.printStackTrace();
		}
		

	}
	
	private void initR() {
		
		int slaverNumber = 0;
		try {
			slaverNumber = Configs.getIntValue("DB_SLAVER_NUM");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (slaverNumber == 0)
			return;
		
		System.out.println("\nDB_SLAVER_NUM=" + slaverNumber + "\n");
		
		dsR = new HikariDataSource();
		
		try{
			String url = Configs.getString("DB_URL");
			
			url = url.replace("${DB_IP}", Configs.getString("DB_IP_SLAVER"))
					.replace("${DB_PORT}", Configs.getString("DB_PORT_SLAVER"))
					.replace("${DB_NAME}", Configs.getString("DB_NAME"));
			
			System.err.println("DB_URL_R: " + url);
			dsR.setReadOnly(false);
			dsR.setJdbcUrl(url);
			dsR.setUsername(Configs.getString("DB_USER"));
			dsR.setPassword(Configs.getString("DB_PASSWORD"));
			dsR.setConnectionTimeout(30000);
			dsR.setIdleTimeout(600000);
			dsR.setMaxLifetime(1800000);
			dsR.setMaximumPoolSize(Configs.getIntValue("DB_MAX") * slaverNumber);
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public DataSource get(){
		return dsW;
	}
	
	public DataSource getR(){
		return dsR;
	}
}
