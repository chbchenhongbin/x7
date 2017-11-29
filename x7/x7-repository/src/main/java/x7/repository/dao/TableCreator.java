package x7.repository.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.core.repository.SqlFieldType;
import x7.repository.mapper.MapperFactory;


public class TableCreator {

	public final static String DB_SQL = "CREATE DATABASE IF NOT EXISTS ${DB} CHARACTER SET utf8 COLLATE 'utf8_general_ci';";
	
	public static void create(Class clz){

		String sql = parse(clz);
		
		create(sql);
	}
	
	public static void create(String sql){


	}
	

	private static String parse(Class clz) {

		Parser.parse(clz);
		
		List<BeanElement> temp = MapperFactory.getElementList(clz);
		Map<String, BeanElement> map = new HashMap<String, BeanElement>();
		List<BeanElement> list = new ArrayList<BeanElement>();
		for (BeanElement be : temp) {

			
			if (be.sqlType != null && be.sqlType.equals("text")) {
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
				.append(MapperFactory.getTableName(clz))
				.append(Persistence.SUFFIX)
				.append(" (").append("\n");
		if (isSinglePk) {
			sb.append("   ").append(keyOne);
					
			BeanElement be = map.get(keyOne);
			String sqlType = be.getSqlType(); 
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
		} else {
			
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
					.append(be.sqlType);
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
//				sb.append(" DEFAULT NULL,").append("\n");
			}
		}

		for (BeanElement be : list) {
			sb.append("   ").append(be.property).append(" ")
					.append(be.sqlType).append(",").append("\n");
		}

		if (isSinglePk) {
			sb.append("   PRIMARY KEY (").append(keyOne).append(")");
		} else {
			sb.append("   PRIMARY KEY (").append(keyOne).append(",")
					.append(keyTwo).append(")");
		}
		
		sb.append("\n");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

		String sql = sb.toString();
		System.out.println(sql);
		return sql;
	}
	
	
}
