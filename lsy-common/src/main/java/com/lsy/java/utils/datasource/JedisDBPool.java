
package com.lsy.java.utils.datasource;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class JedisDBPool {
    private static final Jedis jedis;

    static {
        // 配置连接池
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);  // 设置最大连接数
        config.setMaxIdle(5);  // 设置最大空闲连接数
        config.setBlockWhenExhausted(false);  // 连接耗尽时不阻塞
        // 创建连接池
        JedisPool jedisPool = new JedisPool(config, "localhost", 6379);
        jedis = jedisPool.getResource();
        // 释放连接池
        jedisPool.close();
    }


    public static void main(String[] args) {
        // 操作 List 类型
        jedis.lpush("age", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        jedis.rpush("user", "张三", "李四", "王五");
        System.out.println(jedis.lindex("age", 2));

        // 操作 Hash 类型
        jedis.hset("person", "person1", "胡桃");
        // 通过map方式传入value
        Map<String, String> hashMap = new HashMap();
        hashMap.put("name", "甘雨");
        hashMap.put("age", "23");
        jedis.hset("genshin", hashMap);
        System.out.println(jedis.hget("genshin", "name"));

        // 操作 Set 类型
        jedis.sadd("names", "a", "b", "c", "a");
        System.out.println(jedis.smembers("names"));

        // 操作有序集合 Zset 类型
        jedis.zadd("score", 88, "张三");
        jedis.zadd("score", 90, "李四");
        jedis.zadd("score", 87, "王五");
        System.out.println(jedis.zrange("score", 0, 1));
    }
}