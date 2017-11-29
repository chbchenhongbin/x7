package x7.repository.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import x7.core.bean.BeanSerial;
import x7.core.repository.ISerialWR;


public class PersistenceUtil {

	public static byte[] toBytes(Object obj){
		if (obj == null)
			return null;
		
		String clzName = obj.getClass().getName();
		ISerialWR wr = BeanSerial.get(clzName);
		if (wr != null) {
			try {
				ByteBuffer buffer = wr.write(obj);
				buffer.flip();
				return buffer.array();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	public static <T> T toObject(Class<T> clz, byte[] bytes){
		if (bytes == null)
			return null;
		
		String clzName = clz.getName();
		ISerialWR wr = BeanSerial.get(clzName);
		if (wr != null) {
			try {
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				return wr.read(buffer);
			} catch (Exception e) {
				System.out.println("toObject(Class<T> clz, byte[] bytes) 1-------------> " +  clz.getName());
				e.printStackTrace();
				return null;
			}
		}
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
			System.out.println("toObject(Class<T> clz, byte[] bytes) 2-------------> " +  clz.getName());
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
