package x7.core.async;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import x7.core.async.IHeartBeat;


/**
 * 心跳循环器<br>
 * 实现了IHeartBeat接口的service需要注册到此心跳循环器
 * HeartBeator.add(this);
 * @author wyan
 * 
 */
public class HeartBeator {

	private final static long INTERVAL = 50;
	private final static TimeUnit UNIT = TimeUnit.MILLISECONDS;
	private final static ScheduledExecutorService service = Executors
			.newScheduledThreadPool(1);

	private final static CopyOnWriteArrayList<IHeartBeat> tasks = new CopyOnWriteArrayList<IHeartBeat>();

	private static HeartBeator instance;
	public static void newInstance(){
		if (instance == null){
			instance = new HeartBeator();
		}
	}
	
	private HeartBeator(){}
	
	static {
		schedule(new Runnable() {

			@Override
			public void run() {
				try {
					tick();
				} catch (NullPointerException npe){
					npe.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, System.currentTimeMillis() + 1000, INTERVAL);
	}

	private static void schedule(Runnable command, long scheduledAt, long delay) {
		long now = System.currentTimeMillis();
		if (scheduledAt <= now) {
			scheduledAt += 86400000L;
		}
		service.scheduleWithFixedDelay(command, scheduledAt - now, delay, UNIT);
	}

	private static void tick() {
		long now = System.currentTimeMillis();
		for (IHeartBeat task : tasks){
			try{
				task.tick(now);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 注册心跳循环服务
	 * 
	 * @param task
	 */
	public static void add(IHeartBeat task) {
		tasks.add(task);
	}

	/**
	 * 移除心跳循环服务
	 * 
	 * @param task
	 */
	public static void remove(IHeartBeat task) {
		tasks.remove(task);
	}
}
