package zxt.oop.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import x7.repository.dao.Tx;
import x7.repository.exception.PersistenceException;


@Aspect
@Configuration
public class TxAop {

	@Pointcut("execution(public * zxt.oop.controller.*.*(..))")
	public void cut(){
		
	}

	@Around("cut()")
	public Object around(ProceedingJoinPoint proceedingJoinPoint){
		Tx.begin();
		try{
			Object obj = null;
			org.aspectj.lang.Signature signature =  proceedingJoinPoint.getSignature(); 
			Class returnType = ((MethodSignature) signature).getReturnType(); 
			if (returnType == void.class){

				proceedingJoinPoint.proceed();
			}else{
				obj = proceedingJoinPoint.proceed();
			}
			
			Tx.commit();
			return obj;
		}catch(Throwable e){
			e.printStackTrace();
			Tx.rollback();
			if (e instanceof PersistenceException){// or other runtime exception
				throw new PersistenceException(e.getMessage());
			}else{
				throw new PersistenceException("server busy");
			}
		}
		
	}
}
