package x7.repository.redis;

import java.util.List;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import x7.core.config.Configs;

public class JedisConnector_Cache {

	private static JedisPool pool;  
	
	private static JedisConnector_Cache instance;

	
	public static JedisConnector_Cache getInstance(){
		if (instance == null){
			instance = new JedisConnector_Cache();
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(Configs.getIntValue("x7.redis.max"));
			config.setMaxIdle(Configs.getIntValue("x7.redis.idle"));
			config.setJmxEnabled(true);
			config.setJmxNamePrefix("redis-cahce");
			config.setTestOnBorrow(true);
			
			pool = new JedisPool( config, Configs.getString("x7.redis.ip.cache"), Configs.getIntValue("x7.redis.port.cache"));  //6379
		}
		return instance;
	}
	
	private JedisConnector_Cache(){
		
	}
	
	public Jedis get(){
		return pool.getResource();
	}
	
	public void close(Jedis jedis){
		pool.returnResource(jedis);
	}
	
	public void closeBroken(Jedis jedis){
		pool.returnBrokenResource(jedis);
	}
	
	public boolean set(String key, String value){
		if (key == null || key.equals("") ) 
			return false;
		return set(key.getBytes(),value.getBytes());
	}
	
	public boolean set(byte[] key, byte[] value){
		
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return false;
			jedis.set(key,value);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
			return false;
		}
		return true;
	}
	
	public boolean set(byte[] key, byte[] value, int validSeconds){
		
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return false;
			jedis.set(key,value);
			jedis.expire(key, validSeconds);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
			return false;
		}
		return true;
	}
	
	public String get(String key){
		
		String str = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			str = jedis.get(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return str;
	}
	
	public List<byte[]> mget(byte[][] keyArr){
		
		if (keyArr == null || keyArr.length == 0)
			return null;
		
		List<byte[]> byteList = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			byteList = jedis.mget(keyArr);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return byteList;
	}
	
	public byte[] get(byte[] key){
		
		byte[] value = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			value = jedis.get(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return value;
	}
	
	public boolean delete(byte[] key){
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return false;
			jedis.del(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
			return false;
		}
		return true;
	}
	

}
