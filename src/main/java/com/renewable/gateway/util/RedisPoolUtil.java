package com.renewable.gateway.util;

import com.renewable.gateway.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class RedisPoolUtil {

	/**
	 * 设置key的有效期，单位是秒
	 *
	 * @param key
	 * @param exTime
	 * @return
	 */
	public static Long expire(String key, int exTime) {
		Jedis jedis = null;
		Long result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.expire(key, exTime);
		} catch (Exception e) {
			log.error("expire key:{} exTime:{} error", key, exTime, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	//exTime的单位是秒
	public static String setEx(String key, String value, int exTime) {
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.setex(key, exTime, value);
		} catch (Exception e) {
			log.error("setex key:{} value:{} exTime:{} error", key, value, exTime, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static String set(String key, String value) {
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.set(key, value);
		} catch (Exception e) {
			log.error("set key:{} value:{} error", key, value, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static String get(String key) {
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.get(key);
		} catch (Exception e) {
			log.error("get key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static Long del(String key) {
		Jedis jedis = null;
		Long result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.del(key);
		} catch (Exception e) {
			log.error("del key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}


//    public static void main(String[] args) {
//        Jedis jedis = RedisPool.getJedis();
//        RedisPoolUtil.set("keyTest","valueTest");
//        String value = RedisPoolUtil.get("keyTest");
//        System.out.println("valueTest: "+value);
//        RedisPoolUtil.setEx("keyEx","valueEx",60*10);
//        RedisPoolUtil.expire("keyTest",60*20);
//        RedisPoolUtil.del("keyTest");
//        RedisPoolUtil.del("keyEx");
//        System.out.println("end");
//    }
}
