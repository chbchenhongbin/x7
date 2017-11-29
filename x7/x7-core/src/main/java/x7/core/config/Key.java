package x7.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented  
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) 
public @interface Key {
	int id() default 0;
	String desc() default "";
}
