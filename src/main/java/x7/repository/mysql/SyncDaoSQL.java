package x7.repository.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.mysql.jdbc.Statement;

import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;
import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.RollbackException;
import x7.repository.mysql.bean.BeanSqlMapper;
import x7.repository.mysql.bean.SqlKey;


/**
 * 
 * @author sim
 *
 * 
 */
public class SyncDaoSQL implements ISyncDao {

	private static SyncDaoSQL instance;

	public static SyncDaoSQL getInstance() {
		if (instance == null) {
			instance = new SyncDaoSQL();
		}
		return instance;
	}

	private SyncDaoSQL() {
	}

	private DataSource dataSource;

	private DataSource dataSource_R;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDataSource_Slaver(DataSource dataSource_Slaver) {
		this.dataSource_R = dataSource_Slaver;
	}

	private Connection getConnection(boolean isRead) throws SQLException {
		if (dataSource == null) {
			System.err.println("No DataSource");
		}
		if (dataSource_R == null) {
			return getConnection(dataSource);
		}

		if (isRead) {
			return getConnection(dataSource_R);
		}
		
		return getConnection(dataSource);
	}
	
	private Connection getConnection(DataSource ds) throws SQLException {
		Connection c = ds.getConnection();
		
		if (c == null) {
			try {
				TimeUnit.MICROSECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ds.getConnection();
		}

		return c;
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
	
	private static void close (PreparedStatement pstmt){
		if (pstmt != null){
			try{
				pstmt.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private String[] getResultKey(String key){
		String[] kArr = new String[2];
		kArr[0] = key;
		kArr[1] = key;
		if (key.contains(" ")){
			String[] arr = key.split(" ");
			kArr[0] = arr[1];
			kArr[1] = arr[1];
		}else {
			if (key.contains(".")){
				kArr[0] = key.substring(key.indexOf(".") + 1);
			}
		}
		return kArr;
	}

	@SuppressWarnings({ "rawtypes" })
	public long create(Object obj) {

		long id = -1;

		Class clz = obj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.CREATE);

		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		boolean isNoBizTx = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			Parsed parsed = Parser.get(clz);
			boolean isCombinedKey = parsed.isCombinedKey();
			String keyOne = parsed.getKey(Persistence.KEY_ONE);

			Long keyOneValue = 0L;
			Field keyOneField = parsed.getKeyField(Persistence.KEY_ONE);
			Class keyOneType = keyOneField.getType();
			if (keyOneType != String.class) {
				keyOneValue = parsed.getKeyField(Persistence.KEY_ONE).getLong(obj);
			}

			/*
			 * 返回自增键
			 */
			Long keyTwoValue = null;
			if (isCombinedKey) {
				keyTwoValue = parsed.getKeyField(Persistence.KEY_TWO).getLong(obj);
				if (keyTwoValue == null || keyTwoValue == 0) {
					keyTwoValue = this.getMaxId_Master(clz, parsed.getKeyField(Persistence.KEY_ONE).getLong(obj)) + 1;// MUST
					// KEY_ONE
					System.out.println("keyTwoValue = " + keyTwoValue);
					parsed.getKeyField(Persistence.KEY_TWO).set(obj, keyTwoValue.intValue());
				}
			}

			conn = getConnection(false);

			conn.setAutoCommit(false);
			if (keyOneType != String.class && (keyOneValue == null || keyOneValue == 0)) {
				pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				pstmt = conn.prepareStatement(sql);
			}

			isNoBizTx = Tx.isNoBizTx();
			if (!isNoBizTx) {
				Tx.add(pstmt);
			}

			int i = 1;
			for (BeanElement ele : eles) {

				if (!isCombinedKey) {
					if (!parsed.isNotAutoIncreament() && ele.property.equals(keyOne)) {
						continue;
					}
				}

				Object value = ele.getMethod.invoke(obj);
				if (value == null) {
					if (ele.clz == Boolean.class || ele.clz == Integer.class || ele.clz == Long.class
							|| ele.clz == Double.class || ele.clz == Float.class)
						value = 0;
				}

				value = SqlUtil.filter(value);
				
				pstmt.setObject(i++, value);

			}

			pstmt.execute();

			if (parsed.isCombinedKey()) { // 有主键自增
				id = keyTwoValue;
			} else {
				if (keyOneType != String.class && (keyOneValue == null || keyOneValue == 0)) {
					ResultSet rs = pstmt.getGeneratedKeys();
					if (rs.next()) {
						id = rs.getLong(1);
					}
					// ResultSet rs = pstmt.executeQuery("SELECT
					// last_insert_id();");
					// if (rs.next()) {
					// id = rs.getLong(1);
					// }
				} else {
					id = keyOneValue;
				}
			}
			
			if (isNoBizTx) {
				conn.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (isNoBizTx) {
				try {
					conn.rollback();
					System.out.println("line 199" + e.getMessage());
					e.printStackTrace();

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				throw new RollbackException("RollbackException: " + e.getMessage());
			}
		} finally {
			if (isNoBizTx) {
				close(pstmt);
				close(conn);
			}
		}

		return id;
	}

	public boolean refresh(Object obj) {

		@SuppressWarnings("rawtypes")
		Class clz = obj.getClass();

		filterTryToCreate(clz);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getRefreshMap(parsed, obj);

		String tableName = BeanSqlMapper.getTableName(clz);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" ");
		String sql = SqlUtil.concatRefresh(sb, parsed, queryMap);

		// System.out.println("refreshOptionally: " + sql);

		boolean flag = false;
		boolean isNoBizTx = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			isNoBizTx = Tx.isNoBizTx();
			if (!isNoBizTx) {
				Tx.add(pstmt);
			}

			int i = 1;
			for (Object value : queryMap.values()) {
				value = SqlUtil.filter(value);
				pstmt.setObject(i++, value);
			}

			/*
			 * 处理KEY
			 */
			Field keyOneF = parsed.getKeyField(Persistence.KEY_ONE);
			Field keyTwoF = parsed.getKeyField(Persistence.KEY_TWO);
			SqlUtil.adpterSqlKey(pstmt, keyOneF, keyTwoF, obj, i);

			
			flag = pstmt.executeUpdate() == 0 ? false : true;

			if (isNoBizTx) {
				conn.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (isNoBizTx) {
				try {
					conn.rollback();
					System.out.println("line 275 " + e.getMessage());
					e.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				throw new RollbackException("RollbackException: " + e.getMessage());
			}
		} finally {
			if (isNoBizTx) {
				close(pstmt);
				close(conn);
			}
		}

		return flag;
	}

	@SuppressWarnings("rawtypes")
	public boolean remove(Object obj) {

		Class clz = obj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.REMOVE);

		boolean flag = false;
		boolean isNoBizTx = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			isNoBizTx = Tx.isNoBizTx();
			if (!isNoBizTx) {
				Tx.add(pstmt);
			}

			Parsed parsed = Parser.get(clz);

			int i = 1;

			SqlUtil.adpterSqlKey(pstmt, parsed.getKeyField(Persistence.KEY_ONE),
					parsed.getKeyField(Persistence.KEY_TWO), obj, i);

			flag = pstmt.executeUpdate() == 0 ? false : true;

			if (isNoBizTx) {
				conn.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (isNoBizTx) {
				try {
					conn.rollback();
					System.out.println("line 334 " + e.getMessage());
					e.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				throw new RollbackException("RollbackException: " + e.getMessage());
			}
		} finally {
			if (isNoBizTx) {
				close(pstmt);
				close(conn);
			}
		}

		return flag;
	}

	@Override
	public <T> T get(Class<T> clz, long idOne) {
		filterTryToCreate(clz);

		Parsed parsed = Parser.get(clz);
		if (parsed.isCombinedKey()) {
			throw new PersistenceException("CombinedKey object should get(clz, idOne, idTwo)");
		}

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.QUERY);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
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
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	public <T> List<T> list(Class<T> clz, long id) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.QUERY);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
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
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	public <T> T get(Class<T> clz, long idOne, long idTwo) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.QUERY_TWO);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
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
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }

						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		if (list.isEmpty())
			return null;

		return list.get(0);
	}

	public List<Map<String,Object>> list(Class clz, String sql, List<Object> conditionList) {
		
		sql  = sql.replace("drop"," ").replace("delete"," ").replace("insert"," ").replace(";",""); //手动拼接SQL, 必须考虑应用代码的漏洞
		
		filterTryToCreate(clz);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object obj : conditionList) {
				pstmt.setObject(i++, obj);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> mapR = new HashMap<String, Object>();
					list.add(mapR);
					ResultSetMetaData rsmd = rs.getMetaData();
					int count = rsmd.getColumnCount();
					for (i = 1; i <= count; i++) {
						String key = rsmd.getColumnLabel(i);
						String value = rs.getString(i);
						mapR.put(key, value);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	public <T> List<T> list(Class<T> clz) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.LOAD);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
						// try {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	@SuppressWarnings("rawtypes")
	private long getMaxId_Master(Class clz, long key) {// FIXME

		long id = 0;

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.MAX_ID);
		System.out.println("SQL getMaxId = " + sql + ", key = " + key);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
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
			close(pstmt);
			close(conn);
		}

		return id;
	}

