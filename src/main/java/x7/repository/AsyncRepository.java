package x7.repository;

import java.util.List;

import x7.core.bean.IQuantity;
import x7.repository.redis.JedisConnector_Persistence;

/**
 * 
 * 分布式系统的异步数据操作<br>
 * 
 * @author Sim
 *
 */
public class AsyncRepository {

	private static AsyncRepository instance;
	public static AsyncRepository getInstance() {
		if (instance == null){
			instance = new AsyncRepository();
		}
		return instance;
	}
	private AsyncRepository (){
		
	}
	
	/**
	 * 基于Redis的, 用于单线程增长累计数, 单线程模式无需在启动时初始化Redis数据
	 * 
	 * @param obj
	 * @param offset
	 * @return current quantity
	 */
	public int increaseBySingleThreadModel(IQuantity obj, int offset) {
		if (offset < 1) {
			throw new RuntimeException("increasing quantity must > 0");
		}

		String mapKey = obj.getClass().getName();

		int quantity = (int) JedisConnector_Persistence.getInstance().hincrBy(mapKey, obj.getKey(), offset);

		/*
		 * 初始化
		 */
		if (quantity <= offset) {
			obj.setQuantity(0);
			List<IQuantity> list = Repositories.getInstance().list(obj);

			if (! list.isEmpty()) {
				IQuantity q = list.get(0);
				quantity = q.getQuantity() + offset;
				JedisConnector_Persistence.getInstance().hset(mapKey, obj.getKey(), String.valueOf(quantity));
			}
		}

		obj.setQuantity(quantity);

		return quantity;
	}

}
