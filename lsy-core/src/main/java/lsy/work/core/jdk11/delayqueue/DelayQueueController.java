package lsy.work.core.jdk11.delayqueue;


import com.lsy.java.model.DelayedTask;
import com.lsy.java.model.Student;

import java.util.concurrent.*;


/**
 * DelayQueue 是一个线程安全的无界阻塞（BlockingQueue） 队列，内部封装了一个 PriorityQueue(优先队列),
 * PriorityQueue 内部使用完全二叉堆来实现队列元素排序，我们在向 DelayQueue 队列中添加元素时，会给元素一个 Delay(延迟时间)作为排序条件，
 * 队列中最小的元素会优先放在队首。队列中的元素只有到了 Delay 时间才允许从队列中取出, 取出的时候 wait notify
 */

public class DelayQueueController {

    public static final int SIZE = 10;

    public static void main(String[] args) {
        DelayQueueController test = new DelayQueueController();
        //初始化线程池
        BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(10);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 10, TimeUnit.MILLISECONDS, arrayBlockingQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        DelayQueue<DelayedTask> delayTaskQueue = new DelayQueue<>();
        //模拟SIZE个延迟任务
        for (byte i = 0; i < SIZE; i++) {
            Long runAt = System.currentTimeMillis() + 1000 * i;
            String name = "Zhang_" + i;
            byte age = (byte) (10 + i);
            String gender = (i % 2 == 0 ? "male" : "female");
            Student student = new Student(name, age, gender);
            delayTaskQueue.put(new DelayedTask<Student>(student, 1, function -> test.print(student), runAt));
        }

        while (true) {
            if (delayTaskQueue.size() == 0) {
                break;
            }
            try {
                //从延迟队列中取值,如果没有对象过期则取到null
                DelayedTask delayedTask = delayTaskQueue.poll();
                if (delayedTask != null) {
                    threadPool.execute(delayedTask);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();
    }


    public String print(Object object) {
        System.out.println(Thread.currentThread().getName());
        String str = ">>>junit log>>>" + object.getClass().getSimpleName() + ":" + object.toString();
        System.out.println(str);
        return str;
    }
}

