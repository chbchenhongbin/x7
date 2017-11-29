package x7.core.repository;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author Sim
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE}) 
public @interface Persistence {
	
	String SUFFIX = "${SUFFIX}";
	String PAGINATION = "${PAGINATION}";
	
	int KEY_ONE = 1;
	int KEY_TWO = 2;
	int KEY_SHARDING = 7;
	int KEY_ONE_SHARDING = 17;
	
	/**
	 * 
	 * only effect on property
	 */
	int key() default 0;
	/**
	 * 
	 * only effect on property<br>
	 * will not save the property in relation DB, like MySql<br>
	 * but will save the property int cache, or K-V DB,like mc or redis<br>
	 * instead of "transient", while transport the stream of object
	 */
//	boolean ignored() default false;
	/**
	 * 
	 * only effect on property
	 */
	boolean isNotAutoIncrement() default false;
	
	/**
	 * just string(60<=length < 512), datetime, text<br>
	 * only effect on getter<br>
	 */
	String type() default "";
	/**
	 * 
	 * only effect on getter<br>
	 */
	int length() default 60;
	
	/**
	 * 
	 * only old db suggested
	 */
	String mapper() default "";
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface isMobile{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface isEmail{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface notNull{
	}
	
	/**
	 * 
	 * 类名上标记不可使用缓存，仅限二级缓存
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	@interface noCache{
	}
	
	/**
	 * 
	 * only effect on property<br>
	 * will not save the property in relation DB, like MySql<br>
	 * but will save the property int cache, or K-V DB,like mc or redis<br>
	 * instead of "transient", while transport the stream of object
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface ignore{
	}
	

}
