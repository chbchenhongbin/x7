package x7.core.repository;

import java.util.List;

/**
 * 缓存接口
 * @author sim
 *
 */
public interface ICache {

	void set(String key, String value);
	void set(byte[] key, byte[] value);
	String get(String key);
	byte[] get(byte[] key);
	List<byte[]> get(byte[][] key);
	void delete(byte[] key);
}
