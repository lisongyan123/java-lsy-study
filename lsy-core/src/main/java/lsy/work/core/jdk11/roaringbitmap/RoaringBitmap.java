package lsy.work.core.jdk11.roaringbitmap;

import org.roaringbitmap.longlong.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 解决数据去重的问题
 */
public class RoaringBitmap {
    public static void main(String[] args) {
        // Roaring64NavigableMap使用示例
        LongBitmapDataProvider r = Roaring64NavigableMap.bitmapOf(1, 2, 100, 1000);
        r.addLong(1234);
        System.out.println(r.contains(1)); // true
        System.out.println(r.contains(3)); // false
        LongIterator li = r.getLongIterator();
        getProcessID();
        while (li.hasNext()) {
            System.out.println(li.next());
        }

        // Roaring64Bitmap使用示例
        Roaring64Bitmap bitmap1 = new Roaring64Bitmap();
        Roaring64Bitmap bitmap2 = new Roaring64Bitmap();
        int k = 1 << 16;
        for (int i = 0; i < 10000; ++i) {
            bitmap1.add(i * k);
            bitmap2.add(i * k + 1);
        }
        bitmap1.or(bitmap2);
        getProcessID();
        System.out.println(bitmap1.getLongCardinality());
        bitmap1.forEachInRange(0, 10 * k, new LongConsumer() {
            @Override
            public void accept(long value) {
                System.out.println(value);
            }
        });
    }

    public static final int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getName());
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
    }
}
