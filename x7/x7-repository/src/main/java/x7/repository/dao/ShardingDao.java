package x7.repository.dao;

import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.web.Pagination;



/**
 * 
 * 分库分表 DAO<BR>
 * 没提供所以库，所有表的遍历，和复杂条件查询<br>
 * 
 * @author sim
 *
 * @param <T>
 */
public interface ShardingDao {

	long create(Object obj);

	/**
	 * 一般要查出对象再更新
	 * @param obj
	 */
	boolean refresh(Object obj);
	
	boolean refresh(Object obj, Map<String, Object> conditonMap);

	boolean remove(Object obj);

	boolean execute(Object obj, String sql);

	<T> T get(Class<T> clz, long idOne, long idTwo);
	<T> T get(Class<T> clz, long idOne);
	<T> T getOne(T conditionObj);
	<T> long getCount(Object obj);
	<T> long getCount(Class<T> clz, long idOne);
	
	<T> Pagination<T> list(Criteria criteria, Pagination<T> pagination);
	
	Pagination<Map<String,Object>> list(Criteria.Fetch criterionJoinable, Pagination<Map<String,Object>> pagination);

}