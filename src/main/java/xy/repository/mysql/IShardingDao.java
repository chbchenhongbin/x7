package xy.repository.mysql;

import java.util.List;
import java.util.Map;

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
public interface IShardingDao {

	long create(Object obj);

	/**
	 * 一般要查出对象再更新
	 * @param obj
	 */
	void refresh(Object obj);
	
	boolean refresh(Object obj, Map<String, Object> conditonMap);

	void remove(Object obj);

	<T> List<T> list(Class<T> clz, long idOne);
	
	/**
	 * 仅仅根据第一主键查询的结果分页<br>
	 * 通常配合缓存使用<br>
	 * @param clz
	 * @param idOne
	 * @param pagination
	 * 
	 */
	<T> Pagination<T> list(Class<T> clz, long idOne, Pagination<T> pagination);
	
	/**
	 * 适合单主键
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);

	<T> T get(Class<T> clz, long idOne, long idTwo);

	/**
	 * 默认路由属性, 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * 
	 */
	<T> List<T> list(Object conditionObj);
	
	<T> T getOne(T conditionObj, String orderBy, String sc);

	/**
	 * 仅仅根据第一主键查询最大ID值<br
	 * @param clz
	 * @param idOne
	 * 
	 */
	<T> long getMaxId(Class<T> clz, long idOne);
	
	long getMaxId(Object conditionObj);
	/**
	 * 仅仅根据第一主键查询总数<br
	 * @param clz
	 * @param idOne
	 * 
	 */
	<T> long getCount(Class<T> clz, long idOne);
	
	@Deprecated
	boolean execute(Object obj, String sql);

}