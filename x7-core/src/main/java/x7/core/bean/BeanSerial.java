package x7.core.bean;

import java.util.HashMap;
import java.util.Map;

import x7.core.repository.ISerialWR;

public class BeanSerial {

	private static Map<String, ISerialWR> map = new HashMap<String, ISerialWR>();

	public static Map<String, ISerialWR> getMap() {
		return map;
	}

	public static void setMap(Map<String, ISerialWR> mapx) {
		map = mapx;
	}
	
	public static ISerialWR get(String clzName){
		ISerialWR wr = map.get(clzName);
		
		if (wr == null) {
			try{
				wr = (ISerialWR) Class.forName(clzName + "WR").newInstance();
				System.out.println("wr = " + wr);
				map.put(clzName, wr);
			}catch (Exception e) {
				map.put(clzName, null);
			}
		}
		
		return wr;
	}
}
