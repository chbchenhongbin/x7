package x7.core.util;

public class SetterByProperty {
	
	public static final String SETTER_PREFIX = "set";
	
	public static String convertPropToSetter(String property) throws Exception{

		String setter = SETTER_PREFIX;
		
		if (property.equals(""))
			return setter;
		
		String c = property.substring(0, 1);
		int length = property.length();
		String remains = "";
		if (length>1)
		{
			remains = property.substring(1);
			return setter+c.toUpperCase()+remains;
		}
		
		return setter+c.toUpperCase();
	}
	

}
