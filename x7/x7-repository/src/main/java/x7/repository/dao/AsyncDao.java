package x7.repository.dao;

import java.util.List;

public interface AsyncDao {

	/**
	 * 持久化对象<br>
	 * 限定：同样的对象不能在同一时间段被创建2次，如果需要多条记录，请创建多个实例<br>
	 * @param obj
	 * @throws Exception
	 */
	void create(Object obj);

	/**
	 * 更新对象<br>
	 * 一般要查出对象在更新
	 * 
	 * @param obj
	 * 
	 */
	void refresh(Object obj);

	/**
	 * 更新对象<br>
	 * 
	 * @param obj
	 * @throws Exception
	 */
	void remove(Object obj);
	
	<T> List<T> listSync(Class<T> clz);
}