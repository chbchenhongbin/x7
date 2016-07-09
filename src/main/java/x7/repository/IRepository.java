package x7.repository;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaJoinable;
import x7.core.web.Pagination;



/**
 * 
 * 持久化<br>
 * 未提供业务层事务管理，建议用其他方式解决事务，例如乐观所, 各种最终一致<br>
 * 适合电商领域，不适合企业级开发<br>
 * @author Sim
 *
 */
public interface IRepository {
	
	/**
	 * 更新缓存
	 * @param clz
	 */
	<T> void refreshCache(Class<T> clz);

	/**
	 * 创建
	 * @param obj
	 * @return
	 */
	long create(Object obj);

	/**
	 * 更新, 支持局部更新
	 * @param obj
	 */
	void refresh(Object obj);
	
	/**
	 * 带条件支持局部更新
	 * @param obj
	 * @param conditionMap
	 * @return
	 */
	boolean refresh(Object obj, Map<String, Object> conditionMap);

	/**
	 * 删除
	 * @param obj
	 */
	void remove(Object obj);

	/**
	 * 根据主键查询
	 * 
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> List<T> list(Class<T> clz, long idOne);
	
	/**
	 * 根据主键查出单条
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);

	/**
	 * 根据联合主键查出单条
	 * @param clz
	 * @param idOne
	 * @param idTwo
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne, long idTwo);

	/**
	 * 仅仅根据第一主键查询的结果分页<br>
	 * 通常配合缓存使用<br>
	 * 
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
	 * 根据对象查一条记录
	 * @param conditionObj
	 * @param orderBy
	 * @param sc "DESC", "ASC"
	 * @return
	 */
	<T> T getOne(T conditionObj, String orderBy, String sc);

	/**
	 * 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * 
	 */
	<T> Pagination<T> list(Object conditionObj, Pagination<T> pagination);

	/**
	 * 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * @param extConditionMap
	 *            可以拼接的条件
	 * 
	 */
	<T> Pagination<T> list(Criteria criteria, Pagination<T> pagination);

	/**
	 * SQL语句查询
	 * 
	 * @param clz
	 * @param sql
	 * 
	 */
	@Deprecated
	<T> List<T> list(Class<T> clz, String sql, List<Object> conditionList);
	
	/**
	 * loadAll
	 * @param clz
	 * 
	 */
	<T> List<T> list(Class<T> clz);
	
	/**
	 * 这个仅仅支持keyOne in 查询, 不支持联合主键bean
	 * @param clz
	 * @param inList
	 */
	<T> List<T> in(Class<T> clz, List<? extends Object> inList);
	
	/**
	 * 支持单一的指定property的in查询, 包括主键
	 * @param clz
	 * @param inProperty
	 * @param inList
	 */
	<T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList);
	/**
	 * 联合主键，查第二主键最大值
	 * @param clz
	 * @param idOne
	 * 
	 */
	<T> long getMaxId(Class<T> clz, long idOne);
	/**
	 * 单主键，最大值
	 * @param clz
	 * 
	 */
	<T> long getMaxId(Class<T> clz);
	
	/**
	 * 最大值
	 * @param clz
	 * 
	 */
	long getMaxId(Object conditionObj);
	
	/**
	 * 联合主键，查总条数
	 * @param clz
	 * @param idOne
	 * 
	 */
	<T> long getCount(Class<T> clz, long idOne);
	
	/**
	 * 查总条数
	 * @param obj
	 * 
	 */
	long getCount(Object obj);
	
	/**
	 * 查累计数
	 * @param conditionObj
	 * @param sumProperty
	 * 
	 */
	Object getSum(Object conditionObj, String sumProperty);
	
	/**
	 * 条件查询累计
	 * @param conditionObj
	 * @param sumProperty
	 * @param criterion
	 * 
	 */
	Object getSum(Object conditionObj, String sumProperty, Criteria criterion);
	/**
	 * 条件查询计数
	 * @param conditionObj
	 * @param countProperty
	 * @param criteria
	 * 
	 */
	Object getCount(Object conditionObj, String countProperty, Criteria criteria);
	
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