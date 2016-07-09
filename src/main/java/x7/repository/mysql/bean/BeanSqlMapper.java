package x7.repository.mysql.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.core.repository.SqlFieldType;


public class BeanSqlMapper implements SqlKey{

	private static Map<Class, Map<String, String>> sqlsMap = new HashMap<Class, Map<String, String>>();
	
	
	/**
	 * 返回SQL
	 * 
	 * @param clz
	 *            ? extends IAutoMapped
	 * @param type
	 *            (BeanMapper.CREATE|BeanMapper.REFRESH|BeanMapper.DROP|
	 *            BeanMapper.QUERY)
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getSql(Class clz, String type) {

		Map<String, String> sqlMap = sqlsMap.get(clz);
		if (sqlMap == null) {
			sqlMap = new HashMap<String, String>();
			sqlsMap.put(clz, sqlMap);
			parseBean(clz);
		}

		return sqlMap.get(type);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String tryToCreate(Class clz) {

		Map<String, String> sqlMap = sqlsMap.get(clz);
		if (sqlMap == null) {
			sqlMap = new HashMap<String, String>();
			sqlsMap.put(clz, sqlMap);
			parseBean(clz);
			return sqlMap.remove(CREATE_TABLE);
		}

		return "";

	}

	/**
	 * 
	 * @param clz
	 * @return
	 */
	public static List<BeanElement> getElementList(Class clz) {
		return Parser.get(clz).getBeanElementList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void parseBean(Class clz) {

		Parser.parse(clz);

		getTableSql(clz);
		getRefreshSql(clz);
		getRemoveSql(clz);
		getQuerySql(clz);
		getLoadSql(clz);
		getMaxIdSql(clz);
		getCreateSql(clz);
		getPaginationSql(clz);
		getCount(clz);
	}
	


	private static String getRefreshSql(Class clz) {

		List<BeanElement> list = Parser.get(clz).getBeanElementList();

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ");
		sb.append(getTableName(clz)).append(space);
		sb.append("SET ");

		Parsed parsed = Parser.get(clz);
		boolean flag = parsed.isCombinedKey();
		String keyOne = parsed.getKey(Persistence.KEY_ONE);

		List<BeanElement> tempList = new ArrayList<BeanElement>();
		for (BeanElement p : list) {
			String column = p.property;
			if (column.equals(keyOne))
				continue;

			if (flag) {
				if (column.equals(parsed.getKey(Persistence.KEY_TWO)))
					continue;
			}
			tempList.add(p);
		}

		int size = tempList.size();
		for (int i = 0; i < size; i++) {
			String column = tempList.get(i).property;

			sb.append(column).append(" = ?");
			if (i < size - 1) {
				sb.append(", ");
			}
		}

		sb.append(" WHERE ");

		parseKey(sb, clz);

		String sql = sb.toString();

		sqlsMap.get(clz).put(REFRESH, sql);

		System.out.println(sql);

		return sql;

	}

	private static String getRemoveSql(Class clz) {

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ");
		sb.append(getTableName(clz)).append(space);
		sb.append("WHERE ");

		parseKey(sb, clz);

		String sql = sb.toString();

		sqlsMap.get(clz).put(REMOVE, sql);

		System.out.println(sql);

		return sql;

	}

	private static void parseKey(StringBuilder sb, Class clz) {
		Parsed parsed = Parser.get(clz);

		sb.append(parsed.getKey(Persistence.KEY_ONE));
		sb.append(" = ?");

		if (parsed.isCombinedKey()) {
			sb.append(" AND ");
			sb.append(parsed.getKey(Persistence.KEY_TWO));
			sb.append(" = ?");
		}
	}

	private static String getQuerySql(Class clz) {

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(getTableName(clz)).append(space);
		sb.append("WHERE ");

//		parseKey(sb, clz);
		
		Parsed parsed = Parser.get(clz);

		sb.append(parsed.getKey(Persistence.KEY_ONE));
		sb.append(" = ?");
		
		String sql = sb.toString();

		sqlsMap.get(clz).put(QUERY, sql);

		System.out.println(sql);

		if (parsed.isCombinedKey()) {
			sb.append(" AND ");
			sb.append(parsed.getKey(Persistence.KEY_TWO));
			sb.append(" = ?");
			
			sql = sb.toString();
			sqlsMap.get(clz).put(QUERY_TWO, sql);
		}



		return sql;

	}

	private static String getLoadSql(Class clz) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(getTableName(clz));

		String sql = sb.toString();

		sqlsMap.get(clz).put(LOAD, sql);

		System.out.println(sql);

		return sql;

	}

