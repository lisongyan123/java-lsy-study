package com.lsy.java.study.controller;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Operation(summary = "Get a greeting message", description = "Returns a simple greeting message")
    @GetMapping("/hello")
    public String sayHello(@Parameter(description = "Name of the person to greet") @RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info(String.format("Hello, %s!", name));
        return String.format("Hello, %s!", name);
    }

    ThreadLocal<Map<String, Object>> transmittableThreadLocal = new TransmittableThreadLocal<>();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());


    private static TransmittableThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 在主线程中设置TransmittableThreadLocal的值
        threadLocal.set("Hello, World!");

        // 使用TtlExecutors包装线程池
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(executorService);

        // 提交任务到线程池中
        ttlExecutorService.submit(() -> {
            String value = threadLocal.get();
            System.out.println("Task is running in thread: " + Thread.currentThread().getName() + ", TransmittableThreadLocal value: " + value);
        });

        // 关闭线程池
        ttlExecutorService.shutdown();
    }

}