package x7.repository.pool;

import java.util.Map;

import javax.sql.DataSource;

public interface DataSourcePool {

	DataSource get();

	DataSource getR();

	Map<String, DataSource> getDsMapW();

	Map<String, DataSource> getDsMapR();
	
}
