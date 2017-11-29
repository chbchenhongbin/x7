package x7.config;

import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader; 
import java.io.OutputStreamWriter; 
import java.util.ArrayList; 
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.config.Configs;
import x7.core.util.KeyUtil;



public class TextParser{
	private static TextParser instance = null;
	private  Map<String, Object> map = null;
	
	public static TextParser getInstance(){
		if (instance == null){
			instance = new TextParser();
		}
		return instance;
	}
	private TextParser(){

	}
	
	
	
	public void load(String localAddress, String configSpace){
		
		map = Configs.referMap(configSpace);
		
		try{
//			String root = PathUtil.getRoot();
//
//			if (configSpace == null || configSpace.equals("")){
//				readConfigs(root + "config", null);
//			}else{
//				readConfigs(root + "config" + "/"+ configSpace, configSpace);
//			}
			
			readConfigs(localAddress + "/"+ configSpace, configSpace);

		}catch (Exception e){
			e.printStackTrace();
			String notes = "无法启动";
			System.err.println("\n"+notes+"\n");			
			String err = "加载配置文件出错,请检查配置文件config/*.txt";
			System.err.println(err + "\n");
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(0);

		}
	}
	
	 
	
	public int getIntValue(String key){
	Integer value = 0;
	
		try{
			value = Integer.valueOf(map.get(key)+""); 
		}catch (MissingResourceException mre){
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();
		}catch (Exception e){
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "="+map.get(key);
			System.err.println(err);
			e.printStackTrace();
		}
		return value;
	}
	
	public String getString(String key){
		String value = "";
		
		try{
			value = map.get(key)+"";
		}catch (MissingResourceException mre){
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();

		}catch (Exception e){
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "="+map.get(key);
			System.err.println(err);
			e.printStackTrace();

		}
		return value;
	}
	
	public long getLongValue(String key){
		Long value = 0L;
		
		try{
			value = Long.valueOf(map.get(key)+"");
		}catch (MissingResourceException mre){
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();

		}catch (Exception e){
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "="+map.get(key);
			System.err.println(err);
			e.printStackTrace();
	
		}
		return value;
	}
	
	
	public void readConfigs(String path, String space){
		
		File file = new File(path);
		if (file.isDirectory()){
			for (String childStr : file.list()){
				if (childStr.endsWith(".txt") || childStr.endsWith(".properties") || childStr.endsWith(".cfg") || childStr.endsWith(".init")){
					System.out.println(".... " + childStr);
					readConfig(path+"/"+childStr, space);
				}else if (! childStr.contains(".")){
					if (space == null || space.equals("")){
						readConfigs(path+"/"+childStr, childStr);
					}else{
						readConfigs(path+"/"+childStr, space+"."+childStr);
					}
				}
			}
			
		}
	}
	 
	/**
	 × 读取文件存入configData中
	 * @param path
	 * @return
	 */
	public void readConfig(String path,String space){
		FileInputStream fis=null;
		BufferedReader br=null;
		try {
			fis=new FileInputStream(path);
			br=new BufferedReader(new InputStreamReader(fis,"utf-8"));
			String dataStr="";
			while((dataStr=br.readLine())!=null){
				if(dataStr.contains("=")){
					//等号左边为key，等号右边为value
					String key=dataStr.substring(0,dataStr.indexOf("=")).trim();
					String value=dataStr.substring(dataStr.indexOf("=")+1);
					
					if (space != null && !space.equals("")){
						key = space + "." + key;
					}
					
					if (key.contains(".")){
						List<String> keyList = KeyUtil.getKeyList(key);
						int size = keyList.size();

						Map<String, Object> mapObject = map;
						int length = size - 1;
						for (int i = 0; i < length; i++) {
							String k = keyList.get(i);
							Object o = mapObject.get(k);
							if (o == null){
								o = new ConcurrentHashMap<String,Object>();
								mapObject.put(k, o);
							}
							mapObject = (Map<String, Object>) o;
						}
						mapObject.put(keyList.get(length), value);
					}else {
						map.put(key, value);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fis.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 将文件读到list中
	 * @param path
	 * @return
	 */
	public static List<String> readContent(String path){
		List<String> list=new ArrayList<String>();
		FileInputStream fis=null;
		BufferedReader br=null;
		try {
			fis=new FileInputStream(path);
			br=new BufferedReader(new InputStreamReader(fis,"utf-8"));
			String dataStr="";
			while((dataStr=br.readLine())!=null){ 
				list.add(dataStr);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fis.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
		return list;
	}
	
	 
	/**
	 * 写入文件
	 * @param path
	 * @param data
	 */
	public static void writeConfig(String path,List<String> data){
		FileOutputStream fos=null;
		BufferedWriter br=null;
		try {
			fos=new FileOutputStream(path);
			br=new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
			for (String str : data) {
				br.write(str);
				br.newLine();
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			 try {
				br.flush();
				fos.flush();
				br.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
