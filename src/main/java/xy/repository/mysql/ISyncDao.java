package xy.repository.mysql;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaJoinable;
import x7.core.web.Pagination;


/**
 * 
 * @author sim
 *
 */
public interface ISyncDao {

	long create(Object obj);

	
	/**
	 * 直接更新，不需要查出对象再更新<BR>
	 * 对于可能重置为0的数字，或Boolean类型，不能使用JAVA基本类型
	 * @param obj
	 */
	void refresh(Object obj);
	
	boolean refresh(Object obj, Map<String,Object> conditionMap);

	void remove(Object obj);
	
	/**
	 * 适合单主键
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);

	/**
	 * 适合联合主键
	 * @param clz
	 * @param idOne
	 * 
	 */
	<T> List<T> list(Class<T> clz, long idOne);

	<T> T get(Class<T> clz, long idOne, long idTwo);

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
	 * 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * 
	 */
	<T> List<T> list(Object conditionObj);
	
	/**
	 * 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * 
	 */
	<T> Pagination<T> list(Object conditionObj, Pagination<T> pagination);

	/**
	 * 
	 * @param criterion
	 * @param pagination
	 * 
	 */
	Pagination<Map<String,Object>> list(Criteria criterion, Pagination<Map<String,Object>> pagination);
	/**
	 * SQL语句查询
	 * 
	 * @param clz
	 * @param sql
	 * 
	 */
	<T> List<T> list(Class<T> clz, String sql,
			List<Object> conditionList);

	<T> List<T> list(Class<T> clz);
	
	<T> T getOne(T conditionObj, String orderBy, String sc);

	<T> long getMaxId(Class<T> clz, long idOne);

	<T> long getMaxId(Class<T> clz);
	
	long getMaxId(Object obj);

	<T> long getCount(Class<T> clz, long idOne);
	
	long getCount(Object obj);
	
	@Deprecated
	boolean execute(Object obj, String sql);
	
	Object getSum(Object conditionObj, String sumProperty);
	
	Object getSum(Object conditionObj, String sumProperty, Criteria criteria);
	//20160122 add by cl
	Object getCount(Object conditionObj, String countProperty, Criteria criteria);
	
	<T> List<T> in(Class<T> clz, List<? extends Object> inList);
	
	<T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList);
	
	/**
	 * 连表查询，标准化拼接
	 * 尽量避免在互联网业务系统中使用<br>
	 * 不支持缓存<br>
	 * @param criterionJoinable
	 * @param pagination
	 * 
	 */
	Pagination<Map<String,Object>> list(CriteriaJoinable criterionJoinable, Pagination<Map<String,Object>> pagination);
}