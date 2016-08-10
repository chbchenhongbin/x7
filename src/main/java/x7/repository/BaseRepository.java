package x7.repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;


import x7.core.bean.Criteria;
import x7.core.bean.CriteriaJoinable;
import x7.core.bean.IQuantity;
import x7.core.bean.IdGenerator;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.IIndexTyped;
import x7.core.repository.Persistence;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.web.Pagination;
import x7.core.web.PaginationSorted;
import x7.repository.exception.PersistenceException;
import x7.repository.mysql.bean.IKeyType;
import x7.repository.redis.JedisConnector_Persistence;



/**
 * 
 * 其他模块的Repository建议继承此类
 *
 */
@Repository
public class BaseRepository {
	
	public final static String ID_MAP_KEY = "ID_MAP_KEY";

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

	public long createId(IdGenerator obj) {
		long id = JedisConnector_Persistence.getInstance().hincrBy(ID_MAP_KEY, obj.getClass().getName(),
				1);

		if (id == 0) {
			throw new PersistenceException("UNEXPECTED EXCEPTION WHILE CREATING ID");
		}

		obj.setId(id);
		Repositories.getInstance().createAsync(obj);

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
		
		int quantity = (int) JedisConnector_Persistence.getInstance().hincrBy(mapKey,
				obj.getKey(), -reduced);

		obj.setQuantity(quantity);

		return quantity;
	}
	
