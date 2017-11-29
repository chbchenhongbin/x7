package x7.repository.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sql.DataSource;

import x7.core.bean.Criteria;
import x7.core.bean.Criteria.Fetch;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.config.Configs;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;
import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.RollbackException;
import x7.repository.exception.ShardingException;
import x7.repository.sharding.ShardingPolicy;

/**
 * 
 * Sharding MySQL
 * @author Sim
 *
 */
public class ShardingDaoImpl implements ShardingDao {

	private final static String FALL_LINE = "_";
	private ExecutorService service = Executors.newCachedThreadPool();
	
	private static ShardingDaoImpl instance;

	public static ShardingDaoImpl getInstance() {
		if (instance == null) {
			instance = new ShardingDaoImpl();
		}
		return instance;
	}

	private ShardingDaoImpl() {
	}

	private Map<String, DataSource> dsWMap;
	private Map<String, DataSource> dsRMap;

	public void setDsWMap(Map<String, DataSource> dsWMap) {
		this.dsWMap = dsWMap;
	}

	public void setDsRMap(Map<String, DataSource> dsRMap) {
		this.dsRMap = dsRMap;
	}

	private String getKeyFieldName(Class clz) {
		Parsed parsed = Parser.get(clz);

		String fieldName = null;
		try {
			fieldName = parsed.getKey(Persistence.KEY_SHARDING);
			if (fieldName == null) {
				fieldName = parsed.getKey(Persistence.KEY_ONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fieldName;

	}

	private String getKey(Object obj) {
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
		if (idOne.equals(keySharding)) {
			if (value.equals("0"))
				throw new ShardingException(
						"\n SHARDING NO VALUE, IF SHARDING = IDONE, SHARDING CAN BE NOT 0, \n obj = " + obj + "\n");
		}

		if (value.equals("")) {
			throw new ShardingException("SHARDING VALUE IS NULL, ojb = " + obj);
		}

		String policy = Configs.getString("x7.db.sharding.policy");

		return ShardingPolicy.get(policy).getKey(value);

	}

	private String getKey(long key) {

		String policy = Configs.getString("x7.db.sharding.policy");
		return ShardingPolicy.get(policy).getKey(key);

	}

	private String getKey(String key) {

		String policy = Configs.getString("x7.db.sharding.policy");
		return ShardingPolicy.get(policy).getKey(key);

	}

	@SuppressWarnings({ "rawtypes"})
	private String getKey(Criteria criteria) {
		String key = null;

		Class clz = criteria.getClz();

		String keyFieldName = getKeyFieldName(clz);

		if (StringUtil.isNotNull(keyFieldName)) {
			Object obj = criteria.getAndMap().get(keyFieldName);
			if (obj == null) {
				obj = criteria.getOrMap().get(keyFieldName);
			}
			if (!Objects.isNull(obj)) {
				if (obj.getClass() == long.class || obj instanceof Long) {
					key = getKey(Long.valueOf(obj.toString()).longValue());
				} else {
					key = getKey(obj.toString());
				}
			}
		}

		return key;
	}

	private Connection getConnectionForMasterId(String key) throws SQLException {
		DataSource dataSource = dsWMap.get(key);
		return getConnection(dataSource);
	}

	private Connection getConnection(String key, boolean isRead) throws SQLException {

		if (dsRMap == null || dsRMap.isEmpty()) {// ONLY WRITE

			if (!isRead) {
				if (!Tx.isNoBizTx()) {
					Connection connection = Tx.getConnection();
					if (connection == null) {
						DataSource dataSource = dsWMap.get(key);
						if (dataSource == null) {
							throw new RollbackException("No DataSource");
						}
						connection = getConnection(dataSource);
						Tx.add(connection);
					}
					return connection;
				}
			}
			DataSource dataSource = dsWMap.get(key);
			if (dataSource == null) {
				throw new RollbackException("No DataSource");
			}
			return getConnection(dataSource);
		}

		if (isRead) {// READ
			DataSource dataSource = dsRMap.get(key);
			return getConnection(dataSource);
		}

		if (!Tx.isNoBizTx()) {
			Connection connection = Tx.getConnection();
			if (connection == null) {
				DataSource dataSource = dsWMap.get(key);
				if (dataSource == null) {
					throw new RollbackException("No DataSource");
				}
				connection = getConnection(dataSource);
				Tx.add(connection);
			}
			return connection;
		}

		DataSource dataSource = dsWMap.get(key);
		if (dataSource == null) {
			throw new RollbackException("No DataSource");
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

	private static void close(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void tryToParse(Class clz) {
		Parsed parsed = Parser.get(clz);
	}

	private long create(Object obj, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, false);
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		long id = DaoImpl.getInstance().create(obj, conn);
		return id;
	}

	@Override
	public long create(Object obj) {

		tryToParse(obj.getClass());
		String key = getKey(obj);
		return create(obj, key);
	}

	private boolean refresh(Object obj, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, false);
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		boolean flag = DaoImpl.getInstance().refresh(obj, conn);
		return flag;

	}

	@Override
	public boolean refresh(Object obj) {
		tryToParse(obj.getClass());
		String key = getKey(obj);
		return refresh(obj, key);
	}

	private boolean refresh(Object obj, Map<String, Object> conditionMap, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, false);
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		boolean flag = DaoImpl.getInstance().refresh(obj, conditionMap, conn);
		return flag;

	}

	@Override
	public boolean refresh(Object obj, Map<String, Object> conditionMap) {
		tryToParse(obj.getClass());
		String key = getKey(obj);
		return refresh(obj, conditionMap, key);
	}

	private boolean remove(Object obj, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, false);
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		boolean flag = DaoImpl.getInstance().remove(obj, conn);
		return flag;
	}

	@Override
	public boolean remove(Object obj) {
		tryToParse(obj.getClass());
		String key = getKey(obj);
		return remove(obj, key);
	}

	@Override
	public boolean execute(Object obj, String sql) {
		tryToParse(obj.getClass());
		String key = getKey(obj);

		return false;
	}

	private <T> T get(Class<T> clz, long idOne, long idTwo, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		T t = DaoImpl.getInstance().get(clz, idOne, idTwo, conn);
		return t;
	}

	@Override
	public <T> T get(Class<T> clz, long idOne, long idTwo) {

		tryToParse(clz);
		String key = getKey(idOne);

		return get(clz, idOne, idTwo, key);
	}

	private <T> T get(Class<T> clz, long idOne, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		T t = DaoImpl.getInstance().get(clz, idOne, conn);
		return t;
	}

	@Override
	public <T> T get(Class<T> clz, long idOne) {

		tryToParse(clz);
		String key = getKey(idOne);

		return get(clz, idOne, key);
	}

	private <T> T getOne(T conditionObj, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		T t = DaoImpl.getInstance().getOne(conditionObj, conn);
		return t;
	}

	@Override
	public <T> T getOne(T conditionObj) {

		tryToParse(conditionObj.getClass());
		String key = getKey(conditionObj);

		return getOne(conditionObj, key);
	}

	private <T> long getCount(Class<T> clz, long idOne, String key) {

		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		long count = DaoImpl.getInstance().getCount(clz, idOne, conn);
		return count;
	}

	@Override
	public <T> long getCount(Class<T> clz, long idOne) {
		tryToParse(clz);
		String key = getKey(idOne);

		return getCount(clz, idOne, key);
	}

	private <T> Pagination<T> list(Criteria criteria, Pagination<T> pagination, String key) {
		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		Pagination<T> p = DaoImpl.getInstance().list(criteria, pagination, conn);
		return p;
	}

	@Override
	public <T> Pagination<T> list(Criteria criteria, Pagination<T> pagination) {

		tryToParse(criteria.getClz());

		String key = getKey(criteria);

		if (StringUtil.isNotNull(key)) {
			return list(criteria, pagination, key);
		}

		String policy = Configs.getString("x7.db.sharding.policy");
		String[] keyArr = ShardingPolicy.get(policy).getSuffixArr();

		Map<String, Pagination<T>> resultMap = new HashMap<>();

		/*
		 * map script
		 */
		final int page = pagination.getPage();
		final int rows = pagination.getRows();

		Map<String,Future<Pagination<T>>> futureMap = new HashMap<>();

		for (String k : keyArr) {

			Callable<Pagination<T>> task = new Callable<Pagination<T>>() {

				@Override
				public Pagination<T> call() throws Exception {

					Pagination<T> p = new Pagination<T>();
					p.setPage(1);
					p.setRows(page * rows);
					try {
						p = list(criteria, p, k);
					} catch (Exception e) {
						for (Future<Pagination<T>> f : futureMap.values()){
							f.cancel(true);
						}
						throw new PersistenceException("Exception occured while query from sharding DB: " + k);
					}
					return p;

				}

			};

			Future<Pagination<T>> future = service.submit(task);
			futureMap.put(k,future);

		}

		/*
		 * reduce script
		 */
		Set<Entry<String,Future<Pagination<T>>>> entrySet = futureMap.entrySet();
		for (Entry<String,Future<Pagination<T>>> entry : entrySet) {
			String k = entry.getKey();
			Future<Pagination<T>> future = entry.getValue();
			try {
				Pagination<T> p = future.get(2, TimeUnit.MINUTES);
				resultMap.put(k, p);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				for (Future<Pagination<T>> f : futureMap.values()){
					f.cancel(true);
				}
				throw new PersistenceException("DB is busy, while query from sharding DB: " + k);
			}
		}
		
		long totalRows = 0;
		List<T> resultList = new ArrayList<>();
		for (Pagination<T> p : resultMap.values()){
			resultList.addAll(p.getList());
			totalRows += p.getTotalRows();
		}
		
		String orderBy = null;
		String sc = null;
		if (pagination instanceof PaginationSorted){
			orderBy = criteria.getOrderByList().get(0);
			sc = criteria.getSc();
		}else{
			Parsed parsed = Parser.get(criteria.getClz());
			orderBy = parsed.getKey(Persistence.KEY_ONE);
			sc = "DESC";
		}
		
		Class clz = criteria.getClz();
		BeanUtilX.sort(clz, resultList, orderBy, sc.toUpperCase().equals("ASC"));
		
		resultList = resultList.subList(rows * (page-1), rows * page);
		
		pagination.setTotalRows(totalRows);
		pagination.setList(resultList);
		
		return pagination;
	}
	
	private Pagination<Map<String, Object>> list(Fetch criterionJoinable, Pagination<Map<String, Object>> pagination, String key) {
		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}

		Pagination<Map<String, Object>> p = DaoImpl.getInstance().list(criterionJoinable, pagination, conn);
		return p;
	}

	@Override
	public Pagination<Map<String, Object>> list(Fetch criteria, Pagination<Map<String, Object>> pagination) {

		String key = getKey(criteria);

		if (StringUtil.isNotNull(key)) {
			return list(criteria, pagination, key);
		}

		String policy = Configs.getString("x7.db.sharding.policy");
		String[] keyArr = ShardingPolicy.get(policy).getSuffixArr();

		Map<String, Pagination<Map<String, Object>>> resultMap = new HashMap<>();

		/*
		 * map script
		 */
		final int page = pagination.getPage();
		final int rows = pagination.getRows();
		pagination.setRows(rows * page);
		pagination.setPage(1);
		Map<String,Future<Pagination<Map<String, Object>>>> futureMap = new HashMap<>();
		

		for (String k : keyArr) {

			Callable<Pagination<Map<String, Object>>> task = new Callable<Pagination<Map<String, Object>>>() {

				@Override
				public Pagination<Map<String, Object>> call() throws Exception {

					Pagination<Map<String, Object>> p = new Pagination<Map<String, Object>>();
					p.setPage(1);
					p.setRows(page * rows);
					try {
						p = list(criteria, p, k);
					} catch (Exception e) {
						for (Future<Pagination<Map<String, Object>>> f : futureMap.values()){
							f.cancel(true);
						}
						throw new PersistenceException("Exception occured while query from sharding DB: " + k);
					}

					return p;

				}

			};

			Future<Pagination<Map<String, Object>>> future = service.submit(task);
			futureMap.put(k,future);

		}

		/*
		 * reduce script
		 */
		Set<Entry<String,Future<Pagination<Map<String, Object>>>>> entrySet = futureMap.entrySet();
		for (Entry<String,Future<Pagination<Map<String, Object>>>> entry : entrySet) {
			String k = entry.getKey();
			Future<Pagination<Map<String, Object>>> future = entry.getValue();
			try {
				Pagination<Map<String, Object>> p = future.get(2, TimeUnit.MINUTES);
				resultMap.put(k, p);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				for (Future<Pagination<Map<String, Object>>> f : futureMap.values()){
					f.cancel(true);
				}
				throw new PersistenceException("DB is busy, while query from sharding DB: " + k);
			}
		}
		
		long totalRows = 0;
		List<Map<String, Object>> resultList = new ArrayList<>();
		for (Pagination<Map<String, Object>> p : resultMap.values()){
			resultList.addAll(p.getList());
			totalRows += p.getTotalRows();
		}
		
		String orderBy = null;
		String sc = null;
		if (pagination instanceof PaginationSorted){
			orderBy = criteria.getOrderByList().get(0);
			sc = criteria.getSc();
		}else{
			Parsed parsed = Parser.get(criteria.getClz());
			orderBy = parsed.getKey(Persistence.KEY_ONE);
			sc = "DESC";
		}

		BeanUtilX.sort(resultList, orderBy, sc.toUpperCase().equals("ASC"));

		resultList = resultList.subList(rows * (page-1), rows * page);
		
		pagination.setTotalRows(totalRows);
		pagination.setList(resultList);
		
		return pagination;
	}
	
	private <T> long getCount(Object obj, String key){
		Connection conn = null;
		try {
			conn = getConnection(key, true);// FIXME true, need a policy
		} catch (SQLException e) {
			throw new RuntimeException("NO CONNECTION");
		}
		return DaoImpl.getInstance().getCount(obj, conn);
	}

	@Override
	public <T> long getCount(Object obj) {
		
		String key = getKey(obj);

		if (StringUtil.isNotNull(key)) {
			return getCount(obj, key);
		}

		String policy = Configs.getString("x7.db.sharding.policy");
		String[] keyArr = ShardingPolicy.get(policy).getSuffixArr();
		
		/*
		 * map script
		 */
		Map<String,Future<Long>> futureMap = new HashMap<>();

		for (String k : keyArr) {

			Callable<Long> task = new Callable<Long>() {

				@Override
				public Long call() throws Exception {

					Long count = 0L;
					try {
						count = getCount(obj, k);
					} catch (Exception e) {
						for (Future<Long> f : futureMap.values()){
							f.cancel(true);
						}
						throw new PersistenceException("Exception occured while query from sharding DB: " + k);
					}
					return count;

				}

			};

			Future<Long> future = service.submit(task);
			futureMap.put(k,future);

		}

		/*
		 * reduce script
		 */
		long totalCount = 0;
		Set<Entry<String,Future<Long>>> entrySet = futureMap.entrySet();
		for (Entry<String,Future<Long>> entry : entrySet) {
			String k = entry.getKey();
			Future<Long> future = entry.getValue();
			try {
				Long count = future.get(2, TimeUnit.MINUTES);
				totalCount += count;
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				for (Future<Long> f : futureMap.values()){
					f.cancel(true);
				}
				throw new PersistenceException("DB is busy, while query from sharding DB: " + k);
			}
		}

		return totalCount;
	}
	

}
