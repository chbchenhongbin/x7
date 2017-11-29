package x7.repository.exception;

public class RollbackException extends RuntimeException{

	private static final long serialVersionUID = 5749142995896236081L;
	private String message;
	
	public RollbackException(){

	}
	
	public RollbackException(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
