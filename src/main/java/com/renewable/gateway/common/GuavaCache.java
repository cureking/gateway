package com.renewable.gateway.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
//  由于guavacache不支持不同数据不同过期策略，故抛弃。引以为戒啊。
public class GuavaCache {

    //由于采用了lombok的@Slf4j的logback。所以必须要再次声明建立Logger
    //声明一个静态的内存块-guavacache
    private static LoadingCache<String, String> loadingCache =
            CacheBuilder.
                    newBuilder().
                    //这里设置了guavacache的初始容量与最大容量。如果最终，清洗数据的时域TYPE采用缓存，也许需要重新设置缓存的配置。甚至得从guava转为redis
                            initialCapacity(1000).
                    maximumSize(10000).
                    expireAfterAccess(12, TimeUnit.HOURS).
                    build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String s) throws Exception {
                            //  return null;    //默认的写法，在调用时需注意xx=key.value和xx.equals(key)。前者会引发nullpointError。故改写
                            return "null";
                        }
                    });

    public static void setKey(String key, String value) {
        loadingCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = loadingCache.get(key);
            if (value.equals("null")) {
                return null;
            }
            return value;
        } catch (Exception e) {
            log.error("LocalCache get error", e);
        }
        return null;
    }
}
