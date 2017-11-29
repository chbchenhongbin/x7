package x7.repository.redis;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import x7.core.config.Configs;

/**
 * 禁止用flushall命令<br>
 * 
 * @author Sim
 *
 */
public class JedisConnector_Persistence {

	private static JedisPool pool;

	private static JedisConnector_Persistence instance;

	public static JedisConnector_Persistence getInstance() {
		if (instance == null) {
			instance = new JedisConnector_Persistence();
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(Configs.getIntValue("x7.redis.max"));
			config.setMaxIdle(Configs.getIntValue("x7.redis.idle"));
			config.setJmxEnabled(true);
			config.setJmxNamePrefix("redis-persistence");
			config.setTestOnBorrow(true);

			System.out.println("x7.redis.ip.persistence = " + Configs.getString("x7.redis.ip.persistence"));
			System.out.println("x7.redis.port.persistence = " + Configs.getString("x7.redis.port.persistence"));

			pool = new JedisPool(config, Configs.getString("x7.redis.ip.persistence"),
					Configs.getIntValue("x7.redis.port.persistence")); // 6379
		}
		return instance;
	}

	private JedisConnector_Persistence() {

	}

	public Jedis get() {
		return pool.getResource();
	}

	public void close(Jedis jedis) {
		pool.returnResource(jedis);
	}

	public void closeBroken(Jedis jedis) {
		pool.returnBrokenResource(jedis);
	}

	public void set(String key, String value) {
		if (key == null || key.equals(""))
			return;
		// set(key.getBytes(),value.getBytes());

		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key, value);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}
	}

	public void set(String key, String value, int seconds) {
		if (key == null || key.equals(""))
			return;
		// set(key.getBytes(),value.getBytes());

		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key, value);
			jedis.expire(key, seconds);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}
	}

	public void set(byte[] key, byte[] value, int seconds) {

		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key, value);
			jedis.expire(key, seconds);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

	}

	public void set(byte[] key, byte[] value ) {

		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key, value);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

	}

	public String get(String key) {

		String str = null;
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return null;
			str = jedis.get(key);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return str;
	}

	public List<byte[]> mget(byte[][] keyArr) {

		if (keyArr == null || keyArr.length == 0)
			return null;

		List<byte[]> byteList = null;
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return null;
			byteList = jedis.mget(keyArr);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return byteList;
	}

	public byte[] get(byte[] key) {

		byte[] value = null;
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return null;
			value = jedis.get(key);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return value;
	}

	public void delete(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.del(key);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

	}

	public void hset(String mapName, String key, String value) {
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.hset(mapName, key, value);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}
	}

	public String hget(String mapName, String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return "";
			value = jedis.hget(mapName, key);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return value;
	}

	public long hincrBy(String mapName, String key, long increment) {
		long value = 0;
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return 0;
			value = jedis.hincrBy(mapName, key, increment);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return value;
	}
	
	public boolean lock(String key){
		
		boolean isLock = false;
		
		String value = "LOCK";
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return isLock;
			isLock = (jedis.setnx(key, value) == 1);
			if (isLock) {
				jedis.expire(key, 3); 
			}
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

		return isLock;
	}
	
	public void unLock(String key){
		Jedis jedis = null;
		try {
			jedis = get();
			if (jedis == null)
				return;
			jedis.del(key);
			pool.returnResource(jedis);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
		}

	}
}
