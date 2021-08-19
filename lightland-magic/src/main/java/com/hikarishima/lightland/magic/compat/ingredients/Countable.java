package com.hikarishima.lightland.magic.compat.ingredients;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Countable {

    public int count = 1;

    public static <T extends Countable> List<T> collect(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        LinkedHashMap<T, T> set = new LinkedHashMap<>();
        for (T t : list) {
            if (set.containsKey(t))
                set.get(t).count += t.count;
            else set.put(t, t);
        }
        return new ArrayList<>(set.values());
    }

}
