package layer.filter;

import java.util.Random;

public interface IFilter<T, R> {

    R[][] process(T[][] map);

    @SafeVarargs
    static <T> T rand(Random r, T... vals) {
        int c = r.nextInt(vals.length);
        return vals[c];
    }

}
