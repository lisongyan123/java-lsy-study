package lsy.work.core.jdk11.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 令牌桶限流(smoothbursty)
 */
public class SmoothBurstyRateLimitTest {

    public static void main(String[] args) {
        smoothBurstyRate();
        smoothWarmingUp();
    }

    /**
     * 平滑突发限流，应对突发流量，比如突然之间访问，可以与之令牌，滞后处理
     */
    public static void smoothBurstyRate() {
        //每秒允许5个请求，表示桶容量为5且每秒新增5个令牌，即每隔0.2毫秒新增一个令牌
        RateLimiter limiter = RateLimiter.create(5);
        //一次性消费5个令牌
        System.out.println(limiter.acquire(100));
        //limiter.acquire(1)将等待差不多 2 秒桶中才能有令牌
        System.out.println(limiter.acquire(1));
        //固定速率
        System.out.println(limiter.acquire(1));
        //固定速率
        System.out.println(limiter.acquire(1));
        //固定速率m
        System.out.println(limiter.acquire(1));
    }

    /**
     * smoothWarmingUp 带有预热期的平滑限流, 启动后有一段预热期, 将分发频率提升到配置频率,适合系统刚启动需要时间热身的场景
     */
    public static void smoothWarmingUp() {
        // permitsPerSecond: 每秒新增的令牌数  warmupPeriod: 从冷启动速率过渡到平均速率的时间间隔
        // 系统冷启动后慢慢的趋于平均固定速率（即刚开始速率慢一些，然后慢慢趋于我们设置的固定速率） 预热时间是 3 s，3s之内达到设置
        RateLimiter r = RateLimiter.create(2, 3, TimeUnit.SECONDS);
        while (true) {
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("get 1 tokens: " + r.acquire(1) + "s");
            System.out.println("end");
            /**
             * output:
             * get 1 tokens: 0.0s
             * get 1 tokens: 1.329289s
             * get 1 tokens: 0.994375s
             * get 1 tokens: 0.662888s  上边三次获取的时间相加正好为3秒
             * end
             * get 1 tokens: 0.49764s  正常速率0.5秒一个令牌
             * get 1 tokens: 0.497828s
             * get 1 tokens: 0.49449s
             * get 1 tokens: 0.497522s
             */
        }
    }
}