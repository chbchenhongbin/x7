package x7.core.repository;

public class CacheException extends RuntimeException {
	
	private static final long serialVersionUID = -264402035167527666L;
	public CacheException(){
		
	}
	public CacheException(String msg){
		super(msg);
	}
}