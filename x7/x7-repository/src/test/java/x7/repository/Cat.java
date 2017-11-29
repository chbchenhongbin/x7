package x7.repository;

import java.util.List;

import x7.core.bean.BeanElement;
import x7.core.bean.FieldAndMethod;
import x7.core.bean.Parser;
import x7.core.repository.ReflectionCache;
import x7.core.util.BeanUtilX;

public class Cat {

	private Boolean isPet;
	private boolean isHapply;
	public Boolean getIsPet() {
		return isPet;
	}
	public void setIsPet(Boolean isPet) {
		this.isPet = isPet;
	}
	public boolean isHapply() {
		return isHapply;
	}
	public void setHapply(boolean isHapply) {
		this.isHapply = isHapply;
	}
	@Override
	public String toString() {
		return "Cat [isPet=" + isPet + ", isHapply=" + isHapply + "]";
	}
	
	
	public static void main(String[] args) {
		List<BeanElement> list = BeanUtilX.getElementList(Cat.class);
		for (BeanElement be : list){
			System.out.println("property="+be.property +", setter="+be.setter + ", getter="+be.getter);
		}
		
		ReflectionCache cache = Parser.getReflectionCache(Cat.class);
		for (FieldAndMethod fm : cache.getMap().values()){
			System.out.println("property="+fm.getProperty() +", setter="+fm.getSetterName() + ", getter="+fm.getGetterName());
		}
	}
}
