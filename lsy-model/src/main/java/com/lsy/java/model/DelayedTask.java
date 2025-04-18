package com.lsy.java.model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DelayedTask<T> implements Delayed, Runnable {

    /**
     * 任务参数
     */
    private T taskParam;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 任务函数
     */
    private Function<T, String> function;

    /**
     * 任务执行时刻
     */
    private Long runAt;

    public T getTaskParam() {
        return taskParam;
    }

    public Integer getType() {
        return type;
    }

    public Function<T, String> getFunction() {
        return function;
    }

    public Long getRunAt() {
        return runAt;
    }

    public DelayedTask(T taskParam, Integer type, Function<T, String> function, Long runAt) {
        this.taskParam = taskParam;
        this.type = type;
        this.function = function;
        this.runAt = runAt;
    }

    @Override
    public void run() {
        if (taskParam != null) {
            function.apply(taskParam);
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.runAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        DelayedTask object = (DelayedTask) o;
        return this.runAt.compareTo(object.getRunAt());
    }
}
