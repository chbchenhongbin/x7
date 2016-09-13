package x7.repository;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class GenericTester {

	public static Class getClass(Type type, int i) {  
        if (type instanceof ParameterizedType) { // 处理泛型类型  
            System.out.println("1111111");  
            return getGenericClass((ParameterizedType) type, i);  
        } else if (type instanceof TypeVariable) {  
            System.out.println("--------" + ((Class) getClass(((TypeVariable) type).getBounds()[0], 0)).getName());  
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象  
        } else {// class本身也是type，强制转型  
            return (Class) type;  
        }  
    }  
  
    public static Class getGenericClass(ParameterizedType parameterizedType, int i) {  
        Object genericClass = parameterizedType.getActualTypeArguments()[i];  
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型  
            System.out.println("111111");  
            return (Class) ((ParameterizedType) genericClass).getRawType();  
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型  
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();  
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象  
            System.out.println("33333333");  
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);  
        } else {
            System.out.println("444444");  
            return (Class) genericClass;  
        }  
    }  
  

}
