package lsy.work.core.jdk11.guava.future;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListenableFutureTest {
    // 以下是线程池的性能测试：
    // Future的性能测试
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        jdkFuture();
        completableFutureTest();
        listenableFutureTest();
        testThread();
    }

    public static void testThread() {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        IntStream.range(0, 5).parallel().forEach(
                (v) -> test1()
        );
    }

    public static void jdkFuture() {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        ListenableFutureTest listenableFutureTest = new ListenableFutureTest();

        List<Future<String>> futureList = Arrays.asList(executor.submit(() -> ListenableFutureTest.test1()),
                executor.submit(() -> ListenableFutureTest.test2()),
                executor.submit(() -> ListenableFutureTest.test3()),
                executor.submit(() -> ListenableFutureTest.test4()));
        List<String> collect = futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        System.out.println("jdkFuture性能测试耗时：" + (System.currentTimeMillis() - startTime));
    }

    // CompletableFuture性能测试
    public static void completableFutureTest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        long startTime = System.currentTimeMillis();
        ListenableFutureTest listenableFutureTest = new ListenableFutureTest();
        List<CompletableFuture<String>> futures = new ArrayList<>(4);
        futures.add(CompletableFuture.supplyAsync(() -> ListenableFutureTest.test1(), executor));
        futures.add(CompletableFuture.supplyAsync(() -> ListenableFutureTest.test2(), executor));
        futures.add(CompletableFuture.supplyAsync(() -> ListenableFutureTest.test3(), executor));
        futures.add(CompletableFuture.supplyAsync(() -> ListenableFutureTest.test4(), executor));

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[4]));
        CompletableFuture<List<String>> listCompletableFuture = allFuture.thenApplyAsync(value -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()), executor);
        List<String> join = listCompletableFuture.join();

        System.out.println("CompletableFuture性能测试耗时：" + (System.currentTimeMillis() - startTime));
    }

    //  guava性能测试
    public static void listenableFutureTest() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        ListeningExecutorService guavaExecutor = MoreExecutors.listeningDecorator(executor);

        long startTime = System.currentTimeMillis();
        ListenableFutureTest listenableFutureTest = new ListenableFutureTest();
        ListenableFuture<String> lf1 = guavaExecutor.submit(() -> ListenableFutureTest.test1());
        ListenableFuture<String> lf2 = guavaExecutor.submit(() -> ListenableFutureTest.test2());
        ListenableFuture<String> lf3 = guavaExecutor.submit(() -> ListenableFutureTest.test3());
        ListenableFuture<String> lf4 = guavaExecutor.submit(() -> ListenableFutureTest.test4());
        ListenableFuture<List<String>> listListenableFuture = Futures.allAsList(new ListenableFuture[]{lf1, lf2, lf3, lf4});
        List<String> strings = listListenableFuture.get();
        System.out.println("guava性能测试耗时：" + (System.currentTimeMillis() - startTime));
    }

    public static String test1() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test1");
        return "test1";
    }

    public static String test2() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test2");
        return "test2";
    }

    public static String test3() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test3");
        return "test3";
    }

    public static String test4() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test4");
        return "test4";
    }
}
