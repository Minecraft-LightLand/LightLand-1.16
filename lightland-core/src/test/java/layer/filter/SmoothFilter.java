package layer.filter;

import java.util.Arrays;
import java.util.Random;

public class SmoothFilter<T> implements IFilter<T, T> {

    public enum Type {
        ALL, SIDE;
    }

    private final Random r;
    private final Type type;

    public SmoothFilter(Random r, Type type) {
        this.r = r;
        this.type = type;
    }

    @Override
    public T[][] process(T[][] map) {
        int w = map.length;
        int h = map[0].length;
        T[][] ans = Arrays.copyOf(map, w);
        for (int j = 0; j < w; j++) {
            ans[j] = Arrays.copyOf(map[j], h);
            for (int k = 0; k < h; k++) {
                int j0 = Math.max(0, j - 1);
                int k0 = Math.max(0, k - 1);
                int j1 = Math.min(w - 1, j + 1);
                int k1 = Math.min(h - 1, k + 1);
                boolean b0 = map[j][k0] == map[j][k1];
                boolean b1 = map[j0][k] == map[j1][k];
                boolean bv = map[j0][k] == map[j][k0];
                if (type == Type.ALL) {
                    if (b0 && b1 && bv) {
                        map[j][k] = map[j0][k];
                    }
                } else if (type == Type.SIDE) {
                    if (b0 && !b1) {
                        map[j][k] = map[j][k0];
                    } else if (!b0 && b1) {
                        map[j][k] = map[j0][k];
                    } else {
                        map[j][k] = IFilter.rand(r, map[j][k0], map[j][k1], map[j0][k], map[j0][k1]);
                    }
                }
            }
        }
        return ans;
    }
}
