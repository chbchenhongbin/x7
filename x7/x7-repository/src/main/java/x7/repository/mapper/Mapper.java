  package x7.repository.mapper;

public interface Mapper {

	String CREATE = "CREATE";
	String REFRESH = "REFRESH";
	String REMOVE = "REMOVE";
	String QUERY = "QUERY";
	String QUERY_TWO = "QUERY_TWO";
	String LOAD = "LOAD";
	String MAX_ID = "MAX_ID";
	String COUNT = "COUNT";
	String PAGINATION = "PAGINATION";
	String CREATE_TABLE = "CREATE_TABLE";
	String INDEX = "INDEX";
	
	interface Interpreter {
		String getTableSql(Class clz);
		String getRefreshSql(Class clz);
		String getQuerySql(Class clz);
		String getLoadSql(Class clz);
		String getMaxIdSql(Class clz);
		String getCreateSql(Class clz);
		String getPaginationSql(Class clz);
		String getCount(Class clz);
	}
}
