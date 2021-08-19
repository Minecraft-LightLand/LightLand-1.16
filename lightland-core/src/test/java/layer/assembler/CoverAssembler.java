package layer.assembler;

import java.util.Arrays;

public class CoverAssembler<T> implements IAssembler<T, Boolean> {

    private final T color;

    public CoverAssembler(T color) {
        this.color = color;
    }

    @Override
    public T[][] assemble(T[][] base, Boolean[][] layer) {
        int w = base.length;
        int h = base[0].length;
        T[][] ans = Arrays.copyOf(base, w);
        for (int i = 0; i < w; i++) {
            ans[i] = Arrays.copyOf(base[i], h);
            for (int j = 0; j < h; j++) {
                if (layer[i][j]) {
                    ans[i][j] = color;
                }
            }
        }
        return ans;
    }

}
