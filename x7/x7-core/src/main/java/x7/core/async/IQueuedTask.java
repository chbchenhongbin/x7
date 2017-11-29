package x7.core.async;

/**
 * 仅用在IQueuedService里，例如，军团任务，邮件等
 * @author wyan
 *
 */
public interface IQueuedTask {

	void submit();
}
