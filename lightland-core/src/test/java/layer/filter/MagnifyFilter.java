package layer.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MagnifyFilter<T> implements IFilter<T, T> {

    public enum Type {
        NOISE, SMOOTH
    }

    private final Random r;
    private final Type type;

    public MagnifyFilter(Random r, Type type) {
        this.r = r;
        this.type = type;
    }

    @Override
    public T[][] process(T[][] map) {
        int w = map.length;
        int h = map[0].length;
        T[][] ans = Arrays.copyOf(map, w * 2);
        for (int j = 0; j < w; j++) {
            ans[j * 2] = Arrays.copyOf(map[0], h * 2);
            ans[j * 2 + 1] = Arrays.copyOf(map[0], h * 2);
            for (int k = 0; k < h; k++) {
                int j1 = Math.min(w - 1, j + 1);
                int k1 = Math.min(h - 1, k + 1);
                ans[j * 2][k * 2] = map[j][k];
                ans[j * 2 + 1][k * 2] = IFilter.rand(r, map[j][k], map[j1][k]);
                ans[j * 2][k * 2 + 1] = IFilter.rand(r, map[j][k], map[j][k1]);
                ans[j * 2 + 1][k * 2 + 1] = max(map[j][k], map[j1][k], map[j][k1], map[j1][k1]);
            }
        }
        return ans;
    }

    @SafeVarargs
    private final T max(T... vals) {
        if (type == Type.NOISE) {
            return IFilter.rand(r, vals);
        }
        Map<T, Integer> count = new HashMap<>();
        for (T v : vals) {
            count.put(v, count.getOrDefault(v, 0) + 1);
        }
        return count.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

}
