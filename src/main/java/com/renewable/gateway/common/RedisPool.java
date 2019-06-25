package com.renewable.gateway.common;

import com.renewable.gateway.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description：
 * @Author: jarry
 */
//@Component
public class RedisPool {

	private static JedisPool pool;      //Jedis连接池
	private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));     //最大连接数
	private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));       //在jedispool中最大的Idle状态（空闲状态）的jedis实例的个数
	private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));       //在jedispool中最小的Idle状态（空闲状态）的jedis实例的个数
	private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));     //在borrow一个jedis实例的时候，是否要进行验证操作。如果赋值为ture，则得到的redis实例必然是可用的
	private static boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));     //在reture一个jedis实例的时候，是否要进行验证操作。如果赋值为ture，则放回jedispool的redis实例必然是可用的
	private static String redisIp = PropertiesUtil.getProperty("redis.ip");
	private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

	//该方法之后会在类的静态块中调用，所以只会调用一次。所以设置为private
	private static void initPool() {
		JedisPoolConfig config = new JedisPoolConfig();

		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);

		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);

		//默认是true，这里只是为了强调。连接耗尽时，是否阻塞。false会抛出异常（可以监听到的），true会阻塞直到超时。
		config.setBlockWhenExhausted(true);

		//这里采用的构造方法，没有password与database参数
		pool = new JedisPool(config, redisIp, redisPort, 1000 * 2); //超时时间不可设置过短。否则容易因为正常网络延迟，连接池放弃连接，返回错误。
	}

	//静态块会在类加载时，执行
	static {
		System.out.println("redisPool init starting");
		initPool();
		System.out.println("redisPool init end");
	}

	public static Jedis getJedis() {
		return pool.getResource();
	}

	public static void returnResource(Jedis jedis) {
		pool.returnResource(jedis);
	}

	public static void returnBrokenResource(Jedis jedis) {
		pool.returnBrokenResource(jedis);
	}


//    public static void main(String[] args) {
//        Jedis jedis = pool.getResource();
//        jedis.set("jarry_key","jarry_value");
//        pool.returnResource(jedis);
//
//        pool.destroy(); //销毁连接池。临时调用，业务时，不可以使用
//        System.out.println("program is end");
//    }
}
