package x7.config;

import java.io.File;

public class PathUtil {

	public static String getRoot(){
		String path = System.getProperty("user.dir");
		
		
		String osName = System.getProperty("os.name");
		
		String root = "";
		
		if (osName.toLowerCase().contains("win")){
			root = path;
			root = path.substring(0,path.lastIndexOf(File.separator) + 1);
		}else{ 
			root = path.substring(0,path.lastIndexOf(File.separator) + 1);
		}
			
		
		return root;
	}
}
