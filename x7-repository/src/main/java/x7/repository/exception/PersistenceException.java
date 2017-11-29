package x7.repository.exception;

public class PersistenceException extends RuntimeException{

	private static final long serialVersionUID = 5749142995896236081L;
	private String message;
	
	public PersistenceException(){

	}
	
	public PersistenceException(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