	public long create(Object obj) {
		/*
		 * FIXME 日志
		 */
		System.out.println("BaesRepository.create: " + obj);

		long id = Repositories.getInstance().create(obj);

		/*
		 * 创建索引
		 */
		Class clz = obj.getClass();

		// String clzName = obj.getClass().getName();
		Class<IIndexTyped> clzIndex = BeanUtilX.getIndexClass(clz);
		if (clzIndex == null)
			return id;

		Parsed parsed = Parser.get(clz);

		IIndexTyped objIndex = null;
		try {
			objIndex = (IIndexTyped) clzIndex.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (objIndex == null)
			return id;

		String keyOne = parsed.getKey(Persistence.KEY_ONE);

		if (objIndex.getTypeMap().containsKey(IKeyType.EXCLUSIVE)) {
			String keyType = null;
			Object key = 0;
			long idt = 0;
			Field field = null;
			try {
				field = clz.getDeclaredField(IKeyType.class.getSimpleName().substring(1));//
				field.setAccessible(true);
				keyType = field.get(obj).toString();

				String property = objIndex.getTypeMap().get(IKeyType.EXCLUSIVE);
				String getter = BeanUtil.getGetter(property);
				Method method = clz.getDeclaredMethod(getter);
				key = method.invoke(obj);
				// field = clz.getDeclaredField(property);
				// field.setAccessible(true);
				// key = field.getInt(obj);

				idt = id;
				if (idt == 0) {
					field = clz.getDeclaredField(keyOne);
					field.setAccessible(true);
					idt = field.getLong(obj);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (keyType == null || key == null || "0".equals(key.toString()) || idt == 0)
				return id;
			
			IIndexTyped temp = null;
			try {
				temp = (IIndexTyped) clzIndex.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			temp.setKeyOne(IIndexTyped.getKeyOne(keyType, key));
			temp.setType(keyType);
			temp.setId(idt);

			Repositories.getInstance().create(temp);

			return id;
		}

		for (String type : objIndex.getTypeMap().keySet()) {
			String property = objIndex.getTypeMap().get(type);

			Field field = null;
			Object key = 0;
			long idt = 0;
			try {
				// field = clz.getDeclaredField(property);
				// field.setAccessible(true);
				// key = field.getInt(obj);
				String getter = BeanUtil.getGetter(property);
				Method method = clz.getDeclaredMethod(getter);
				key = method.invoke(obj);

				// field = clz.getDeclaredField(keyOne);
				// field.setAccessible(true);
				// idt = field.getInt(obj);

				idt = id;
				if (idt == 0) {
					field = clz.getDeclaredField(keyOne);
					field.setAccessible(true);
					idt = field.getLong(obj);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (key == null || "0".equals(key.toString()) || idt == 0)
				continue;
			
			IIndexTyped temp = null;
			try {
				temp = (IIndexTyped) clzIndex.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			temp.setKeyOne(IIndexTyped.getKeyOne(type, key));
			temp.setType(type);
			temp.setId(idt);


			Repositories.getInstance().create(temp);
		}

		return id;

	}

	/**
	 * 直接更新，不需要查出对象再更新<BR>
	 * 对于可能重置为0的数字，或Boolean类型，不能使用JAVA基本类型
	 * 
	 * @param obj
	 */
	public void refresh(Object obj) {
		Repositories.getInstance().refresh(obj);
	}
	
	/**
	 * 带条件更新(默认需要ID, 不需要增加id)<br>
	 * 不支持无ID更新<br>
	 * @param obj
	 * @param conditionMap
	 * @return true | false
	 */
	public boolean refresh(Object obj, Map<String,Object> conditionMap) {
		return Repositories.getInstance().refresh(obj, conditionMap);
	}

	public void refreshAsync(Object obj) {
		Repositories.getInstance().refreshAsync(obj);
	}

	/**
	 * 删除数据<br>
	 * 为了删除索引，必须先从数据库里查出对象，再删�?<br>
	 * 如果有辅助索引类(IIndexTyped.java), 每次仅仅支持删除�?条记录，自动删除索引<br>
	 * 
	 * @param obj
	 */
	public void remove(Object obj) {
		Repositories.getInstance().remove(obj);

		/*
		 * 删除索引
		 */
		Class clz = obj.getClass();

		// String clzName = obj.getClass().getName();
		Class clzIndex = BeanUtilX.getIndexClass(clz);
		if (clzIndex == null)
			return;

		Parsed parsed = Parser.get(clz);

		IIndexTyped objIndex = null;
		try {
			objIndex = (IIndexTyped) clzIndex.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (objIndex == null)
			return;

		String keyOne = parsed.getKey(Persistence.KEY_ONE);

		if (objIndex.getTypeMap().containsKey(IKeyType.EXCLUSIVE)) {
			String keyType = null;
			long key = 0;
			long idt = 0;
			Field field = null;
			try {
				field = clz.getDeclaredField(IKeyType.class.getSimpleName().substring(1));
				field.setAccessible(true);
				keyType = field.get(obj).toString();

				String property = objIndex.getTypeMap().get(IKeyType.EXCLUSIVE);
				field = clz.getDeclaredField(property);
				field.setAccessible(true);
				key = field.getLong(obj);

				field = clz.getDeclaredField(keyOne);
				field.setAccessible(true);
				idt = field.getLong(obj);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (keyType == null || key == 0 || idt == 0)
				return;

			objIndex.setKeyOne(IIndexTyped.getKeyOne(keyType, key));
			objIndex.setId(idt);

			Repositories.getInstance().remove(objIndex);

			return;
		}

		for (String type : objIndex.getTypeMap().keySet()) {
			String property = objIndex.getTypeMap().get(type);

			Field field = null;
			long key = 0;
			long idt = 0;
			try {
				field = clz.getDeclaredField(property);
				field.setAccessible(true);
				key = field.getLong(obj);

				field = clz.getDeclaredField(keyOne);
				field.setAccessible(true);
				idt = field.getLong(obj);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (key == 0)
				continue;

			objIndex.setKeyOne(IIndexTyped.getKeyOne(type, key));
			objIndex.setId(idt);

			Repositories.getInstance().remove(objIndex);
		}

	}

	public <T> List<T> list(Class<T> clz, long idOne) {
		/*
		 * FIXME 日志
		 */

		Class clzIndex = BeanUtilX.getIndexClass(clz);
		if (clzIndex != null) {
			throw new PersistenceException("with IIndexTyped, should list(index)");
		}

		List<T> list = Repositories.getInstance().list(clz, idOne);

		return list;
	}

	/**
	 * 
	 * 根据主键查一条记录，(findByPK)
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public <T> T get(Class<T> clz, long idOne) {
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().get(clz, idOne);

	}

	/**
	 * 
	 * 根据第一主键和第二主键查�?条记录，(findByPK)
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public <T> T get(Class<T> clz, long idOne, long idTwo) {
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
	public <T> List<T> list(Class<T> clz) {

		return Repositories.getInstance().list(clz);
	}

	/**
	 * 根据对象查询
	 * 
	 * @param conditionObj
	 * 
	 */
	public <T> List<T> list(Object conditionObj) {
		
		if (conditionObj instanceof CriteriaJoinable){
			throw new RuntimeException("Exception supported, to invoke Repositories.getInstance().list(criteriaJoinalbe);");
		}
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(conditionObj);
	}

	/**
	 * 主键查询 分页
	 */
	public <T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination) {

		Class clzIndex = BeanUtilX.getIndexClass(clz);
		if (clzIndex != null) {
			throw new PersistenceException("with IIndexTyped, should list(index, pagination)");
		}
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(clz, idOne, pagination);
	}

	/**
	 * 根据对象查找，分页?
	 * 
	 * @param conditionObj
	 * @param pagination
	 * 
	 */
	public <T> Pagination<T> list(Object conditionObj, Pagination<T> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(conditionObj, pagination);
	}

	/**
	 * 手动拼接SQL查询�? 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	public Pagination<Map<String, Object>> list(CriteriaJoinable criteria, Pagination<Map<String, Object>> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}
	

	/**
	 * 获取�?大ID
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public <T> long getMaxId(Class<T> clz, long idOne) {

		return Repositories.getInstance().getMaxId(clz, idOne);
	}

	/**
	 * 获取�?大ID
	 * 
	 * @param clz
	 * 
	 */
	public <T> long getMaxId(Class<T> clz) {
		return Repositories.getInstance().getMaxId(clz);
	}

	public long getMaxId(Object conditionObj) {
		return Repositories.getInstance().getMaxId(conditionObj);
	}

	/**
	 * count
	 * 
	 * @param clz
	 * @param idOne
	 * 
	 */
	public <T> long getCount(Class<T> clz, long idOne) {

		return Repositories.getInstance().getCount(clz, idOne);
	}
	
	public long getCount(Object conditonObj) {
		return Repositories.getInstance().getCount(conditonObj);
	}

	public <T> T getOne(T conditionObj, String orderBy, String sc) {

		return Repositories.getInstance().getOne(conditionObj, orderBy, sc);
	}

	public <T> T getOne(Object conditionObj) {

		List<T> list = Repositories.getInstance().list(conditionObj);
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	/**
	 * 特殊的更新时间方法，不标记缓存时间<br>
	 * 后台查询时需要在进入查询页面时，调用refreshCache<br>
	 * 主要是防止登录等操作，而导致了用户的缓存失效<br>
	 * 建议后台系统走另外一套缓存<br>
	 * 
	 * @param obj
	 */
	public void refreshTime(Object obj) {
		Repositories.getInstance().refreshTime(obj);
	}

	/**
	 * 配合refreshTime使用，后台按更新时间查询列表之前调用<br>
	 * 
	 * @param clz
	 */
	public void refreshCache(Class clz) {
		Repositories.getInstance().refreshCache(clz);
	}

	/**
	 * 按索引查询 分页<BR>
	 * 注意： ORDER BY 只能按主键
	 */
	public <T> void list(IIndexTyped index, Pagination<T> pagination) {

		BeanUtilX.filter(index);

		Class<T> beanClz = BeanUtilX.getBeanClass(index);

		Pagination idxPagination = null;
		String sc = PaginationSorted.DESC;
		if (pagination instanceof PaginationSorted) {
			PaginationSorted ps = (PaginationSorted) pagination;
			sc = ps.getSc();

			if (!ps.getOrderBy().equals("id")) {
				throw new RuntimeException("BIG_INDEX EXCEPTION: BIG TABLE, OR SHARDING ORDER BY ID only, order by = "
						+ ps.getOrderBy() + ", SPHINX, ELASTIC SEARCH SUGGESTED");
			}
		}

		idxPagination = new PaginationSorted(pagination.getPage(), pagination.getRows(), "id", sc);

		Repositories.getInstance().list(index, idxPagination);

		long totalRows = idxPagination.getTotalRows();
		int page = idxPagination.getPage();
		int rows = idxPagination.getRows();

		List<IIndexTyped> idxList = idxPagination.getList();

		List<Object> idList = new ArrayList<Object>();
		
		for (IIndexTyped idx : idxList) {
			if (idx.getId() != 0){
				idList.add(idx.getId());
			}
		}
		
		List<T> list = null;
		if (idList.isEmpty()){
			list = new ArrayList();
		}else{
			list = in(beanClz, idList);
		}

		pagination.setTotalRows(totalRows);
		pagination.setPage(page);
		pagination.setRows(rows);
		pagination.setList(list);
	}

	/**
	 * 按索引查询 不分页，适合少量数据查询<BR>
	 */
	public <T> List<T> list(IIndexTyped index) {

		BeanUtilX.filter(index);

		Class<T> beanClz = BeanUtilX.getBeanClass(index);

		List<IIndexTyped> idxList = Repositories.getInstance().list(index);
		

		List<Object> idList = new ArrayList<Object>();
		
		for (IIndexTyped idx : idxList) {
//			T t = get(beanClz, idx.getId()); // 按主键查询(唯一主键，BIG TABLE, SHARDING)
//			if (t != null)
//				list.add(t);
			
			if (idx.getId() != 0){
				idList.add(idx.getId());
			}
		}
		
		if (idList.isEmpty())
			return new ArrayList();
		
		List<T> list = in(beanClz, idList);
		
		return list;
	}

	/**
	 * 按索引查询 不分页，适合少量数据查询<BR>
	 */
	public <T> T get(IIndexTyped index) {

		BeanUtilX.filter(index);

		Class<T> beanClz = BeanUtilX.getBeanClass(index);

		List<IIndexTyped> idxList = Repositories.getInstance().list(index);

		List<Object> idList = new ArrayList<Object>();
		
		for (IIndexTyped idx : idxList) {
//			T t = get(beanClz, idx.getId()); // 按主键查询(唯一主键，BIG TABLE, SHARDING)
//			if (t != null)
//				list.add(t);
			
			if (idx.getId() != 0){
				idList.add(idx.getId());
			}
		}
		
		List<T> list = in(beanClz, idList);

		if (list.isEmpty())
			return null;

		return list.get(0);
	}

	public Object getSum(Object conditionObj, String sumProperty) {
		return Repositories.getInstance().getSum(conditionObj, sumProperty);
	}
	
	public Object getSum(Object conditionObj, String sumProperty, Criteria criteria) {
		return Repositories.getInstance().getSum(conditionObj, sumProperty, criteria);
	}
    
	public Object getCount(Object conditionObj, String sumProperty, Criteria criteria){
		return Repositories.getInstance().getCount(conditionObj, sumProperty, criteria);
	}
	
	public <T> List<T> in (Class<T> clz, List<Object> inList){
		if (inList.isEmpty())
			return new ArrayList<T>();
		return Repositories.getInstance().in(clz, inList);
	}
	
	public <T> List<T> in (Class<T> clz, String inProperty, List<Object> inList){
		if (inList.isEmpty())
			return new ArrayList<T>();
		return Repositories.getInstance().in(clz, inProperty, inList);
	}
	
	/**
	 * 手动拼接SQL查询�? 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	public <T> Pagination<T> list(Criteria criteria, Pagination<T> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}
	
}
