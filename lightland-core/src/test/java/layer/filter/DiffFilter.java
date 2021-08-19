package layer.filter;

public class DiffFilter<T> implements IFilter<T, Boolean> {

    private final BiPred<T> pred;

    public DiffFilter(BiPred<T> pred) {
        this.pred = pred;
    }

    @Override
    public Boolean[][] process(T[][] map) {
        int w = map.length;
        int h = map[0].length;
        Boolean[][] ans = new Boolean[w][h];
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                int j0 = Math.max(0, j - 1);
                int k0 = Math.max(0, k - 1);
                int j1 = Math.min(w - 1, j + 1);
                int k1 = Math.min(h - 1, k + 1);
                boolean b0 = pred.isDiff(map[j][k], map[j][k0]);
                boolean b1 = pred.isDiff(map[j][k], map[j][k1]);
                boolean b2 = pred.isDiff(map[j][k], map[j0][k]);
                boolean b3 = pred.isDiff(map[j][k], map[j1][k]);
                ans[j][k] = b0 || b1 || b2 || b3;
            }
        }
        return ans;
    }

    public interface BiPred<T> {

        boolean isDiff(T a, T b);

    }

}
