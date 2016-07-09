package x7.repository.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil {

	public static byte[] toBytes(Object obj){
		if (obj == null)
			return null;
		
		/*
		 * BAOS
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				oos.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static <T> T toObject(byte[] bytes){
		if (bytes == null)
			return null;
		/*
		 * BAOS
		 */
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try{
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			return (T) obj;
		}catch (Exception e){
			System.out.println("toObject(byte[] bytes) -------------> " );
			e.printStackTrace();
		}finally{
			try {
				ois.close();
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
