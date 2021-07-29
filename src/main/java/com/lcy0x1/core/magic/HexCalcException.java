package com.lcy0x1.core.magic;

import java.util.ArrayList;
import java.util.Collection;

public class HexCalcException extends Exception {

    public static void trycatch(FlowChart chart) throws HexCalcException {
        HexCalcException ans = new HexCalcException();
        for (FlowChart.Flow f : chart.flows) {
            if (f.flawed())
                ans.error.add(new Side(f.arrow));
        }
        if (ans.error.size() != 0)
            throw ans;
    }

    public final Collection<Side> error = new ArrayList<>();

    private HexCalcException() {

    }

    public static class Side {

        public int row, cell;
        public HexDirection dir;

        private Side(ArrowResult arrow) {
            this.row = arrow.row;
            this.cell = arrow.cell;
            this.dir = arrow.dir;
        }

    }

}
