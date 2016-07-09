package xy.repository.mysql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtil;
import x7.repository.mysql.bean.BeanSqlMapper_Migration;
import x7.repository.mysql.bean.SqlKey;



/**
 * 
 * @author sim
 *
 * @param <T>
 */
public class MigrationDao {

	@SuppressWarnings("rawtypes")
	private static MigrationDao instance;

	@SuppressWarnings("rawtypes")
	public static MigrationDao getInstance() {
		if (instance == null) {
			instance = new MigrationDao();
		}
		return instance;
	}

	private MigrationDao() {
	}

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private Connection getConnection() throws SQLException {
		if (dataSource == null) {
			System.err.println("No DataSource");
		}
		return dataSource.getConnection();
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

	@SuppressWarnings({ "rawtypes" })
	public int create(Object obj) {

		int id = -1;

		Class clz = obj.getClass();

		filterTryToCreate(clz);

		String sql = BeanSqlMapper_Migration.getSql(clz, SqlKey.CREATE);

		List<BeanElement> eles = BeanSqlMapper_Migration.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			/*
			 * 返回自增键
			 */
			Parsed parsed = Parser.get(clz);
			boolean isCombinedKey = parsed.isCombinedKey();
			String keyOne = parsed.getKey(Persistence.KEY_ONE);

			System.out.println("create sql: " + sql);
			int i = 1;
			for (BeanElement ele : eles) {

				if (!isCombinedKey) {
					if (!parsed.isNotAutoIncreament() && ele.property.equals(keyOne)) {
						continue;
					}
				}
				Method method = null;
				try {
					method = obj.getClass().getDeclaredMethod(ele.getter);
				} catch (NoSuchMethodException e) {
					method = obj.getClass().getSuperclass().getDeclaredMethod(ele.getter);
				}
				Object value = method.invoke(obj);
				System.out.println("getter = " + ele.getter + " | value = " + value);
				pstmt.setObject(i++, value);

			}

			pstmt.execute();

			if (parsed.isCombinedKey()) { // 有主键自增
				Method method = null;
				try {
					method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(parsed.getKey(Persistence.KEY_TWO)));
				} catch (NoSuchMethodException e) {
					method = obj.getClass().getSuperclass()
							.getDeclaredMethod(BeanUtil.getGetter(parsed.getKey(Persistence.KEY_TWO)));
				}
				id = (Integer) method.invoke(obj);
			} else {
				ResultSet rs = pstmt.executeQuery("SELECT last_insert_id();");
				if (rs.next()) {
					id = rs.getInt(1);
					System.out.println("autoIncrementId = " + id);
				}
			}
			conn.commit();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
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


	@SuppressWarnings({ "unchecked" })
	public <T> List<T> list(Class<T> clz, int id) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Migration.getSql(clz, SqlKey.QUERY);
		List<BeanElement> eles = BeanSqlMapper_Migration.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
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
						Method method = null;
						try {
							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
						} catch (NoSuchMethodException e) {
							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
						}
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

	@SuppressWarnings({ "unchecked" })
	public <T> T get(Class<T> clz, int idOne, int idTwo) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Migration.getSql(clz, SqlKey.QUERY_TWO);
		List<BeanElement> eles = BeanSqlMapper_Migration.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
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
						Method method = null;
						try {
							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
						} catch (NoSuchMethodException e) {
							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
						}
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

	@SuppressWarnings({ "unchecked" })
	public <T> List<T> list(Class<T> clz) {

		filterTryToCreate(clz);

		List<T> list = new ArrayList<T>();

		String sql = BeanSqlMapper_Migration.getSql(clz, SqlKey.LOAD);
		List<BeanElement> eles = BeanSqlMapper_Migration.getElementList(clz);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					T obj = clz.newInstance();
					list.add(obj);
					for (BeanElement ele : eles) {
						Method method = null;
						try {
							method = obj.getClass().getDeclaredMethod(ele.setter, ele.clz);
						} catch (NoSuchMethodException e) {
							method = obj.getClass().getSuperclass().getDeclaredMethod(ele.setter, ele.clz);
						}
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

	

	private void filterTryToCreate(Class clz) {

		String sql = BeanSqlMapper_Migration.tryToCreate(clz);
		if (sql == null || sql.equals(""))
			return;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql);

			pstmt.execute();
			
			String index = BeanSqlMapper_Migration.getSql(clz, SqlKey.INDEX);
			if (index != null){
				pstmt = conn.prepareStatement(index);
				pstmt.execute();
			}
			
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


}