	@SuppressWarnings("rawtypes")
	public long getMaxId(Class clz, long key) {

		long id = 0;

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.MAX_ID);
		System.out.println("SQL getMaxId = " + sql + ", key = " + key);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
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
			close(pstmt);
			close(conn);
		}

		return id;
	}

	@SuppressWarnings("rawtypes")
	public long getMaxId(Class clz) {

		long id = 0;

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.MAX_ID);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					id = rs.getLong("maxId");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return id;
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	private void filterTryToCreate(Class clz) {

		String sql = BeanSqlMapper.tryToCreate(clz);
		if (sql == null || sql.equals(""))
			return;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			pstmt.execute();

			String index = BeanSqlMapper.getSql(clz, SqlKey.INDEX);
			if (index != null) {
				pstmt = conn.prepareStatement(index);
				pstmt.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close(pstmt);
			close(conn);

		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination) {

		filterTryToCreate(clz);

		Parsed parsed = Parser.get(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.PAGINATION);
		sql += (" AND " + parsed.getKey(Persistence.KEY_ONE));
		sql += " = ?";

		long count = 0;
		if (! pagination.isScroll()){
			count = getCount(sql.replace(Persistence.PAGINATION, "COUNT(*) count"), idOne);
		}

		pagination.setTotalRows(count);
		int page = pagination.getPage();
		int rows = pagination.getRows();
		int start = (page - 1) * rows;

		sql = sql.replace(Persistence.PAGINATION, " * ");

		if (pagination instanceof PaginationSorted) {
			PaginationSorted ps = (PaginationSorted) pagination;
			sql = sql + " order by  " + ps.getOrderBy() + " " + ps.getSc();
		}

		sql = sql + " LIMIT " + start + "," + rows;

		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
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
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return pagination;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> list(Object conditionObj) {

		Class clz = conditionObj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.LOAD);

		sql = sql.concat(" WHERE 1=1");

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		// System.out.println("SyncDao.list(obj)...SQL: " + sql);

		List<T> list = new ArrayList<T>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					T obj = (T) clz.newInstance();
					list.add(obj);
					for (BeanElement ele : parsed.getBeanElementList()) {
						Method method = ele.setMethod;
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));

						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> Pagination<T> list(Object conditionObj, Pagination<T> pagination) {

		Class clz = conditionObj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.PAGINATION);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		// System.out.println("SyncDao.list(conditionObj, pagination)...SQL: " +
		// sql);

		String countSql = sql.replace(Persistence.PAGINATION, "COUNT(*) count");

		long count = 0;
		if (! pagination.isScroll()){
			count = getCount(countSql, queryMap);
		}

		pagination.setTotalRows(count);
		int page = pagination.getPage();
		int rows = pagination.getRows();
		int start = (page - 1) * rows;

		sql = sql.replace(Persistence.PAGINATION, " * ");

		if (pagination instanceof PaginationSorted) {
			PaginationSorted ps = (PaginationSorted) pagination;
			sql = sql + " order by  " + ps.getOrderBy() + " " + ps.getSc();
		}

		// sql = sql + " LIMIT " + start + "," + (start + rows);
		sql = sql + " LIMIT " + start + "," + rows;
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = (T) clz.newInstance();
					pagination.getList().add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return pagination;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> Pagination<T> list(Criteria criteria, Pagination<T> pagination) {

		Class clz = criteria.getClz();
		filterTryToCreate(clz);

		List<Object> valueList = criteria.getValueList();

		String[] sqlArr = CriteriaBuilder.parse(criteria);

		String sqlCount = sqlArr[0];
		String sql = sqlArr[1];

		long count = 0;
		if (! pagination.isScroll()){
			count = getCount(sqlCount, valueList);
		}

		pagination.setTotalRows(count);
		int page = pagination.getPage();
		int rows = pagination.getRows();
		int start = (page - 1) * rows;

		sql = sql + " LIMIT " + start + "," + rows;

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object obj : valueList) {
				pstmt.setObject(i++, obj);
			}
	
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				
				List<BeanElement> eles = BeanSqlMapper.getElementList(clz);
				
				while (rs.next()) {

					T obj = (T) clz.newInstance();
					pagination.getList().add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;
	
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}

				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return pagination;
	}

	public int getCount(String sql, long idOne) {

		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
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
			close(pstmt);
			close(conn);
		}

		return count;
	}

	@Override
	public Object getSum(Object conditionObj, String sumProperty) {

		Class<?> clz = conditionObj.getClass();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.PAGINATION);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		String countSql = sql.replace(Persistence.PAGINATION, "SUM(*) sum");
		countSql = countSql.replace("*", sumProperty);

		Object sum = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(countSql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// Object obj = rs.getObject("sum");
				sum = rs.getObject("sum");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return sum;
	}

	@Override
	public Object getSum(String sumProperty, Criteria criteria) {

		Class<?> clz = criteria.getClz();
		filterTryToCreate(clz);

		List<Object> valueList = criteria.getValueList();

		String[] sqlArr = CriteriaBuilder.parse(criteria);

		String sqlSum = sqlArr[2];

		sqlSum = sqlSum.replace(Persistence.PAGINATION, "SUM(*) sum");
		sqlSum = sqlSum.replace("*", sumProperty);

		Object count = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sqlSum);

			int i = 1;
			for (Object o : valueList) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// Object obj = rs.getObject("sum");
				count = rs.getObject("sum");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;
	}

	public long getCount(String sql, Map<String, Object> queryMap) {

		long count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getLong("count");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;
	}

	public long getCount(String sql, Collection<Object> set) {

		long count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object obj : set) {
				pstmt.setObject(i++, obj);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getLong("count");
				// if (obj != null) {
				// count = ((Long) obj).intValue();
				// }
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;
	}

	@Override
	public long getCount(Object conditionObj) {

		Class<?> clz = conditionObj.getClass();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.PAGINATION);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		String countSql = sql.replace(Persistence.PAGINATION, "COUNT(*) count");

		long count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(countSql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getLong("count");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;

	}

	@Override
	public <T> long getCount(Class<T> clz, long idOne) {

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.COUNT);

		long count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			pstmt.setObject(i++, idOne);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Object obj = rs.getObject("count");
				if (obj != null) {
					count = ((Long) obj);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T getOne(T conditionObj, String orderBy, String sc) {

		Class clz = conditionObj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.LOAD);

		sql = sql.concat(" WHERE 1=1 ");

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		sql = sql + " order by " + orderBy + " " + sc;
		sql = sql + " limit 1";

		// System.out.println("SyncDao.list(obj)...SQL: " + sql);

		List<Object> list = new ArrayList<Object>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					Object obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : parsed.getBeanElementList()) {
						Method method = ele.setMethod;
						// try {
						// method =
						// obj.getClass().getSuperclass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// } catch (NoSuchMethodException e) {
						// method = obj.getClass().getDeclaredMethod(ele.setter,
						// ele.clz);
						// }
						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		if (list.isEmpty())
			return null;

		return (T) list.get(0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public long getMaxId(Object conditionObj) {

		long id = 0;

		Class clz = conditionObj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper.getSql(clz, SqlKey.PAGINATION);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getQueryMap(parsed, conditionObj);
		sql = SqlUtil.concat(sql, queryMap);

		sql = sql.replace(Persistence.PAGINATION, "max(id) maxId");

		// System.out.println("SyncDao.list(obj)...SQL: " + sql);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;

			// if (parsed.isCombinedKey()) {
			// String keyOne = parsed.getKey(Persistence.KEY_ONE);
			// pstmt.setObject(i++, queryMap.get(keyOne));
			// }

			for (Object o : queryMap.values()) {
				pstmt.setObject(i++, o);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Object obj = rs.getObject("maxId");
				if (obj != null) {
					id = Long.valueOf(obj.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return id;
	}

	/**
	 * 没有特殊需求，请不要调用此代码
	 * 
	 * @param sql
	 */
	@Deprecated
	@Override
	public boolean execute(Object obj, String sql) {
		
		sql  = sql.replace("drop"," ").replace("delete"," ").replace("insert"," ").replace(";",""); //手动拼接SQL, 必须考虑应用代码的漏洞
		
		boolean b = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			b = pstmt.executeUpdate() == 0 ? false : true;
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				System.out.println("line 1560 " + e.getMessage());
				e.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			close(pstmt);
			close(conn);
		}

		return b;
	}

	@Override
	public boolean refresh(Object obj, Map<String, Object> conditionMap) {

		@SuppressWarnings("rawtypes")
		Class clz = obj.getClass();

		filterTryToCreate(clz);

		Parsed parsed = Parser.get(clz);

		Map<String, Object> queryMap = BeanUtilX.getRefreshMap(parsed, obj);

		String tableName = BeanSqlMapper.getTableName(clz);
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(tableName).append(" ");
		String sql = SqlUtil.concatRefresh(sb, parsed, queryMap, conditionMap);

		// System.out.println("refreshOptionally: " + sql);

		boolean isNoBizTx = false;
		boolean flag = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(false);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			isNoBizTx = Tx.isNoBizTx();
			if (!isNoBizTx) {
				Tx.add(pstmt);
			}

			int i = 1;
			for (Object value : queryMap.values()) {
				value = SqlUtil.filter(value);
				pstmt.setObject(i++, value);
			}

			/*
			 * 处理KEY
			 */
			Field keyOneF = parsed.getKeyField(Persistence.KEY_ONE);
			Field keyTwoF = parsed.getKeyField(Persistence.KEY_TWO);
			SqlUtil.adpterRefreshCondition(pstmt, keyOneF, keyTwoF, obj, i, conditionMap);

			flag = pstmt.executeUpdate() == 0 ? false : true;
			
			if (isNoBizTx) {
				conn.commit();
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			if (isNoBizTx) {
				try {
					conn.rollback();
					System.out.println("line 1675 " + e.getMessage());
					e.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				throw new RollbackException("RollbackException: " + e.getMessage());
			}
		} finally {
			if (isNoBizTx) {
				close(pstmt);
				close(conn);
			}
		}

		return flag;
	}

	@Override
	public Object getCount(String countProperty, Criteria criteria) {

		Class<?> clz = criteria.getClz();
		filterTryToCreate(clz);

		List<Object> valueList = criteria.getValueList();

		String[] sqlArr = CriteriaBuilder.parse(criteria);

		String sqlSum = sqlArr[2];

		sqlSum = sqlSum.replace(Persistence.PAGINATION, "COUNT(*) count");
		sqlSum = sqlSum.replace("*", countProperty);

		Object count = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sqlSum);

			int i = 1;
			for (Object o : valueList) {
				pstmt.setObject(i++, o);
			}
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// Object obj = rs.getObject("sum");
				count = rs.getObject("count");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return count;
	}

	@Override
	public <T> List<T> in(Class<T> clz, List<? extends Object> inList) {
		filterTryToCreate(clz);

		Parsed parsed = Parser.get(clz);

		if (parsed.isCombinedKey()) {
			throw new RuntimeException(
					"CombinedKey not supported: in(Class<T> clz, String inProperty, List<Object> inList)");
		}

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.LOAD);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		String keyOne = parsed.getKey(Persistence.KEY_ONE);

		Field keyField = parsed.getKeyField(Persistence.KEY_ONE);
		Class<?> keyType = keyField.getType();
		boolean isNumber = (keyType == long.class || keyType == int.class || keyType == Long.class
				|| keyType == Integer.class);

		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" WHERE ").append(keyOne);
		sb.append(" in (");

		int size = inList.size();
		if (isNumber) {
			for (int i = 0; i < size; i++) {
				Object id = inList.get(i);
				if (id == null)
					continue;
				sb.append(id);
				if (i < size - 1) {
					sb.append(",");
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				Object id = inList.get(i);
				if (id == null || StringUtil.isNullOrEmpty(id.toString()))
					continue;
				sb.append("'").append(id).append("'");
				if (i < size - 1) {
					sb.append(",");
				}
			}
		}

		sb.append(")");

		sql = sb.toString();

		System.out.println(sql);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;

						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	@Override
	public <T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList) {
		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper.getSql(clz, SqlKey.LOAD);
		List<BeanElement> eles = BeanSqlMapper.getElementList(clz);

		Parsed parsed = Parser.get(clz);

		BeanElement be = parsed.getElement(inProperty);
		if (be == null) {
			throw new RuntimeException(
					"Exception in method: <T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList), no property: "
							+ inProperty);
		}
		Class<?> keyType = be.getMethod.getReturnType();
		boolean isNumber = (keyType == long.class || keyType == int.class || keyType == Long.class
				|| keyType == Integer.class);

		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" WHERE ").append(inProperty);
		sb.append(" in (");

		int size = inList.size();
		if (isNumber) {
			for (int i = 0; i < size; i++) {
				Object id = inList.get(i);
				if (id == null)
					continue;
				sb.append(id);
				if (i < size - 1) {
					sb.append(",");
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				Object id = inList.get(i);
				if (id == null || StringUtil.isNullOrEmpty(id.toString()))
					continue;
				sb.append("'").append(id).append("'");
				if (i < size - 1) {
					sb.append(",");
				}
			}
		}

		sb.append(")");

		sql = sb.toString();

		System.out.println(sql);

		Connection conn = null;
		PreparedStatement pstmt = null;
		BeanElement tempEle = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = ele.setMethod;

						if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
							Object v = rs.getObject(ele.property);
							if (v != null) {
								method.invoke(obj, Double.valueOf(String.valueOf(v)));
							}
						} else {
							tempEle = ele;
							method.invoke(obj, rs.getObject(ele.property));
						}
					}
				}
			}

		} catch (Exception e) {
			if (tempEle != null) {
				System.out
						.println("Exception occured by class = " + clz.getName() + ", property = " + tempEle.property);
			}
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return list;
	}

	@Override
	public Pagination<Map<String, Object>> list(Criteria.Join criteriaJoinable,
			Pagination<Map<String, Object>> pagination) {

		return this.listX(criteriaJoinable, pagination);
	}

	private Pagination<Map<String, Object>> listX(Criteria.Join criteriaJoinable, Pagination<Map<String, Object>> pagination) {

		Class clz = criteriaJoinable.getClz();
		filterTryToCreate(clz);

		List<Object> valueList = criteriaJoinable.getValueList();

		String[] sqlArr = CriteriaBuilder.parse(criteriaJoinable);

		String sqlCount = sqlArr[0];
		String sql = sqlArr[1];

		long count = 0;
		if (! pagination.isScroll()){
			count = getCount(sqlCount, valueList);
		}
		pagination.setTotalRows(count);
		int page = pagination.getPage();
		int rows = pagination.getRows();
		int start = (page - 1) * rows;

		sql = sql + " LIMIT " + start + "," + rows;

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object obj : valueList) {
				pstmt.setObject(i++, obj);
			}


			List<String> columnList = criteriaJoinable.getColumnList();
			if (columnList.isEmpty()) {
				columnList = criteriaJoinable.listAllColumn();
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> mapR = new HashMap<String, Object>();
					pagination.getList().add(mapR);

					for (String key : columnList) {
						String[] kArr = getResultKey(key);
						mapR.put(kArr[0], rs.getObject(kArr[1]));
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}

		return pagination;
	}

	@Override
	public List<Map<String, Object>> list(Criteria.Join criteriaJoinable) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		Class clz = criteriaJoinable.getClz();
		filterTryToCreate(clz);

		List<Object> valueList = criteriaJoinable.getValueList();

		String[] sqlArr = CriteriaBuilder.parse(criteriaJoinable);

		String sql = sqlArr[1];

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection(true);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			int i = 1;
			for (Object obj : valueList) {
				pstmt.setObject(i++, obj);
			}

			List<String> columnList = criteriaJoinable.getColumnList();
			if (columnList.isEmpty()) {
				columnList = criteriaJoinable.listAllColumn();
			}
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> mapR = new HashMap<String, Object>();
					list.add(mapR);

					for (String key : columnList) {
						String[] kArr = getResultKey(key);
						mapR.put(kArr[0], rs.getObject(kArr[1]));
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(conn);
		}
		
		return list;
	}

}
