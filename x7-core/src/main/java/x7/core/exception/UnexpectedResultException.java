package x7.core.exception;

public class UnexpectedResultException extends RuntimeException {

	private static final long serialVersionUID = -748560635449296031L;

	private Object result;
	
	public UnexpectedResultException(String message, Object result){
		super(message);
		
		this.result = result;
	}

	public Object getResult() {
		return result;
	}
	
}
