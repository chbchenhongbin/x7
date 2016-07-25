package x7.repository.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Tx仅仅是本地事务，不支持分布式事务<br>
 * 同一事务下，严格禁止切换线程，必须同步执行<br>
 * 使用时，优先考虑乐观锁, refresh(obj, map);<br>
 * <br>
 * Sample：<br>
 * <br>
 * <hr>
 * <br>
 * 
 * Tx.begin(); <br>
 * (ok) Tx.commit(); <br>
 * (exception) Tx.rollback(); <br>
 * <br>
 * 
 * @author Sim
 *
 */
public class Tx {

	private static Map<Long, List<Statement>> map = new ConcurrentHashMap<Long, List<Statement>>();

	private static List<Statement> remove() {
		long threadId = Thread.currentThread().getId();
		return map.remove(threadId);
	}

	protected static boolean isNoBizTx() {
		long threadId = Thread.currentThread().getId();
		return !map.containsKey(threadId);
	}

	protected static void add(Statement stmt) {
		long threadId = Thread.currentThread().getId();
		List<Statement> list = map.get(threadId);
		list.add(stmt);
	}

	public static void begin() {
		long threadId = Thread.currentThread().getId();
		map.put(threadId, new ArrayList<Statement>());
	}

	public static void commit() {

		List<Statement> list = remove();
		if (list == null || list.isEmpty())
			return;
		for (Statement stmt : list) {
			Connection connection = null;
			try {
				connection = stmt.getConnection();

				if (connection != null && !connection.isClosed()) {
					connection.commit();
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}finally{
				try{
					stmt.close();
					if (connection != null && !connection.isClosed()){
						connection.close();
					}
				}catch(Exception e) {
					
				}
			}
		}
	}

	public static void rollback() {

		List<Statement> list = remove();
		if (list == null || list.isEmpty())
			return;
		for (Statement stmt : list) {
			Connection connection = null;
			try {
				connection = stmt.getConnection();

				if (connection != null && !connection.isClosed()) {
					connection.rollback();
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}finally{
				try{
					stmt.close();
					if (connection != null && !connection.isClosed()){
						connection.close();
					}
				}catch(Exception e) {
					
				}
			}
		}
	}

//	public static void end() {
//
//		List<Statement> list = remove();
//		if (list == null || list.isEmpty())
//			return;
//		try {
//			for (Statement stmt : list) {
//				Connection connection = stmt.getConnection();
//				System.out.println("connection: " + connection);
//				stmt.close();
//				if (connection != null && !connection.isClosed()) {
//					connection.close();
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}

}
