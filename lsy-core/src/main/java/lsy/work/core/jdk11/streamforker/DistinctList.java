package lsy.work.core.jdk11.streamforker;

import com.lsy.java.model.Dish;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DistinctList {
    static List<Dish> list = Dish.menu;

    public static void main(String[] args) {
        // 查询数据

//        putMap(list, Dish::getName);
        distinct(list);

    }

    public static <T, R> void putMap(List<R> list, Supplier<T> supplier) {
        Map<T, R> nameMap = list.stream().collect(HashMap::new, (m, o) -> m.put(supplier.get(), o), HashMap::putAll);
        System.out.println(nameMap);
    }


    public static void distinct(List<Dish> list) {
        Object ArrayList;
        list = list.stream()
                .filter(o -> o.getName() != null)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(
                                        () -> new TreeSet<>(Comparator.comparing(Dish::getName))), ArrayList::new)
                );
        System.out.println(list.toString());
    }


}
