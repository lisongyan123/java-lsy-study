package com.lsy.java.study.controller;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GuavaScheduledTaskExample {
    public static void main(String[] args) {
        // 创建一个 ScheduledExecutorService 实例
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        // 将 ScheduledExecutorService 装饰为 ListeningScheduledExecutorService
        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors.listeningDecorator(scheduledExecutorService);

        // 提交一个定时任务，延迟 1 秒后执行，之后每隔 2 秒执行一次
        ListenableFuture<?> future = listeningScheduledExecutorService.scheduleAtFixedRate(
                () -> System.out.println("定时任务执行了！"),
                1,
                2,
                TimeUnit.SECONDS
        );

        // 添加任务完成后的回调
        Futures.addCallback(future, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                // 任务成功完成时的回调
                System.out.println("任务成功完成！");
            }

            @Override
            public void onFailure(Throwable t) {
                // 任务失败时的回调
                System.out.println("任务执行失败: " + t.getMessage());
            }
        }, listeningScheduledExecutorService);

        // 为了防止主线程立即退出，让定时任务有时间执行
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 关闭线程池
        listeningScheduledExecutorService.shutdown();
    }
}
