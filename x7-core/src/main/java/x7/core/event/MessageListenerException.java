package x7.core.event;

public class MessageListenerException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543300674495178695L;
	
	private String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public MessageListenerException(String message) {
		super(message);
	}
	

}
