package x7.core.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import x7.core.async.IAsyncTask;

/**
 * 重要的服务继承此类，以实现异步处理，每个子类将拥有独立的线程
 * @author wyan
 *
 */
public class AsyncService extends Thread{
	private final BlockingQueue<IAsyncTask> tasks = new ArrayBlockingQueue<IAsyncTask>(
			1000);

	private boolean isWorking = false;
	
	public AsyncService(){
		isWorking = true;
		this.start();
	}
	
	public AsyncService(String name){
		isWorking = true;
		this.setName(name);
		this.start();
	}
	
	protected void accept(IAsyncTask task) {
		try {
			tasks.put(task);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}	


	@Override
	public void run() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (isWorking) {
			try {
				tasks.take().execute();
			} catch (Exception e){
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 终止服务, 如果需要消耗service对象, 请先也必须调用此方法
	 */
	public void abort(){
		this.accept(new IAsyncTask(){

			@Override
			public void execute() throws Exception {
				isWorking = false;
				
			}});
	}

	
}
