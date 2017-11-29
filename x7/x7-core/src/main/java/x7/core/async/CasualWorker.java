package x7.core.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import x7.core.async.IAsyncTask;

/**
 * 
 * 任何复杂的计算，但计算结果不影响后面的计算的情况下，可以创建临时任务, 交给此类处理<br>
 * 在改变数据时不能用，如果要用，在取那个数据时，也必需用
 * CasualWorker.accept(new ICasualTask(){execute(){}});<br>
 * 适应举例：<br>
 * 1. 在创建场景时，初始化场景的数据<br>
 * 2. 在倒计时的时间段里，<br>
 * 
 * @author wangyan
 *
 */
public final class CasualWorker {
	
	private final static ExecutorService service = Executors.newFixedThreadPool(1);
	
	private final static BlockingQueue<IAsyncTask> tasks = new ArrayBlockingQueue<IAsyncTask>(4096);
	
	static {
		
		service.execute(new Runnable(){

			@Override
			public void run() {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true) {
					try {
						tasks.take().execute();
					} catch (NullPointerException npe){
						npe.printStackTrace();
					}catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			
		});

	}
	/**
	 * 接受临时任务，异步执行
	 * @param task
	 * @throws InterruptedException
	 */
	public static void accept(IAsyncTask task) {
		try {
			tasks.put(task);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
