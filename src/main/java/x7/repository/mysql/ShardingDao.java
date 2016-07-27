package x7.repository.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.ShardingException;
import x7.repository.mysql.bean.BeanSqlMapper;
import x7.repository.mysql.bean.BeanSqlMapper_Sharding;
import x7.repository.mysql.bean.SqlKey;
import x7.repository.mysql.sharding.ShardingConfig;
import x7.repository.mysql.sharding.ShardingDBPool;
import x7.repository.mysql.sharding.ShardingPolicy;




public class ShardingDao implements IShardingDao {


	private static ShardingDao instance;

	public static ShardingDao getInstance() {
		if (instance == null) {
			instance = new ShardingDao();
		}
		return instance;
	}

	private ShardingDao() {
	}

	private ShardingDBPool pool;

	public void setDataSource(ShardingDBPool pool) {
		this.pool = pool;
	}

	private Connection getConnection(String key) throws SQLException {
		if (pool == null) {
			throw new RuntimeException("NO DB POOL");
		}
		DataSource ds = pool.get(key);
		return ds.getConnection();
	}

	/**
	 * 放回连接池,<br>
	 * 连接池已经重写了关闭连接的方法
	 */
	private static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private final String FALL_LINE = "_";

	private String getSql(String sql, String keyTable) {
		sql = sql.replace(Persistence.SUFFIX, FALL_LINE + keyTable);
		return sql;
	}

	private long create(Object obj, String[] keyArr) {

		@SuppressWarnings("rawtypes")
		Class clz = obj.getClass();

		tryToInitSql(clz);

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.CREATE);
		sql = getSql(sql, keyArr[1]);

		List<BeanElement> eles = BeanSqlMapper_Sharding.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;

		long id = -1;

		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			/*
			 * 返回自增键
			 */
			Parsed parsed = Parser.get(clz);
			boolean isCombinedKey = parsed.isCombinedKey();
			String keyOne = parsed.getKey(Persistence.KEY_ONE);
			
			Long keyTwoValue = null;
			if (isCombinedKey){
				keyTwoValue = parsed.getKeyField(Persistence.KEY_TWO).getLong(obj);
				if (keyTwoValue == null || keyTwoValue == 0){
					keyTwoValue = this.getMaxId(clz, parsed.getKeyField(Persistence.KEY_ONE).getLong(obj)) + 1;//getMaxId_Master
					
					parsed.getKeyField(Persistence.KEY_TWO).set(obj, keyTwoValue.intValue());
				}
			}

			System.out.println("create sql: " + sql);
			int i = 1;
			for (BeanElement ele : eles) {

				if (!isCombinedKey) {
					if (!parsed.isNotAutoIncreament() && ele.property.equals(keyOne)) {
						continue;
					}
				}
				Method method = ele.getMethod;
//				try {
//					method = obj.getClass().getDeclaredMethod(ele.getter);
//				} catch (NoSuchMethodException e) {
//					method = obj.getClass().getSuperclass().getDeclaredMethod(ele.getter);
//				}
				Object value = method.invoke(obj);
				System.out.println("getter = " + ele.getter + " | value = " + value);
				pstmt.setObject(i++, value);

			}

			pstmt.execute();

