package lsy.work.core.jdk11.vavr;

import io.vavr.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VavrTest {
    public static void main(String[] args) throws InterruptedException {
        Function2<Integer, Integer, Option<Integer>> safeSum = Function2.lift((v1, v2) -> sum(v1, v2));
        final Option<Integer> i1 = safeSum.apply(-1, -2);
        final Option<Integer> i2 = safeSum.apply(1, 2);
        System.out.println(i1);
        System.out.println(i2);

        function0();
        list();
        option();
        lazy();
        future();
        tryTest();
    }
//
//    public static void withoutElse() {
//        int i = 1;
//        String s = Match(i).of(
//                Case($(1), "one"), //等值匹配
//                Case($(2), "two"),
//                Case($(), "?")
//        );
//        String b = Match(i).of(
//                Case($(is(1)), "one"), //谓词表达式匹配
//                Case($(is(2)), "two"),
//                Case($(), "?")
//        );
//        String arg = "-h";
//        Match(arg).of(
//                Case($(isIn("-h", "--help")), o -> run(this::displayHelp)), //支持在成功匹配后执行动作
//                Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
//                Case($(), o -> run(() -> {
//                    throw new IllegalArgumentException(arg);
//                }))
//        );
//        A obj = new A();
//        Number plusOne = Match(obj).of(
//                Case($(instanceOf(Integer.class)), i -> i + 1), //根据值类型进行匹配
//                Case($(instanceOf(Double.class)), d -> d + 1),
//                Case($(), o -> {
//                    throw new NumberFormatException();
//                })
//        );
//
//        Tuple2 tuple2 = Tuple("a", 2);
//        Try<Tuple2<String, Integer>> _try = Try.success(tuple2);
//        Match(_try).of(
//                Case($Success($Tuple2($("a"), $())), tuple22 -> {
//                }),
//                Case($Failure($(instanceOf(Error.class))), error -> error.fillInStackTrace())
//        );
//
//        Option option = Option.some(1);
//        Match(option).of(
//                Case($Some($()), "defined"),
//                Case($None(), "empty")
//        );
//    }

    public static void future() {
        System.out.println("当前线程名称：" + Thread.currentThread().getName());
        Integer result = Future.of(() -> {
                    System.out.println("future线程名称：" + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    return 100;
                })
                .map(i -> i * 10)
                .await(3000, TimeUnit.MILLISECONDS) //等待线程执行3000毫秒
                .onFailure(e -> e.printStackTrace())
                .getValue() //返回Optional<Try<Integer>>类型结果
                .getOrElse(Try.of(() -> 100)) //如果Option 为 empty时，则返回Try(100)
                .get();
        System.out.println(result); // 1000
    }

    public static void lazy() {
        Lazy<Double> lazy = Lazy.of(Math::random)
                .map(i -> {
                    System.out.println("-----正在进行计算，此处只会执行一次------");
                    return i * 100;
                });
        System.out.println(lazy.isEvaluated());
        System.out.println(lazy.get()); //触发计算
        System.out.println(lazy.isEvaluated());
        System.out.println(lazy.get());//不会重新计算，返回上次结果
    }

    public static void tryTest() {
        Try result = Try.of(() -> 0)
                .map((a) -> 10 / a) //即使此处抛出异常，不会导致当前线程结束。这里无需使用 try{}catch()对代码进行捕获
                .andThen(() -> System.out.printf("--抛出异常此处不会执行--")) //执行一个动作，不修改结果
                .map(i -> {
                    System.out.println("当前值：" + i);
                    return i + 10;
                })
                .onFailure(e -> e.printStackTrace())//失败时会触发onFailure
                .recover(ArithmeticException.class, 1000) //如果遇到 Exception类型的异常,则返回1000
                .map((a) -> a + 1);

        System.out.println("是否抛出异常：" + result.isFailure());
        System.out.println("执行结果:" + result.getOrElse(100)); //如果有异常发生，则返回100
    }

    public static void list() {
        java.util.List<String> otherList = new ArrayList<>();
        java.util.List<String> list = Collections.unmodifiableList(otherList);

        //通过 map实现对数据的修改
        List<String> listNew = list.parallelStream().map(i -> i + "--").collect(Collectors.toList());
        System.out.println(listNew);

        //指定元素数量
        io.vavr.collection.List<Integer> list1 = io.vavr.collection.List.of(1, 2, 3, 4);
        //循环10次填充1
        io.vavr.collection.List list2 = io.vavr.collection.List.fill(10, 1);
        //填充1-> 100 的数据
        io.vavr.collection.List list3 = io.vavr.collection.List.range(1, 100);
        //指定步长为20
        io.vavr.collection.List list4 = io.vavr.collection.List.rangeBy(1, 100, 20);
        // vavr List 转换为 java list
        java.util.List<String> list5 = list4.asJava();
//        list5.add("A"); //不可变list，无法添加元素
        //转变为可变list,
        java.util.List<String> list6 = list4.asJavaMutable();
        list6.add("A");
        int a = list1.map(i -> (i * 10)).flatMap(i -> io.vavr.collection.List.fill(i, i)).foldLeft(0, (m, n) -> m + n); // 3000
        System.out.println("a + " + a);
    }

    public static void option() {
        //java 的 Optional
        Optional<String> javaMaybeFoo = Optional.of("foo");
        Optional<String> javaMaybeFooBar = javaMaybeFoo.map(s -> (String) null)
                .map(s -> s.toUpperCase() + "bar");
        System.out.println(javaMaybeFooBar); //Optional.empty

        //vavr 同样支持 Option,这里的Option和Scala的Option几乎一模一样
        Option<String> maybeFoo = Option.of("foo");
        Option<String> maybeFooBar = maybeFoo.flatMap(s -> Option.of((String) null))
                .map(s -> s.toUpperCase() + "bar");
        System.out.println(maybeFooBar); //None
    }

    /**
     * 记忆化
     *
     * @throws InterruptedException
     */
    public static void function0() throws InterruptedException {
        final Function0<Long> time = Function0.of(System::currentTimeMillis).memoized();
        TimeUnit.SECONDS.sleep(2);
        final Function0<Long> time2 = Function0.of(System::currentTimeMillis).memoized();
        System.out.println(time.get());
        System.out.println(time2.get());
    }

    /**
     * 柯里化
     */
    public void function3() {
        Function3<Integer, Integer, Integer, String> add = (a, b, c) -> a + 2 * b + c * c + "";
        final Function1<Integer, Function1<Integer, String>> apply1 = add.curried().apply(1);
        final Function1<Integer, String> apply2 = apply1.apply(2);
        final String apply3 = apply2.apply(3);
        System.out.println(apply3);
    }

    public void function2() {
        Function3<Integer, Integer, Integer, String> add = (a, b, c) -> a + 2 * b + c * c + "";
        final Function2<Integer, Integer, String> p1 = add.apply(1);
        final Function1<Integer, String> p2 = p1.apply(2);
        final String apply = p2.apply(3);
        System.out.println(apply);
    }


    private static int sum(int first, int second) {
        if (first < 0 || second < 0) {
            throw new IllegalArgumentException("Only positive integers are allowed");
        }
        return first + second;
    }
}
