package x7.core.async;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MasterMessageExecutor {

	private final static Executor executor = Executors.newSingleThreadExecutor();
	
	public static void submit(Runnable runnable){
		executor.execute(runnable);
	}
	
}
