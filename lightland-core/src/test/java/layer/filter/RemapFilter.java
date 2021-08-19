package layer.filter;

import java.lang.reflect.Array;
import java.util.function.Function;

public class RemapFilter<T, R> implements IFilter<T, R> {

    private final Class<R> cls;
    private final Function<T, R> func;

    public RemapFilter(Class<R> cls, Function<T, R> func) {
        this.cls = cls;
        this.func = func;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R[][] process(T[][] map) {
        int w = map.length;
        int h = map[0].length;
        R[][] ans = (R[][]) Array.newInstance(cls, w, h);
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                ans[j][k] = func.apply(map[j][k]);
            }
        }
        return ans;
    }
}
