package x7.core.event;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Wangyan
 *
 */
public interface IEventReceiver {


	final static ConcurrentHashMap<String,TreeMap<String,IEventListener>> listenersMap = new ConcurrentHashMap<String,TreeMap<String,IEventListener>>();

	
	/**
	 * 多次创建同一类的实例，是不会重复添加同一listener的<br>
	 * 程序的正常运行，依赖Event引用的对象<br>
	 * 服务端框架，所以监听器的添加必须在此方法里实现<br>
	 * @param eventType
	 * @param listener
	 */
//	void addEventListener(String eventType, IEventListener listener);
	/**
	 * 小心调用此方法，举例：<br> 
	 * heroListener,不能调用的， <br>
	 * seceneListener，如果是每天多个任务，只能在全部结束后，才能调用
	 * @param eventType
	 * @param listener
	 */
//	void removeEventListener(String eventType, IEventListener listener);
}


