package x7.core.mq;

import java.io.Serializable;

import x7.core.event.IEvent;

/**
 * 
 * 收到来自消息中间件的消息后, 需要派发(EventDispatcher.dispatch(event)) <br>
 *
 */
public class MessageEvent implements IEvent, Serializable{

	private static final long serialVersionUID = -2529175147748847706L;
	private String type; //topic
	private String content;// IEventOwner有时候无法自动转JSON,
	private int reTimes;
	private String tag;
	
	public MessageEvent(String type, String content){
		this.type = type;
		this.content = content;
	}

	@Override
	public String getType() {
		return type;
	}

	/**
	 * IEventOwner有时候无法自动转JSON,<br>
	 * 复杂的对象无法用字符串传输
	 * 
	 */
	public String getContent() {
		return content;
	}


	public int getReTimes() {
		return reTimes;
	}

	public void setReTimes(int reTimes) {
		this.reTimes = reTimes;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "MessageEvent [type=" + type + ", content=" + content + ", reTimes=" + reTimes + ", tag=" + tag
				+ "]";
	}
}
