package x7.core.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldAndMethod{
	private Field field;
	private Method setter;
	private Method getter;
	
	
	private String property;
	private String setterName;
	private String getterName;
	
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	public Method getSetter() {
		return setter;
	}
	public void setSetter(Method setter) {
		this.setter = setter;
	}
	public Method getGetter() {
		return getter;
	}
	public void setGetter(Method getter) {
		this.getter = getter;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getSetterName() {
		return setterName;
	}
	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}
	public String getGetterName() {
		return getterName;
	}
	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}
	@Override
	public String toString() {
		return "FieldAndMethod [field=" + field == null ? "null" : field.getName() + ", setter=" + setter.getName() + ", getter=" + getter.getName() + "]";
	}
}