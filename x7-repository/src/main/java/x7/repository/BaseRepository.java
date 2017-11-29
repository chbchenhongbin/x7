package x7.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import x7.core.async.CasualWorker;
import x7.core.async.IAsyncTask;
import x7.core.bean.Criteria;
import x7.core.bean.IQuantity;
import x7.core.util.StringUtil;
import x7.core.web.Pagination;
import x7.repository.exception.PersistenceException;
import x7.repository.redis.JedisConnector_Persistence;

/**
 * 
 * 其他模块的Repository建议继承此类
 *
 */
@Repository
public abstract class BaseRepository<T> {

	public final static String ID_MAP_KEY = "ID_MAP_KEY";

	public Map<String, String> map = new HashMap<String, String>();

	private Class<T> clz;

	public BaseRepository() {
		parse();
	}
	
	private void parse(){
		
		Type genType = getClass().getGenericSuperclass();

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		
		this.clz = (Class)params[0];
		
		System.out.println("______BaseRepository, " + this.clz.getName());
	}

	protected Object preMapping(String methodName, Object... s) {

		boolean isOne = methodName.startsWith("get");

		String sql = map.get(methodName);
		if (StringUtil.isNullOrEmpty(sql)) {

			methodName = methodName.replace("list", "").replace("get", "").replace("find", "").replace("By", " where ");
			methodName = methodName.replace("And", " = ? and ").replace("Or", " = ? or ");

			methodName = methodName.toLowerCase();

			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(methodName).append(" = ?");

			sql = sb.toString();

			map.put(methodName, sql);

		}
		List<Object> conditionList = Arrays.asList(s);
		List<T> list = (List<T>) ManuRepository.list(clz, sql, conditionList);

		if (isOne) {
			if (list.isEmpty())
				return null;
			return list.get(0);
		}

		return list;
	}


