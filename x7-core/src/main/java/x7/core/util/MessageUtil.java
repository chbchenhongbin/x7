package x7.core.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wyan
 * 
 */
public class MessageUtil {
	
	public static Date readDate(ByteBuffer buffer)  {
		long time = buffer.getLong();
		if (time == 0)
			return null;
		return new Date(time);
	}
	
	public static void writeDate(ByteBuffer buffer, Date date) {
		if (date == null){
			buffer.putLong(0L);
		}else{
			buffer.putLong(date.getTime());
		}
	}
	
	public static String readString(ByteBuffer buffer)  {
		short stringLength = 0;
		byte[] stringBytes;
		stringLength = buffer.getShort();
		stringBytes = new byte[stringLength];
		buffer.get(stringBytes);
		try {
			String str =  new String(stringBytes, "UTF-8");
			if (StringUtil.isNullOrEmpty(str))
				return null;
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeString(ByteBuffer buffer, String content){
		if (content == null)
			content = "";
		byte[] bytes = null;
		try {
			bytes = content.getBytes("UTF-8");
			buffer.putShort((short) bytes.length);
			buffer.put(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static int getLength(String content)  {
		if (content == null)
			content = "";
		try {
			return 2 + content.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 2;
	}
	
	public static Map<Integer,Integer> readMap(ByteBuffer buffer) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		short size = buffer.getShort();
		for (short i=0; i<size; i++){
			map.put(buffer.getInt(), buffer.getInt());
		}
		return map;
	}
	
	public static void writeMap(ByteBuffer buffer, Map<Integer,Integer> map) {
		if (map==null || map.isEmpty()){
			buffer.putShort((byte)0);
			return;
		}
		short size = (short) map.size();
		buffer.putShort(size);
		for (Integer key : map.keySet()){
			buffer.putInt(key);
			buffer.putInt(map.get(key));
		}
	}
	
	public static int getLength(Map<Integer,Integer> map){
		int size = 0;
		if (map!=null ){
			size = map.size() * 8;
		}
		return 2 + size;
	}

}
