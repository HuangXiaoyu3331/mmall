package com.huang.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * guava缓存工具类
 *
 * @author hxy
 * @date 2019/01/14
 */
@Slf4j
public class TokenCache {

    /**
     * token的前缀，因为跟TokenCache的业务非常紧密，所以放在这里，不放在Const里面
     */
    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            //设置缓存的初始容量
            .initialCapacity(1000)
            //设置最大缓存，当缓存数量超过最大缓存的时候，guava会使用LRU算法(最少使用算法)来移除缓存项
            .maximumSize(10000)
            //设置缓存的有校期，这里设置为12个小时
            .expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    //替换成字符串的null，避免后面在用key.equals(XXX)的时候，报空指针异常
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    public static String getKey(String key) {
        String value = null;
        String nullStr = "null";
        try {
            value = localCache.get(key);
            if (nullStr.equals(value)) {
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            log.error("localCache get error", e);
        }
        return null;
    }
}
