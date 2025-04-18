package lsy.work.core.jdk11.guava.future;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCache {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Cache<String, String> cache1 = CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS) // 若缓存项在给定的时间内没有被写访问，则回收（延时回收）。
                .expireAfterAccess(3, TimeUnit.SECONDS) // 若缓存项在给定的实际内没有被读访问，则回收（延时回收）。
                .weakValues() // 弱引用存储值，可垃圾回收
                .weakKeys() // 弱引用存储键，可垃圾回收
                .concurrencyLevel(8) // 并发级别，指同时支持写缓存的线程数
                .initialCapacity(10)  // 初始化容量
                .maximumSize(2000) // 最大存储上限，超过按照LRU算法移除缓存项
                .recordStats()  // 统计缓存的命中率
                .removalListener(new MyListener()) // 缓存的移除监听（监听到的是新值）
                .build();
        cache1.put("myKey", "myValue");
        System.out.println("获取值：" + cache1.getIfPresent("myKey"));

        cache1.invalidate("myKey");
        System.out.println("获取我的key:" + cache1.get("myKey", () -> "操作数据库"));

        Thread.sleep(5000);
        System.out.println("再次获取：" + cache1.get("myKey", () -> "再次操作数据库"));

        cache1.put("myKey11", "myValue11"); // 自然的过期，并不会触发监听
        Thread.sleep(5000);
        System.out.println(cache1.getIfPresent("myKey11"));
        System.in.read();
    }

    public static class MyListener implements RemovalListener<String, String> {
        @Override
        public void onRemoval(RemovalNotification<String, String> notification) {
            System.out.println("修改的key:" + notification.getKey());
            System.out.println("修改的值:" + notification.getValue());
            System.out.println("修改的原因:" + notification.getCause());
        }
    }

}