			if (parsed.isCombinedKey()) { // 有主键自增
//				Method method = null;
//				try {
//					method = obj.getClass().getDeclaredMethod(BeanUtilX.getGetter(parsed.getKey(Persistence.KEY_TWO)));
//				} catch (NoSuchMethodException e) {
//					method = obj.getClass().getSuperclass().getDeclaredMethod(BeanUtilX.getGetter(parsed.getKey(Persistence.KEY_TWO)));
//				}
				id = keyTwoValue;
			} 
			conn.commit();

		}catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				e.printStackTrace();
				throw new PersistenceException("Persistence Exception: " + e.getStackTrace());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return id;
	}

	@Override
	public long create(Object obj) {

		tryToInitSql(obj.getClass());
		String[] keyArr = getKeys(obj);
		return create(obj, keyArr);
	}

	private void refresh(Object obj, String[] keyArr) {

		Class clz = obj.getClass();

		Parsed parsed = Parser.get(clz);

		Map<String,Object> queryMap = BeanUtilX.getRefreshMap(parsed, obj);
		
		String tableName = BeanSqlMapper_Sharding.getTableName(clz);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ")
			.append(tableName).append(" ");
		String sql = SqlUtil.concatRefresh(sb, parsed, queryMap);
		sql = getSql(sql, keyArr[1]);
		System.out.println("refreshOptionally: " + sql);
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);


			int i = 1;
			for (Object value : queryMap.values()) {

				pstmt.setObject(i++, value);
			}

			/*
			 * 处理KEY
			 */
			SqlUtil.adpterSqlKey(pstmt, parsed.getKeyField(Persistence.KEY_ONE), parsed.getKeyField(Persistence.KEY_TWO), obj, i);

			pstmt.execute();
			conn.commit();
		}catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				e.printStackTrace();
				throw new PersistenceException("Persistence Exception: " + e.getStackTrace());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

	}

	@Override
	public void refresh(Object obj) {
		tryToInitSql(obj.getClass());
		String[] keyArr = getKeys(obj);
		refresh(obj, keyArr);
	}
	
	private boolean refresh(Object obj, Map<String, Object> conditionMap, String[] keyArr) {


		Class clz = obj.getClass();

		Parsed parsed = Parser.get(clz);

		Map<String,Object> queryMap = BeanUtilX.getRefreshMap(parsed, obj);
		
		String tableName = BeanSqlMapper_Sharding.getTableName(clz);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ")
			.append(tableName).append(" ");
		String sql = SqlUtil.concatRefresh(sb, parsed, queryMap, conditionMap);
		sql = getSql(sql, keyArr[1]);
		System.out.println("refreshOptionally: " + sql);
		
		boolean flag = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);


			int i = 1;
			for (Object value : queryMap.values()) {

				pstmt.setObject(i++, value);
			}

			/*
			 * 处理KEY
			 */
			SqlUtil.adpterRefreshCondition(pstmt, parsed.getKeyField(Persistence.KEY_ONE), parsed.getKeyField(Persistence.KEY_TWO), obj, i, conditionMap);

			flag = pstmt.executeUpdate() == 0 ? false : true;
			conn.commit();
		}catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			flag = false;
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return flag;
	}
	
	@Override
	public boolean refresh(Object obj, Map<String,Object> conditionMap){
		tryToInitSql(obj.getClass());
		String[] keyArr = getKeys(obj);
		return refresh(obj, conditionMap, keyArr);
	}

	private void remove(Object obj, String[] keyArr) {

		Class clz = obj.getClass();

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.REMOVE);
		sql = getSql(sql, keyArr[1]);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			Parsed parsed = Parser.get(clz);

			int i = 1;

			SqlUtil.adpterSqlKey(pstmt, parsed.getKeyField(Persistence.KEY_ONE), parsed.getKeyField(Persistence.KEY_TWO), obj, i);

			pstmt.execute();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				e.printStackTrace();
				throw new PersistenceException("Persistence Exception: " + e.getStackTrace());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}
	}

	@Override
	public void remove(Object obj) {
		tryToInitSql(obj.getClass());
		String[] keyArr = getKeys(obj);
		remove(obj, keyArr);
	}

	private <T> List<T> list(Class<T> clz, long id, String[] keyArr) {

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.QUERY);
		sql = getSql(sql, keyArr[1]);

		List<BeanElement> eles = BeanSqlMapper_Sharding.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;

			pstmt.setObject(i++, id);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return list;
	}

	@Override
	public <T> List<T> list(Class<T> clz, long id) {
		tryToInitSql(clz);
		String[] keyArr = getKeys(id);
		return list(clz, id, keyArr);
	}

	private <T> T get(Class<T> clz, long idOne, long idTwo, String[] keyArr) {

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.QUERY_TWO);
		sql = getSql(sql, keyArr[1]);

		List<BeanElement> eles = BeanSqlMapper_Sharding.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;

			pstmt.setObject(i++, idOne);
			pstmt.setObject(i++, idTwo);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	@Override
	public <T> T get(Class<T> clz, long idOne, long idTwo) {

		tryToInitSql(clz);
		String[] keyArr = getKeys(idOne);
		return get(clz, idOne, idTwo, keyArr);
	}
	
	private <T> T get(Class<T> clz, long idOne,  String[] keyArr) {

		Parsed parsed = Parser.get(clz);
		if (parsed.isCombinedKey()){
			throw new PersistenceException("CombinedKey object should get(clz, idOne, idTwo)");
		}
		
		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.QUERY_TWO);
		sql = getSql(sql, keyArr[1]);

		List<BeanElement> eles = BeanSqlMapper_Sharding.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;

			pstmt.setObject(i++, idOne);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		if (list.isEmpty())
			return null;
		return list.get(0);
	}
	@Override
	public <T> T get(Class<T> clz, long idOne) {
		tryToInitSql(clz);
		String[] keyArr = getKeys(idOne);
		return get(clz, idOne, keyArr);
	}


	private <T> List<T> list(Class<T> clz, Object conditionObj, String[] keyArr){
	
		Parsed parsed = Parser.get(clz);

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.LOAD);
		sql = getSql(sql, keyArr[1]);
		
		sql = sql.concat(" WHERE 1=1");
		
		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);
		
		System.out.println("ShardingDao.list(obj)...SQL:  " + sql);
		
		List<T> list = new ArrayList<T>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : parsed.getBeanElementList()) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return list;
	}
	
	@Override
	public <T> List<T> list(Object conditionObj) {
		Class clz = conditionObj.getClass();
		tryToInitSql(clz);
		String[] keyArr = getKeys(conditionObj);
		return list(clz, conditionObj, keyArr);
	}
	
	private int getCount(String sql, long idOne, String[] keyArr){
		
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			pstmt.setObject(i++, idOne);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Object obj = rs.getObject("count");
				if (obj != null) {
					count = ((Long) obj).intValue();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return count;
	}
	

	@Override
	public <T> T getOne(T conditionObj, String orderBy, String sc) {
		Class clz = conditionObj.getClass();
		tryToInitSql(clz);
		String[] keyArr = getKeys(conditionObj);
		return getOne(clz, conditionObj, orderBy,sc, keyArr);
	}
	
	private <T> T  getOne(Class clz, T conditionObj, String orderBy, String sc, String[] keyArr){
		Parsed parsed = Parser.get(clz);
		

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.LOAD);
		sql = getSql(sql, keyArr[1]);
		
		sql = sql.concat(" WHERE 1=1");
		
		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);
		
		sql = sql + " order by " + orderBy + " " +  sc;
		sql = sql + " limit 1";
		
		System.out.println("ShardingDao.list(obj)...SQL:  " + sql);
		
		List<Object> list = new ArrayList<Object>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Object obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : parsed.getBeanElementList()) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}
		
		if (list.isEmpty())
			return null;

		return (T) list.get(0);		
	}
	
	private <T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination, String[] keyArr) {

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.PAGINATION);
		sql = getSql(sql, keyArr[1]);
		
		int count = getCount(sql.replace(Persistence.PAGINATION, "COUNT(*) count"),idOne,keyArr);
		
		pagination.setTotalRows(count);
		int page = pagination.getPage();
		int rows = pagination.getRows();
		int start = (page - 1) * rows;
		
		sql = sql.replace(Persistence.PAGINATION, " * ");
		
		if (pagination instanceof PaginationSorted){
			PaginationSorted ps = (PaginationSorted) pagination;
			sql = sql + " order by  " + ps.getOrderBy() + " "+ ps.getSc();
		}
		
		sql = sql + " LIMIT " + start + "," +  rows;

		List<BeanElement> eles = BeanSqlMapper_Sharding.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;

			pstmt.setObject(i++, idOne);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					pagination.getList().add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
