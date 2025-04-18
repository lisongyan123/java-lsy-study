package com.lsy.java.utils.caffeine;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

public class CaffeineUtil {
    public static void main(String[] args) {

        Cache<Object, Object> cache = Caffeine.newBuilder()
                .initialCapacity(100) //设置缓存的初始化容量
                .expireAfterWrite(Duration.ofHours(10))
                .maximumSize(1000) //设置最大的容量
                .build();
        //向缓存中插入数据
        cache.put("key1", 123);
        //从缓存中取出数据
        Object value1 = cache.get("key1", key -> 456);
        System.out.println(value1);
        //获取没有的数据
        Object value2 = cache.get("key2", key -> 789);
        System.out.println(value2);
    }
}