	private static String getMaxIdSql(Class clz) {


		Parsed parsed = Parser.get(clz);

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT MAX(");
		if (parsed.isCombinedKey()) {
			sb.append(parsed.getKey(Persistence.KEY_TWO));
		}else{
			sb.append(parsed.getKey(Persistence.KEY_ONE));
		}
		sb.append(") maxId FROM ");
		sb.append(getTableName(clz));

		if (parsed.isCombinedKey()) {
			sb.append(space);
			sb.append("WHERE ");

			sb.append(parsed.getKey(Persistence.KEY_ONE));
			sb.append(" = ?");
		}

		String sql = sb.toString();

		sqlsMap.get(clz).put(MAX_ID, sql);

		System.out.println(sql);

		return sql;


	}

	private static String getCreateSql(Class clz) {

		List<BeanElement> list = Parser.get(clz).getBeanElementList();

		Parsed parsed = Parser.get(clz);
		boolean flag = parsed.isCombinedKey();

		String key = parsed.getKey(Persistence.KEY_ONE);

		List<BeanElement> tempList = new ArrayList<BeanElement>();
		for (BeanElement p : list) {

			if (!flag){
				
				if (!parsed.isNotAutoIncreament() && p.property.equals(key))
					continue;
			}

			tempList.add(p);
		}

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(getTableName(clz)).append(space);

		sb.append("(");
		int size = tempList.size();
		for (int i = 0; i < size; i++) {
			String p = tempList.get(i).property;

			sb.append(p);
			if (i < size - 1) {
				sb.append(",");
			}
		}
		sb.append(") VALUES (");

		for (int i = 0; i < size; i++) {

			sb.append("?");
			if (i < size - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		String sql = sb.toString();

		sqlsMap.get(clz).put(CREATE, sql);

		System.out.println(sql);

		return sql;

	}

	private static String getTableSql(Class clz) {

		List<BeanElement> temp = Parser.get(clz).getBeanElementList();
		Map<String, BeanElement> map = new HashMap<String, BeanElement>();
		List<BeanElement> list = new ArrayList<BeanElement>();
		for (BeanElement be : temp) {

			
			if (be.sqlField != null && be.sqlField.equals("text")) {
				list.add(be);
				continue;
			}
			map.put(be.property, be);
		}

		Parsed parsed = Parser.get(clz);

		boolean isSinglePk = !parsed.isCombinedKey();
		String keyOne = parsed.getKey(Persistence.KEY_ONE);
		String keyTwo = null;

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS ")
				.append(getTableName(clz))
				.append(" (").append("\n");
		if (isSinglePk) {
			sb.append("   ").append(keyOne);
					
			
			BeanElement be = map.get(keyOne);
			String sqlType = be.getSqlType(); 
			System.out.println("p = " + be.property + " sqlType = " + sqlType);
			if (sqlType.equals("int")){
				sb.append(" int(11) NOT NULL");
			}else if (sqlType.equals("bigint")){
				sb.append(" bigint(13) NOT NULL");
			}else if (sqlType.equals("varchar")){
				sb.append(" varchar(").append(be.length).append(") NOT NULL");
			}
			
			if (!parsed.isNotAutoIncreament()){
					sb.append("	AUTO_INCREMENT, ");
			}else{
				sb.append(", ");
			}
					
					sb.append("\n");
			map.remove(keyOne);
		} else {// boolean, TINYINT(1) DEFAULT 0  NULL;
			
			BeanElement be = map.get(keyOne);
			String sqlType = be.getSqlType(); 
			System.out.println("p = " + be.property + " sqlType = " + sqlType);
			sb.append("   ").append(keyOne);
			if (sqlType.equals("int")){
				sb.append(" int(11) NOT NULL,");
			}else if (sqlType.equals("bigint")){
				sb.append(" bigint(13) NOT NULL,");
			}else if (sqlType.equals("varchar")){
				sb.append(" varchar(").append(be.length).append(") NOT NULL,");
			}
			sb.append("\n");
			
			keyTwo = parsed.getKey(Persistence.KEY_TWO);
			BeanElement beTwo = map.get(keyTwo);
			sqlType = beTwo.getSqlType(); 
			sb.append("   ").append(keyTwo);
			if (sqlType.equals("int")){
				sb.append(" int(11) NOT NULL,");
			}else if (sqlType.equals("bigint")){
				sb.append(" bigint(13) NOT NULL,");
			}
			sb.append("\n");
			map.remove(keyOne);
			map.remove(keyTwo);
		}

		for (BeanElement be : map.values()) {
			// `name` varchar(60) DEFAULT NULL,
			sb.append("   ").append(be.property).append(" ")
					.append(be.sqlField);
			if (be.length > 0) {
				
				if (be.getSqlType().equals("float")){
					sb.append("(13,2) NULL ");
				}else if (be.getSqlType().equals("double")){
					sb.append("(13,2) NULL");
				}else{
				
					sb.append("(").append(be.length).append(")");
				}
			}
			
			
			if (be.getSqlType().equals(SqlFieldType.DATE)){
				if (be.property.equals("createTime")){
					sb.append(" NULL DEFAULT CURRENT_TIMESTAMP,").append("\n");
				}else if (be.property.equals("refreshTime")){
					sb.append(" NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,").append("\n");
				}else{
					sb.append(" NULL,").append("\n");
				}
			}else{
				if (be.clz == Boolean.class 
						|| be.clz == boolean.class
						|| be.clz == Integer.class
						|| be.clz == int.class
						|| be.clz == Long.class
						|| be.clz == long.class
						|| be.clz == Double.class
						|| be.clz == double.class
						|| be.clz == Float.class
						|| be.clz == float.class
						){
					sb.append(" DEFAULT 0,").append("\n");
				}else{
					sb.append(" DEFAULT NULL,").append("\n");
				}
			}
		}

		for (BeanElement be : list) {
			sb.append("   ").append(be.property).append(" ")
					.append(be.sqlField).append(",").append("\n");
		}

		if (isSinglePk) {
			sb.append("   PRIMARY KEY (").append(keyOne).append(")");
		} else {
			sb.append("   PRIMARY KEY (").append(keyOne).append(",")
					.append(keyTwo).append(")");
		}
		
		for (BeanElement be : map.values()) {
			if (be.property.equals("refreshTime") && be.getSqlType().equals(SqlFieldType.DATE)){
				
				sb.append(",")
					.append("\n")
					.append("   INDEX ")
					.append("idx_")
					.append(getTableName(clz))
					.append("_rt")
					.append(" (refreshTime)");
			}
		}
		sb.append("\n");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		

		String sql = sb.toString();
		System.out.println(sql);

		sqlsMap.get(clz).put(CREATE_TABLE, sql);
		

		
		return sql;
	}
	
	private static String getPaginationSql(Class clz) {

		String space = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT "+Persistence.PAGINATION + " FROM ");
		sb.append(getTableName(clz)).append(space);
		sb.append("WHERE 1=1 ");
		
		String sql = sb.toString();

		sqlsMap.get(clz).put(PAGINATION, sql);

		System.out.println(sql);

		return sql;

	}
	
	private static String getCount(Class clz) {

		Parsed parsed = Parser.get(clz);
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(");
		if (parsed.isCombinedKey()) {
			sb.append(parsed.getKey(Persistence.KEY_TWO));
		}else{
			sb.append(parsed.getKey(Persistence.KEY_ONE));
		}
		sb.append(") count FROM ");
		sb.append(getTableName(clz));

		String sql = sb.toString();

		sqlsMap.get(clz).put(COUNT, sql);

		System.out.println(sql);

		return sql;

	}
	
	public static String getTableName(Class clz){

		Parsed parsed = Parser.get(clz);
		return parsed.getTableName();
	}
	
	public static String getTableName(Parsed parsed){
		
		return parsed.getTableName();
	}
}
