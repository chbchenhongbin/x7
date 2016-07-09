package x7.repository.exception;

public class ShardingException extends RuntimeException{

	private static final long serialVersionUID = -2001043435246535808L;
	private String message;
	
	public ShardingException(){

	}
	
	public ShardingException(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
}
