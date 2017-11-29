package x7.core.async;

/**
 * 
 * 任何复杂的计算，但计算结果不影响后面的计算的情况下，可以创建临时任务<br>
 * CasualWorker.accept(new ICasualTask(){execute(){}});<br>
 * 适应举例：<br>
 * 1. 在创建场景时，初始化场景的数据<br>
 * 2. 在倒计时的时间段里，<br>
 * 
 * 直接在使用时创建匿名实例<br>
 * 
 * @author wangyan
 *
 */
public interface IAsyncTask{
	void execute() throws Exception;
}
