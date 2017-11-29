package x7.repository.pool;

import org.springframework.stereotype.Component;

import x7.core.util.StringUtil;
import x7.repository.exception.PersistenceException;

@Component
public class DataSourceFactory {
	
	public static DataSourcePool get(String dataSourceType) {
		if (StringUtil.isNullOrEmpty(dataSourceType))
			return HikariPool.getInstance();
		
		if (dataSourceType.equals("hikari"))
			return HikariPool.getInstance();
		
		throw new PersistenceException("DataSource type: " + dataSourceType + ", is comming soon....");
	}
}
