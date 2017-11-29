package x7.repository.dao;

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

	private static Map<Long, Connection> connMap = new ConcurrentHashMap<>();
	private static Map<Long, List<Statement>> map = new ConcurrentHashMap<Long, List<Statement>>();

	private static List<Statement> remove() {
		long threadId = Thread.currentThread().getId();
		return map.remove(threadId);
	}
	
	
	protected static void add(Connection connection){
		long threadId = Thread.currentThread().getId();
		connMap.put(threadId, connection);
	}
	
	protected static Connection getConnection(){
		long threadId = Thread.currentThread().getId();
		return connMap.get(threadId);
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
		
		long threadId = Thread.currentThread().getId();
		Connection connection = connMap.remove(threadId);
		
		if (connection == null){
			remove();
			return;
		}
		
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			List<Statement> list = remove();
			if (list != null) {
				for (Statement stmt : list) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static void rollback() {
		
		long threadId = Thread.currentThread().getId();
		Connection connection = connMap.remove(threadId);
		
		if (connection == null){
			remove();
			return;
		}
		
		try {
			connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			List<Statement> list = remove();
			if (list != null) {
				for (Statement stmt : list) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
