package x7.core.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;


public class StringUtil {

	public static String toUTF8(String strISO88591){
		try {
			return new String(strISO88591.getBytes("ISO8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strISO88591;
	}
	
	public static int parse(String input) {
		if(isEmail(input))
			return 1;
		else if(isMobile(input))
			return 2;
		else 
			return 3;
	}
	
	public static boolean isNotNull(String str){
		return !isNullOrEmpty(str);
	}
	
	public static boolean isNullOrEmpty(String str){
		return str == null || str.equals("") || str.equals("null") || str.equals("NaN") || str.equals("undefined");
	}
	
	public static String filter(String str){
		if (! isNullOrEmpty(str)){
			str = str.replace("<", "&lt").replace(">", "&gt");
			str = str.replace("DROP ", "DROP%")
					.replace("drop ", "drop%")
					.replace("DELETE ", "DELETE%")
					.replace("delete ", "delete%")
					.replace("CREATE ", "CREATE%")
					.replace("create ", "create%")
					.replace("UPDATE ", "UPDATE%")
					.replace("update ", "update%")
					;
		}
		
		return str;
	}
	
	public static String replaceForSimpleSplit(String str, String regE){
		if (! str.contains(",")){
			if (str.contains(":"))
				str = str.replace(":", ",");
			else if (str.contains("/"))
				str = str.replace("/", ",");
			else if (str.contains(";"))
				str = str.replace(";", ",");
			else if (str.contains("-"))
				str = str.replace("-", ",");
			else if (str.contains("_"))
				str = str.replace("_", ",");
		}
		
		return str;
	}
	
	public static boolean isMobile(String mobile){
		String pMobile = "^(1(([34578][0-9])))\\d{8}$";
		return Pattern.matches(pMobile, mobile);
	}
	
	public static boolean isNumeric(String str){
		 for(int i=0;i<str.length();i++){
		      if (!Character.isDigit(str.charAt(i))){
		    	  return false;
		      }
		 }
		 return true;
	}
	
	public static boolean isEmail(String email){

		if((email.indexOf("@") == -1)){
			return false;
		}
		String pEmail = "^[\\w-]{1,40}(\\.[\\w-]{1,20}){0,6}@[\\w-]{1,40}(\\.[\\w-]{1,20}){1,6}$";
		return Pattern.matches(pEmail, email);

	}
	
	public static String listToString (List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		String str = "";
		for (String item : list) {
			str += item + ",";
		}
		return str.substring(0, str.length() - 1);
	}
	
	public static String nullToEmpty(String str){
		if (isNullOrEmpty(str))
			return "";
		return str;
	}
}