//						try {
//							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
//						} catch (NoSuchMethodException e) {
//							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
//						}
						if (ele.clz.getSimpleName().toLowerCase().equals("double")){
							Object v = rs.getObject(ele.property);
							if (v != null){
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						}else{
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return pagination;
	}

	@Override
	public <T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination) {
		tryToInitSql(clz);
		String[] keyArr = getKeys(idOne);
		return list(clz, idOne, pagination, keyArr);
	}

	private <T> long getMaxId(Class<T> clz, long key, String[] keyArr) {

		long id = 0;

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.MAX_ID);
		sql = getSql(sql, keyArr[1]);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			pstmt.setObject(i++, key);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				id = rs.getLong("maxId");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return id;

	}

	@Override
	public <T> long getMaxId(Class<T> clz, long key) {
		tryToInitSql(clz);
		String[] keyArr = getKeys(key);
		return getMaxId(clz, key, keyArr);
	}

	private String[] getKeys(Object obj) {
		Parsed parsed = Parser.get(obj.getClass());

		String value = "";
		Field field = null;
		try {
			field = parsed.getKeyField(Persistence.KEY_SHARDING);
			field.setAccessible(true);
			Object o = field.get(obj);
			value = String.valueOf(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String idOne = parsed.getKey(Persistence.KEY_ONE);
		String keySharding = parsed.getKey(Persistence.KEY_SHARDING);
		if (idOne.equals(keySharding)){
			if (value.equals("0"))
				throw new ShardingException("\n SHARDING NO VALUE, IF SHARDING = IDONE, SHARDING CAN BE NOT 0, \n obj = " + obj +"\n");
		}

		if (value.equals("")) {
			throw new ShardingException("SHARDING VALUE IS NULL, ojb = " + obj);
		}

		String policy = ShardingConfig.getInstance().getPolicy();

		return ShardingPolicy.get(policy).getKeyArr(value);

	}

	private String[] getKeys(String keySharding) {

		String policy = ShardingConfig.getInstance().getPolicy();
		return ShardingPolicy.get(policy).getKeyArr(keySharding);

	}

	private String[] getKeys(int key) {

		String policy = ShardingConfig.getInstance().getPolicy();
		return ShardingPolicy.get(policy).getKeyArr(key);
	
	}

	private void tryToInitSql(Class clz) {
		BeanSqlMapper_Sharding.tryToInitSql(clz);
	}

	
	protected void execute (String sql, String keyDb){
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyDb);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);

		}

	}

	@Override
	public <T> long getCount(Class<T> clz, long idOne) {
		tryToInitSql(clz);
		String[] keyArr = getKeys(idOne);
		return getCount(clz, idOne, keyArr);
	}

	private <T> long getCount(Class<T> clz, long idOne, String[] keyArr) {
		long count = 0;

		String sql = BeanSqlMapper_Sharding.getSql(clz, SqlKey.COUNT);
		sql = getSql(sql, keyArr[1]);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			pstmt.setObject(i++, idOne);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getLong("count");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return count;
	}
	
	private long getMaxId(Class clz, Object conditionObj, String[] keyArr) {
		
		long id = 0;

		String sql = BeanSqlMapper.getSql(clz, SqlKey.MAX_ID);
		sql = getSql(sql, keyArr[1]);
		
		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		System.out.println("SyncDao.list(obj)...SQL:  " + sql);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			
			if (parsed.isCombinedKey()){
				String keyOne = parsed.getKey(Persistence.KEY_ONE);
				pstmt.setObject(i++, queryMap.get(keyOne));
			}
			
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				id = rs.getLong("maxId");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}

		return id;
	}


	
	@Override
	public long getMaxId(Object conditionObj) {
		Class clz = conditionObj.getClass();
		tryToInitSql(clz);
		String[] keyArr = getKeys(conditionObj);
		return getMaxId(clz, conditionObj, keyArr);
	}
	
	private boolean execute(String sql, String[] keyArr) {
		boolean b = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(keyArr[0]);
			conn.setAutoCommit(false);
			
			sql = getSql(sql, keyArr[1]);
			pstmt = conn.prepareStatement(sql);


			b = pstmt.executeUpdate() == 0 ? false : true;
			conn.commit();
		}catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				throw new PersistenceException("Persistence Exception: " + e.getStackTrace());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close(conn);
		}
		
		return b;
	}


	@Override
	public boolean execute(Object obj, String sql) {
		Class clz = obj.getClass();
		tryToInitSql(clz);
		String[] keyArr = getKeys(obj);
		
		String clzName = clz.getSimpleName();
		clzName = BeanUtil.getByFirstLower(clzName);
		sql.replace(clzName, clzName + Persistence.SUFFIX);
		
		return execute(sql, keyArr);
		
	}


}
