package lsy.work.core.jdk11.completablefuture;


import com.lsy.java.model.ResultModel;
import org.assertj.core.util.Lists;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 难题在于AllOf是void类型
 */
public class CompletableFutureAllOF {
    // 如果allOf中的所有CompletableFuture都返回的是同一个类型的结果，例如String，怎么让allOf直接返回List<String>
    public static <T> CompletableFuture<List<T>> allOf(Collection<CompletableFuture<T>> futures) {
        return futures.stream().collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        l -> CompletableFuture.allOf(l.toArray(new CompletableFuture[0]))
                                .thenApply(v -> l.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                )
        );
    }

    public static void main(String[] args) {
        CompletableFuture completableFuture = new CompletableFuture();
        baseOnCallBack(completableFuture);
        completableFuture.thenAcceptAsync(result -> {
            System.out.println("baseOnCallback: " + result);
        });
        // 没有join不返回值
    }

    /**
     * 基于回调
     *
     * @return
     */
    public static void baseOnCallBack(CompletableFuture<ResultModel> resultCompletableFuture) {
        CompletableFuture<List<String>> result1 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList("a", "b", "c");
        });
        CompletableFuture<List<Integer>> result2 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList(1, 2, 3);
        });
        CompletableFuture<String> result3 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hash-test";
        });

        CompletableFuture.allOf(result1, result2, result3).thenAcceptAsync(v -> {
            resultCompletableFuture.complete(ResultModel.builder()
                    //一定存在的，因为已经完成了
                    .string(result3.join())
                    .strings(result1.join())
                    .integers(result2.join())
                    .build());
        });
    }


    /**
     * 基于返回
     *
     * @return
     */
    public static CompletableFuture<ResultModel> baseOnReturn() {
        CompletableFuture completableFuture = new CompletableFuture();
        CompletableFuture<List<String>> result1 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList("a", "b", "c");
        });
        CompletableFuture<List<Integer>> result2 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Lists.newArrayList(1, 2, 3);
        });
        CompletableFuture<String> result3 = CompletableFuture.supplyAsync(() -> {
            //模拟io
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hash-test";
        });
        CompletableFuture.allOf(result1, result2, result3).thenAcceptAsync(v -> {
            completableFuture.complete(ResultModel.builder()
                    //一定存在的，因为已经完成了
                    .string(result3.join())
                    .strings(result1.join())
                    .integers(result2.join())
                    .build());
        });
        return completableFuture;
    }

}
