package lsy.work.core.jdk11.treemap;

import java.util.TreeMap;

public class TreeMapDeload {
    public static <K, V> V getNumedDataFromTreeMap(TreeMap<K, V> treeMap, int size) {
        V v = (V) treeMap.navigableKeySet().stream().skip(treeMap.size() - size).findFirst().get();

        return treeMap.get(v);
    }


    public static void main(String[] args) {
        // 创建一个 TreeMap
        TreeMap<String, String> treeMap = new TreeMap<>();
        // 添加元素到 TreeMap
        treeMap.put("10_A", "A");
        treeMap.put("10_B", "B");
        treeMap.put("10_C", "C");
        treeMap.put("10_D", "D");
        // 获取第三大的元素（如果存在）
        if (treeMap.size() >= 3) {
            String thirdKey = getNumedDataFromTreeMap(treeMap, 3);
            System.out.println("第三大的元素为：" + thirdKey);
        } else {
            System.out.println("不足三个元素无法确定第三大的元素！");
        }
    }
}