	public void set(byte[] key, byte[] value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	public byte[] get(byte[] key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	public void set(String key, String value, int seconds) {
		JedisConnector_Persistence.getInstance().set(key, value, seconds);
	}

	public void set(String key, String value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	public String get(String key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	public long createId(Object obj) {
		
		final String name = obj.getClass().getName();
		final long id = JedisConnector_Persistence.getInstance().hincrBy(ID_MAP_KEY, name, 1);

		if (id == 0) {
			throw new PersistenceException("UNEXPECTED EXCEPTION WHILE CREATING ID");
		}

		CasualWorker.accept(new IAsyncTask(){

			@Override
			public void execute() throws Exception {
				IdGenerator generator = new IdGenerator();
				generator.setClzName(name);
				List<IdGenerator> list = Repositories.getInstance().list(generator);
				if (list.isEmpty()){
					generator.setMaxId(id);
					Repositories.getInstance().create(generator);
				}else{
					generator.setMaxId(id);
					Repositories.getInstance().refresh(generator);
				}
			}
			
		});

		return id;
	}

	/**
	 * 以持久化后的数量为准<br>
	 * 适合高速更新数量的需求<br>
	 * 执行结束后，需要更新相关的对象到DB, 可以加入队列，合并数量，调用异步更新接口，此 API未实现复杂逻辑<br>
	 * 
	 * @param obj
	 * @param reduced
	 * @return currentQuantity
	 */
	public int reduce(IQuantity obj, int reduced) {
		if (reduced < 0) {
			throw new RuntimeException("reduced quantity must > 0");
		}

		String mapKey = obj.getClass().getName();

		int quantity = (int) JedisConnector_Persistence.getInstance().hincrBy(mapKey, obj.getKey(), -reduced);

		obj.setQuantity(quantity);

		return quantity;
	}
	
	
	public boolean createBatch(List<T> objList){
		return Repositories.getInstance().createBatch(objList);
	}

	public long create(T obj) {
		/*
		 * FIXME 日志
		 */
		System.out.println("BaesRepository.create: " + obj);

		long id = Repositories.getInstance().create(obj);

		return id;

	}

	/**
	 * 直接更新，不需要查出对象再更新<BR>
	 * 对于可能重置为0的数字，或Boolean类型，不能使用JAVA基本类型
	 * 
	 * @param obj
	 */
	public boolean refresh(T obj) {
		return Repositories.getInstance().refresh(obj);
	}

	/**
	 * 带条件更新(默认需要ID, 不需要增加id)<br>
	 * 不支持无ID更新<br>
	 * 
	 * @param obj
	 * @param conditionMap
	 * @return true | false
	 */
	public boolean refresh(T obj, Map<String, Object> conditionMap) {
		return Repositories.getInstance().refresh(obj, conditionMap);
	}

	public void refreshAsync(T obj) {
		Repositories.getInstance().refreshAsync(obj);
	}

	/**
	 * 删除数据<br>
	 * 为了删除索引，必须先从数据库里查出对象，再删<br>
	 * 如果有辅助索引类(IIndexTyped.java), 每次仅仅支持删除条记录，自动删除索引<br>
	 * 
	 * @param obj
	 */
	public void remove(T obj) {
		Repositories.getInstance().remove(obj);
	}


	/**
	 * 
	 * 根据主键查一条记录，(findByPK)
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public T get(long idOne) {
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().get(clz, idOne);

	}

	/**
	 * 
	 * 根据第一主键和第二主键查条记录，(findByPK)
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public T get(long idOne, long idTwo) {
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().get(clz, idOne, idTwo);
	}

	/**
	 * LOAD
	 * 
	 * @param clz
	 * @return
	 */
	public List<T> list() {

		return Repositories.getInstance().list(clz);
	}

	/**
	 * 根据对象查询
	 * 
	 * @param conditionObj
	 * 
	 */
	public List<T> list(T conditionObj) {

		if (conditionObj instanceof Criteria.Fetch) {
			throw new RuntimeException(
					"Exception supported, no pagination not to invoke Repositories.getInstance().list(criteriaJoinalbe);");
		}
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(conditionObj);
	}


	/**
	 * 手动拼接SQL查询 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	public Pagination<Map<String, Object>> list(Criteria.Fetch criteria, Pagination<Map<String, Object>> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}

	/**
	 * 获取ID
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public long getMaxId(long idOne) {

		return Repositories.getInstance().getMaxId(clz, idOne);
	}

	/**
	 * 获取�?大ID
	 * 
	 * @param clz
	 * 
	 */
	public long getMaxId() {
		return Repositories.getInstance().getMaxId(clz);
	}

	public long getMaxId(T conditionObj) {
		return Repositories.getInstance().getMaxId(conditionObj);
	}

	/**
	 * count
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public long getCount(long idOne) {

		return Repositories.getInstance().getCount(clz, idOne);
	}

	public long getCount(T conditonObj) {
		return Repositories.getInstance().getCount(conditonObj);
	}

	public T getOne(T conditionObj, String orderBy, String sc) {

		return Repositories.getInstance().getOne(conditionObj, orderBy, sc);
	}

	public T getOne(T conditionObj) {

		T t = Repositories.getInstance().getOne(conditionObj);
		return t;
	}

	/**
	 * 特殊的更新时间方法，不标记缓存时间<br>
	 * 后台查询时需要在进入查询页面时，调用refreshCache<br>
	 * 主要是防止登录等操作，而导致了用户的缓存失效<br>
	 * 建议后台系统走另外一套缓存<br>
	 * 
	 * @param obj
	 */
	public void refreshTime(T obj) {
		Repositories.getInstance().refreshTime(obj);
	}

	/**
	 * 配合refreshTime使用，后台按更新时间查询列表之前调用<br>
	 * 
	 * @param clz
	 */
	public void refreshCache() {
		Repositories.getInstance().refreshCache(clz);
	}

//	/**
//	 * 按索引查询 分页<BR>
//	 * 注意： ORDER BY 只能按主键
//	 */
//	public void list(IIndexTyped index, Pagination<T> pagination) {
//
//		BeanUtilX.filter(index);
//
//		Class<T> beanClz = BeanUtilX.getBeanClass(index);
//
//		Pagination<IIndexTyped> idxPagination = null;
//		String sc = PaginationSorted.DESC;
//		if (pagination instanceof PaginationSorted) {
//			PaginationSorted<T> ps = (PaginationSorted<T>) idxPagination;
//			sc = ps.getSc();
//
//			if (!ps.getOrderBy().equals("id")) {
//				throw new RuntimeException("BIG_INDEX EXCEPTION: BIG TABLE, OR SHARDING ORDER BY ID only, order by = "
//						+ ps.getOrderBy() + ", SPHINX, ELASTIC SEARCH SUGGESTED");
//			}
//		}
//
//		idxPagination = new PaginationSorted<IIndexTyped>(pagination.getPage(), pagination.getRows(), "id", sc);
//
//		Repositories.getInstance().list(index, idxPagination);
//
//		long totalRows = idxPagination.getTotalRows();
//		int page = idxPagination.getPage();
//		int rows = idxPagination.getRows();
//
//		List<IIndexTyped> idxList = idxPagination.getList();
//
//		List<Object> idList = new ArrayList<Object>();
//
//		for (IIndexTyped idx : idxList) {
//			if (idx.getId() != 0) {
//				idList.add(idx.getId());
//			}
//		}
//
//		List<T> list = null;
//		if (idList.isEmpty()) {
//			list = new ArrayList<>();
//		} else {
//			list = Repositories.getInstance().in(beanClz, idList);
//		}
//
//		pagination.setTotalRows(totalRows);
//		pagination.setPage(page);
//		pagination.setRows(rows);
//		pagination.setList(list);
//	}

	/**
	 * 按索引查询 不分页，适合少量数据查询<BR>
	 */
//	public List<T> list(IIndexTyped index) {
//
//		BeanUtilX.filter(index);
//
//		Class<T> beanClz = BeanUtilX.getBeanClass(index);
//
//		List<IIndexTyped> idxList = Repositories.getInstance().list(index);
//
//		List<Object> idList = new ArrayList<Object>();
//
//		for (IIndexTyped idx : idxList) {
//
//			if (idx.getId() != 0) {
//				idList.add(idx.getId());
//			}
//		}
//
//		if (idList.isEmpty())
//			return new ArrayList<T>();
//
//		List<T> list = Repositories.getInstance().in(beanClz, idList);
//
//		return list;
//	}

	/**
	 * 按索引查询 不分页，适合少量数据查询<BR>
	 */
//	public T get(IIndexTyped index) {
//
//		BeanUtilX.filter(index);
//
//		Class<T> beanClz = BeanUtilX.getBeanClass(index);
//
//		List<IIndexTyped> idxList = Repositories.getInstance().list(index);
//
//		List<Object> idList = new ArrayList<Object>();
//
//		for (IIndexTyped idx : idxList) {
//			// T t = get(beanClz, idx.getId()); // 按主键查询(唯一主键，BIG TABLE,
//			// SHARDING)
//			// if (t != null)
//			// list.add(t);
//
//			if (idx.getId() != 0) {
//				idList.add(idx.getId());
//			}
//		}
//
//		List<T> list = Repositories.getInstance().in(beanClz, idList);
//
//		if (list.isEmpty())
//			return null;
//
//		return list.get(0);
//	}

	public Object getSum(T conditionObj, String sumProperty) {
		return Repositories.getInstance().getSum(conditionObj, sumProperty);
	}

	public Object getSum(T conditionObj, String sumProperty, Criteria criteria) {
		return Repositories.getInstance().getSum(sumProperty, criteria);
	}

	public Object getCount(String sumProperty, Criteria criteria) {
		return Repositories.getInstance().getCount(sumProperty, criteria);
	}

	public List<T> in(List<? extends Object> inList) {
		if (inList.isEmpty())
			return new ArrayList<T>();
		Set<Object> set = new HashSet<Object>();
		for (Object obj : inList){
			set.add(obj);
		}
		
		List<Object> list = new ArrayList<Object>();
		for (Object obj : set){
			list.add(obj);
		}
		
		return Repositories.getInstance().in(clz, list);
	}

	public List<T> in(String inProperty, List<? extends Object> inList) {
		if (inList.isEmpty())
			return new ArrayList<T>();
		
		Set<Object> set = new HashSet<Object>();
		for (Object obj : inList){
			set.add(obj);
		}
		
		List<Object> list = new ArrayList<Object>();
		for (Object obj : set){
			list.add(obj);
		}
		
		return Repositories.getInstance().in(clz, inProperty, list);
	}

	/**
	 * 手动拼接SQL查询 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	public Pagination<T> list(Criteria criteria, Pagination<T> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}

}
