package com.lcy0x1.core.magic;

import java.util.*;

public class HexCalcException extends Exception {

    public final Collection<Side> error = new ArrayList<>();

    public HexCalcException(HexCalc.Arrow arrow) {
        Queue<HexCalc.CalcCell> pre = new ArrayDeque<>();
        Set<HexCalc.CalcCell> post = new HashSet<>();
        pre.add(arrow.dst);
        post.add(arrow.dst);
        while (!pre.isEmpty()) {
            HexCalc.CalcCell first = pre.poll();
            for (HexCalc.Arrow a : first.output)
                if (a != null) {
                    //pre.add(a.dst);//FIXME
                    post.add(a.dst);
                    error.add(new Side(first, a));
                }
        }
    }

    public static class Side {

        public int row, cell;
        public HexDirection dir;

        private Side(HexCalc.CalcCell cell, HexCalc.Arrow arrow) {
            this.row = cell.row;
            this.cell = cell.cell;
            this.dir = arrow.dir;
        }

    }

}
