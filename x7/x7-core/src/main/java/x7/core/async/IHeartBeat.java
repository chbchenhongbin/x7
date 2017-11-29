package x7.core.async;

/**
 * <li>心跳接口</li>
 * 必需异步实现tick方法，实现此接口的service，需要在构造或初始化时：<br>
 * HeartBeator.add(this);
 * @author wyan
 *
 */
public interface IHeartBeat {
	/**
	 * 处理心跳循环的业务逻辑方法
	 * @param now
	 */
	void tick(long now);
	
}
