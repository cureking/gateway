package com.renewable.gateway.common;

import com.renewable.gateway.BaseSpringTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertNotNull;

/**
 * RedisPool Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 20, 2019</pre>
 */
public class RedisPoolTest extends BaseSpringTestCase {

    private Jedis jedis;

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getJedis()
     */
    @Test
    public void testGetJedis() throws Exception {
//TODO: Test goes here...
        jedis = RedisPool.getJedis();
        assertNotNull(jedis);
    }

    /**
     * Method: returnResource(Jedis jedis)
     */
    @Test
    public void testReturnResource() throws Exception {
//TODO: Test goes here...

    }

    /**
     * Method: returnBrokenResource(Jedis jedis)
     */
    @Test
    public void testReturnBrokenResource() throws Exception {
//TODO: Test goes here... 
    }


    /**
     * Method: initPool()
     */
    @Test
    public void testInitPool() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = RedisPool.getClass().getMethod("initPool"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
