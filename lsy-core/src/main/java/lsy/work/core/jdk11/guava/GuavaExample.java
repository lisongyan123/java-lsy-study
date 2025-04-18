package lsy.work.core.jdk11.guava;

import com.google.common.base.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Sets.newHashSet;

public class GuavaExample {
    public static void main(String[] args) throws ExecutionException {
        instantCollect();
        multiCollect();
        string();
        charMatcher();
        filter();
        doubleCollect();
        cache();
    }

    /**
     * 各种各样的集合
     */
    public static void instantCollect() {
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        HashMap<Object, Object> map = Maps.newHashMap();

        // 不可变集合 线程安全
        ImmutableList<String> iList = ImmutableList.of("a", "b", "c");
        ImmutableSet<String> iSet = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> iMap = ImmutableMap.of("k1", "v1", "k2", "v2");
    }

    /**
     * 可重复的map，map的key对应list
     * 可重复的set
     * 双向map,键值都不可以重复
     * 双肩的map sql联合主键
     */
    public static void multiCollect() {
        // 可重复的map
        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("aa", 1);
        map.put("aa", 2);
        System.out.println(map.get("aa"));

        // 可重复的set
        Multiset<String> set = HashMultiset.create();
        set.add("2");
        set.add("2");
        set.add("2");
        System.out.println(set.count("2"));

        // key和value都不能为空
        BiMap<String, String> biMap = HashBiMap.create();
        biMap.forcePut("a", "1");
        biMap.forcePut("b", "1");
        System.out.println(biMap.get("a"));

        // 双键的Map
        Table<String, String, Integer> tables = HashBasedTable.create();
        tables.put("a", "b", 1);
        System.out.println(tables.get("a", "d"));
    }

    /**
     * list转换为字符串
     * map 转换为字符串
     * 字符串转化为list，可以去空格
     * 字符串转化为list，可以去空格
     * string转化为map
     */
    public static void string() {
        // list转换为字符串
        List<String> list = new ArrayList<String>();
        list.add("北京市");
        list.add("丰台区");
        list.add("郭公庄地铁站");
        String result = Joiner.on("-").join(list);
        System.out.println(result);

        // map 转换为字符串
        Map<String, Integer> map = Maps.newHashMap();
        map.put("xiaoming", 12);
        map.put("xiaohong", 13);
        String mapResult = Joiner.on(",").withKeyValueSeparator("=").join(map);
        System.out.println(mapResult);

        // 字符串转化为list，可以去空格
        String str = "1-2-3-4-  5-  6   ";
        List<String> stringList = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(str);
        System.out.println(stringList);

        // string转化为map,注意这里去空格不生效，是因为split后的空格会被去除
        String s = "xiaoming: 11,xiaohong: 23";
        Map<String, String> split = Splitter.on(",").omitEmptyStrings().trimResults().withKeyValueSeparator(": ").split(s);
        System.out.println(split.toString());
    }

    /**
     * 正则匹配
     */
    public static void charMatcher() {
        boolean result = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z')).matches('K'); //true
        String s1 = CharMatcher.inRange('0', '9').retainFrom("abc 123 efg"); // 123
        String s2 = CharMatcher.inRange('0', '9').removeFrom("abc 123 efg"); // abc  efg

        System.out.println("result:" + result);
        System.out.println("s1:" + s1);
        System.out.println("s2:" + s2);
    }

    /**
     * 集合过滤，可以自定义过滤条件
     */
    public static void filter() {
        ImmutableList<String> names = ImmutableList.of("begin", "code", "Guava", "Java");
        Iterable<String> fitered = Iterables.filter(names, Predicates.or(Predicates.equalTo("Guava"), Predicates.equalTo("Java")));
        System.out.println(fitered);

        ImmutableMap<String, Integer> m = ImmutableMap.of("begin", 12, "code", 15);
        Map<String, Integer> m2 = Maps.transformValues(m,
                new Function<Integer, Integer>() {
                    public Integer apply(Integer input) {
                        if (input > 12) {
                            return input;
                        } else {
                            return input + 1;
                        }
                    }
                });
        System.out.println(m2);
    }

    /**
     * 交集、差集、并集
     */
    public static void doubleCollect() {
        HashSet setA = newHashSet(1, 2, 3, 4, 5);
        HashSet setB = newHashSet(4, 5, 6, 7, 8);
        // 并集
        Sets.SetView<Integer> union = Sets.union(setA, setB);
        System.out.println(union.toString());           //union 并集:12345867
        // 差集
        Sets.SetView<Integer> difference = Sets.difference(setA, setB);
        System.out.println(difference.toString());        //difference 差集:123
        // 交集
        Sets.SetView<Integer> intersection = Sets.intersection(setA, setB);
        System.out.println(intersection.toString());  //intersection 交集:45

        HashMap<String, Integer> mapA = Maps.newHashMap();
        mapA.put("a", 1);
        mapA.put("b", 2);
        mapA.put("c", 3);

        HashMap<String, Integer> mapB = Maps.newHashMap();
        mapB.put("b", 20);
        mapB.put("c", 3);
        mapB.put("d", 4);

        MapDifference differenceMap = Maps.difference(mapA, mapB);
        Map entriesDiffering = differenceMap.entriesDiffering();
        Map entriesOnlyLeft = differenceMap.entriesOnlyOnLeft();
        Map entriesOnlyRight = differenceMap.entriesOnlyOnRight();
        Map entriesInCommon = differenceMap.entriesInCommon();

        System.out.println(differenceMap.areEqual());
        System.out.println(entriesDiffering);   // {b=(2, 20)}
        System.out.println(entriesOnlyLeft);    // {a=1}
        System.out.println(entriesOnlyRight);   // {d=4}
        System.out.println(entriesInCommon);    // {c=3}
    }

    public static void cache() throws ExecutionException {
        LoadingCache<String, String> cahceBuilder = CacheBuilder
                .newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        String strProValue = "hello " + key + "!";
                        return strProValue;
                    }
                });
        System.out.println(cahceBuilder.apply("begincode"));  //hello begincode!
        System.out.println(cahceBuilder.get("begincode")); //hello begincode!
        System.out.println(cahceBuilder.get("wen")); //hello wen!
        System.out.println(cahceBuilder.apply("wen")); //hello wen!
        System.out.println(cahceBuilder.apply("da"));//hello da!
        cahceBuilder.put("begin", "code");
        System.out.println(cahceBuilder.get("begin")); //code


        Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
        String resultVal = cache.get("code", new Callable<String>() {
            public String call() {
                String strProValue = "begin " + "code" + "!";
                return strProValue;
            }
        });
        System.out.println("value : " + resultVal); //value : begin code!
    }
}
