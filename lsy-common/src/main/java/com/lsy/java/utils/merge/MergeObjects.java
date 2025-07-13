package com.lsy.java.utils.merge;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ObjectArrays;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeObjects<T> {
    public static void main(String[] args) {
        Float[] a = new Float[]{1.12F, 2.34F, 3.432F, 421.423F, 222.2F};
        Float[] b = new Float[]{1.123F, 2.343F, 3.431F, 421.41F};
        Float[] c = new Float[]{1.22F, 2.33F, 3.41F, 41.41F};
        List<Float[]> floats = new ArrayList<>() {{
            add(a);
            add(b);
            add(c);
        }};
        Float[][] aaa = new Float[floats.size()][b.length];
        Float[] parse = parse(aaa, floats);

        System.out.println(JSONObject.toJSON(parse));
    }

    public static Float[] parse(Float[][] aaa, List<Float[]> floats) {
        for (int i = 0; i < floats.size(); i++) {
            aaa[i] = floats.get(i);
        }
        Object[] concat = concat(aaa);
        Float[] floatArray = Convert.toFloatArray(concat);
        return floatArray;
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    static Object[] concat(Object[]... arrays) {
        int length = 0;
        for (Object[] array : arrays) {
            length += array.length;
        }
        Object[] result = new Object[length];
        int pos = 0;
        for (Object[] array : arrays) {
            for (Object element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

}
