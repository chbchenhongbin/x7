package x7.core.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ScheduledTaskService {
	public final static long ONE_DAY = 86400000L;
	private final ScheduledExecutorService service = Executors
			.newScheduledThreadPool(2);
	private final TimeUnit UNIT = TimeUnit.MILLISECONDS;

	private static ScheduledTaskService instance;
	public static ScheduledTaskService getInstance(){
		if (instance == null){
			instance = new ScheduledTaskService();
		}
		return instance;
	}
	
	/**
	 * ONE SHOT ACTION
	 * 
	 * @param command
	 * @param delay
	 *            call TimeUtil.ONE_DAY, or TimeUtil.ONE_HOUR, ....
	 * 
	 * <BR>
	 *            SAMPLE:<BR>
	 *            service.schedule(new Runnable() {<BR>
	 * <BR>
	 * @Override public void run() {<BR>
	 *           System.out.println(">>>>>>>>>>>>>>>>");<BR>
	 *           }<BR>
	 *           }, TimeUtil.ONE_MINUTE);<BR>
	 */
	public void schedule(Runnable command, long delay) {
		try {
			service.schedule(command, delay, UNIT);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * REPEATED ACTION
	 * 
	 * @param command
	 * @param scheduledAt
	 *            call TimeUtil.scheduledAt(int HOUR) or
	 *            TimeUtil.scheduledAt(int HOUR, int minute)
	 * @param delay
	 *            call TimeUtil.ONE_DAY, or TimeUtil.ONE_HOUR, .... <BR>
	 *            SAMPLE:<BR>
	 *            service.schedule(new Runnable() {<BR>
	 * <BR>
	 * @Override public void run() {<BR>
	 *           System.out.println(">>>>>>>>>>>>>>>>");<BR>
	 *           }<BR>
	 *           }, TimeUtil.scheduledAt(10, 33), TimeUtil.ONE_MINUTE);<BR>
	 */
	public void schedule(Runnable command, long scheduledAt, long delay) {
		long now = System.currentTimeMillis();
		try {
			service.scheduleWithFixedDelay(command, scheduledAt - now, delay,
					UNIT);
		} catch (NullPointerException npe){
			npe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
