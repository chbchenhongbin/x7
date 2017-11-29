package x7.repository.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.repository.exception.PersistenceException;
import x7.repository.sharding.ShardingPolicy;

/**
 * 
x7.db.sharding.database=0,1 <br>
x7.db.driver=com.mysql.jdbc.Driver<br>
x7.db.url=jdbc:mysql://${address}/${name}?characterEncoding=utf8<br>
x7.db.name=dyt<br>
x7.db.user=root<br>
x7.db.password=dyt123456x!<br>
x7.db.max=200<br>
x7.db.min=40<br>
x7.db.address.w=127.0.0.1:4408,127.0.0.1:4409<br>
x7.db.read=0<br>
x7.db.address.r=127.0.0.1:3306<br>
 * @author Sim
 *
 */
public class HikariPool implements DataSourcePool{

	private HikariDataSource dsW;
	private HikariDataSource dsR;
	
	private Map<String,DataSource> dsWMap = new ConcurrentHashMap<String,DataSource>();
	private Map<String,DataSource> dsRMap = new ConcurrentHashMap<String,DataSource>();
 
	private static HikariPool instance;

	public static HikariPool getInstance() {
		if (instance == null) {
			instance = new HikariPool();
		}
		return instance;
	}

	private HikariPool() {
		initW();
		initR();
	}

	private void initW() {

		dsW = new HikariDataSource();
//		dsW.setRegisterMbeans(true);

		String shardingPolicy = Configs.getString("x7.db.sharding.policy");
		if (StringUtil.isNullOrEmpty(shardingPolicy))
			shardingPolicy = "NONE";
		
		if (shardingPolicy.equals("NONE")) {

			try {

				String url = Configs.getString("x7.db.url");
				url = url.replace("${address}", Configs.getString("x7.db.address.w"))
						.replace("${name}", Configs.getString("x7.db.name"));

				System.err.println("x7.db.url: " + url);
				dsW.setReadOnly(false);
				dsW.setJdbcUrl(url);
				dsW.setUsername(Configs.getString("x7.db.username"));
				dsW.setPassword(Configs.getString("x7.db.password"));
				dsW.setConnectionTimeout(300000);
				dsW.setIdleTimeout(600000);
				dsW.setMaxLifetime(1800000);
				dsW.setMaximumPoolSize(Configs.getIntValue("x7.db.max"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			int length = Configs.getIntValue("x7.db.sharding.num");
			String[] addressArr = Configs.getString("x7.db.address.w").split(",");
			if (addressArr.length == 1){
				String address = addressArr[0];
				addressArr = new String[length];
				for (int i = 0; i<length; i++) {
					addressArr[i] = address;
				}
			}else if (addressArr.length < length){
				throw new PersistenceException("SHARDING CONFIG UNEXPECTED, sharding w length = " + length + ", while address size not 1, or not " + length + ", but " + addressArr.length);
			}
			String[] shardingArr = ShardingPolicy.get(shardingPolicy).getSuffixArr();
			for (int i=0; i<length; i++){

				try {

					String url = Configs.getString("x7.db.url");
					url = url.replace("${address}", addressArr[i])
							.replace("${name}", Configs.getString("x7.db.name") + "_" + shardingArr[i]);

					System.err.println("x7.db.url: " + url);
					HikariDataSource dsW = new HikariDataSource();
					dsW.setReadOnly(false);
					dsW.setJdbcUrl(url);
					dsW.setUsername(Configs.getString("x7.db.user"));
					dsW.setPassword(Configs.getString("x7.db.password"));
					dsW.setConnectionTimeout(300000);
					dsW.setIdleTimeout(600000);
					dsW.setMaxLifetime(1800000);
					dsW.setMaximumPoolSize(Configs.getIntValue("x7.db.max"));
					dsWMap.put(shardingArr[i], dsW);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void initR() {

		int readNumber = 0;
		try {
			readNumber = Configs.getIntValue("x7.db.read");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (readNumber == 0)
			return;

		System.out.println("\nx7.db.read=" + readNumber + "\n");

		dsR = new HikariDataSource();
//		dsR.setRegisterMbeans(true);

		String shardingPolicy = Configs.getString("x7.db.sharding.policy");
		if (StringUtil.isNullOrEmpty(shardingPolicy))
			shardingPolicy = "NONE";
		if (shardingPolicy.equals("NONE")) {

			try {
				String url = Configs.getString("x7.db.url");
				url = url.replace("${address}", Configs.getString("x7.db.address.r"))
						.replace("${name}", Configs.getString("x7.db.name"));

				System.err.println("x7.db.url: " + url);
				dsR.setReadOnly(false);
				dsR.setJdbcUrl(url);
				dsR.setUsername(Configs.getString("x7.db.user"));
				dsR.setPassword(Configs.getString("x7.db.password"));
				dsR.setConnectionTimeout(300000);
				dsR.setIdleTimeout(600000);
				dsR.setMaxLifetime(1800000);
				dsR.setMaximumPoolSize(Configs.getIntValue("x7.db.max"));

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			int length = Configs.getIntValue("x7.db.sharding.num");
			String[] addressArr = Configs.getString("x7.db.address.r").split(",");
			if (addressArr.length == 1){
				String address = addressArr[0];
				addressArr = new String[length];
				for (int i = 0; i<length; i++) {
					addressArr[i] = address;
				}
			}else if (addressArr.length < length){
				throw new PersistenceException("SHARDING CONFIG UNEXPECTED, sharding r length = " + length + ", while address size not 1, or not " + length + ", but " + addressArr.length);
			}
			String[] shardingArr = ShardingPolicy.get(shardingPolicy).getSuffixArr();
			for (int i=0; i<length; i++){

				try {
					String url = Configs.getString("x7.db.url");
					url = url.replace("${address}", addressArr[i])
							.replace("${name}", Configs.getString("x7.db.name") + "_" + shardingArr[i]);

					System.err.println("x7.db.url: " + url);
					HikariDataSource dsR = new HikariDataSource();
//					dsW.setRegisterMbeans(true);
					dsR.setReadOnly(false);
					dsR.setJdbcUrl(url);
					dsR.setUsername(Configs.getString("x7.db.user"));
					dsR.setPassword(Configs.getString("x7.db.password"));
					dsR.setConnectionTimeout(300000);
					dsR.setIdleTimeout(600000);
					dsR.setMaxLifetime(1800000);
					dsR.setMaximumPoolSize(Configs.getIntValue("x7.db.max"));
					dsRMap.put(shardingArr[i], dsR);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	public DataSource get() {
		return dsW;
	}

	public DataSource getR() {
		return dsR;
	}


	public Map<String, DataSource> getDsMapW() {
		return dsWMap;
	}

	public Map<String, DataSource> getDsMapR() {
		return dsRMap;
	}
	
	protected DataSource get(String sharding) {
		return dsWMap.get(sharding);
	}

	protected DataSource getR(String sharding) {
		return dsRMap.get(sharding);
	}
}
