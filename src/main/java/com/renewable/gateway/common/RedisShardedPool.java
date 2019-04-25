package com.renewable.gateway.common;

/**
 * @Description：
 * @Author: jarry
 */
public class RedisShardedPool {


//    private static ShardedJedisPool pool;      //sharded jedis连接池
//    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));     //最大连接数
//    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));       //在jedispool中最大的Idle状态（空闲状态）的jedis实例的个数
//    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));       //在jedispool中最小的Idle状态（空闲状态）的jedis实例的个数
//    private static boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));     //在borrow一个jedis实例的时候，是否要进行验证操作。如果赋值为ture，则得到的redis实例必然是可用的
//    private static boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));     //在reture一个jedis实例的时候，是否要进行验证操作。如果赋值为ture，则放回jedispool的redis实例必然是可用的
//    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
//    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
//    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
//    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
//
//    //该方法之后会在类的静态块中调用，所以只会调用一次。所以设置为private
//    private static void initPool() {
//        JedisPoolConfig config = new JedisPoolConfig();
//
//        config.setMaxTotal(maxTotal);
//        config.setMaxIdle(maxIdle);
//        config.setMinIdle(minIdle);
//
//        config.setTestOnBorrow(testOnBorrow);
//        config.setTestOnReturn(testOnReturn);
//
//        //默认是true，这里只是为了强调。连接耗尽时，是否阻塞。false会抛出异常（可以监听到的），true会阻塞直到超时。
//        config.setBlockWhenExhausted(true);
//
//        //这里采用的构造方法，没有password与database参数
//        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
////        info1.setPassword(xxx);   //设置密码
//        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);
//
//        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2); //此处确定为两个，也可使用默认size
//        jedisShardInfoList.add(info1);
//        jedisShardInfoList.add(info2);
//
//        pool = new ShardedJedisPool(config, jedisShardInfoList,
//                Hashing.MURMUR_HASH,    //默认，MURMUR_HASH或者MD5_HASH。 MURMUR_HASH表示的就是一致性hash算法
//                Sharded.DEFAULT_KEY_TAG_PATTERN //枚举
//        );
//    }
//
//    //静态块会在类加载时，执行
//    static {
//        initPool();
//    }
//
//    public static ShardedJedis getJedis() {
//        return pool.getResource();
//    }
//
//    public static void returnResource(ShardedJedis jedis) {
//        pool.returnResource(jedis);
//    }
//
//    public static void returnBrokenResource(ShardedJedis jedis) {
//        pool.returnBrokenResource(jedis);
//    }
//
//    public static void main(String[] args) {
//        ShardedJedis jedis = pool.getResource();
//
//        for (int i = 0; i < 10; i++) {
//            jedis.set("key" + i, "value" + i);
//        }
//        returnResource(jedis);
////        pool.destroy();
//        System.out.println("Program is end");
//    }
//
}
