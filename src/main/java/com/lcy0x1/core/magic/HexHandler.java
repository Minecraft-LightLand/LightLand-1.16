package com.lcy0x1.core.magic;

import com.lcy0x1.core.math.Frac;
import com.lcy0x1.core.util.NBTList;
import com.lcy0x1.core.util.NBTObj;

import java.util.*;
import java.util.Map.Entry;

public class HexHandler {

    public static class ArrowResult extends LocateResult {

        public static LocateResult get(int row, int cell, Direction dir, HexHandler hex) {
            if (row < 0 || row >= hex.getRowCount())
                return null;
            if (cell < 0 || cell >= hex.getCellCount(row))
                return null;
            int dr = dir.getRowOffset(hex.radius, row, cell);
            int dc = dir.getCellOffset(hex.radius, row, cell);
            if (row + dr < 0 || row + dr >= hex.getRowCount())
                return null;
            if (cell + dc < 0 || cell + dc >= hex.getCellCount(row))
                return null;
            return new ArrowResult(row, cell, dir, hex);
        }

        public final int row, cell;
        public final Direction dir;

        private final HexHandler hex;

        ArrowResult(int row, int cell, Direction dir, HexHandler hex) {
            this.row = row;
            this.cell = cell;
            this.dir = dir;
            this.hex = hex;
        }

        public boolean equals(LocateResult loc) {
            if (loc instanceof ArrowResult) {
                ArrowResult arr = (ArrowResult) loc;
                return arr.row == row && arr.cell == cell && arr.dir == dir;
            }
            return false;
        }

        @Override
        public ResultType getType() {
            return ResultType.ARROW;
        }

        @Override
        public int getX() {
            int dr = dir.getRowOffset(hex.radius, row, cell);
            int dc = dir.getCellOffset(hex.radius, row, cell);
            int x0 = hex.getX(row, cell);
            int x1 = hex.getX(row + dr, cell + dc);
            return (x0 + x1) / 2;
        }

        @Override
        public int getY() {
            int dr = dir.getRowOffset(hex.radius, row, cell);
            int dc = dir.getCellOffset(hex.radius, row, cell);
            int y0 = hex.getY(row, cell);
            int y1 = hex.getY(row + dr, cell + dc);
            return (y0 + y1) / 2;
        }

        @Override
        public String toString() {
            return "(" + row + "," + cell + ") to " + dir.ind;
        }

    }

    public class Cell {

        public int row, cell;

        public Cell(int row, int cell) {
            this.row = row;
            this.cell = cell;
        }

        public boolean canWalk(Direction dir) {
            int dr = dir.getRowOffset(radius, row, cell);
            int dc = dir.getCellOffset(radius, row, cell);
            return row + dr >= 0 && row + dr < getRowCount() && cell + dc >= 0 && cell + dc < getCellCount(row + dr);
        }

        public boolean exists() {
            return cells[row][cell] > 0;
        }

        public SubHex getSubHex() {
            return subhex[getInd(row, cell)];
        }

        public int getX() {
            return HexHandler.this.getX(row, cell);
        }

        public int getY() {
            return HexHandler.this.getY(row, cell);
        }

        public boolean isConnected(Direction dir) {
            return (cells[row][cell] & dir.mask()) != 0;
        }

        public boolean isCorner() {
            return row % radius == 0 && (cell == 0 || cell == cells[row].length - 1);
        }

        /**
         * ans[i][j] means input at i will add ans[i][j] to output at j
         */
        public Frac[][] matrix() throws HexException {
            if (subhex[getInd(row, cell)] != null)
                return subhex[getInd(row, cell)].getMatrix();
            Frac[][] ans = new Frac[6][];
            for (int i = 0; i < 6; i++) {
                Direction dir = Direction.values()[i];
                if (isConnected(dir)) {
                    ans[i] = new Frac[6];
                    Direction opo = dir.next(3);
                    if (isConnected(opo)) {
                        ans[i][opo.ind] = new Frac(1, 1);
                        continue;
                    }
                    Direction ccw = dir.next(2);
                    Direction cw = dir.next(4);
                    boolean bc0 = isConnected(ccw);
                    boolean bc1 = isConnected(cw);
                    if (bc0 || bc1) {
                        if (bc0 && bc1)
                            ans[i][ccw.ind] = ans[i][cw.ind] = new Frac(1, 2);
                        else if (bc0)
                            ans[i][ccw.ind] = new Frac(1, 1);
                        else
                            ans[i][cw.ind] = new Frac(1, 1);
                        continue;
                    }
                    Direction bccw = dir.next(1);
                    Direction bcw = dir.next(5);
                    bc0 = isConnected(bccw);
                    bc1 = isConnected(bcw);
                    if (bc0 || bc1) {
                        if (bc0 && bc1)
                            ans[i][bccw.ind] = ans[i][bcw.ind] = new Frac(1, 2);
                        else if (bc0)
                            ans[i][bccw.ind] = new Frac(1, 1);
                        else
                            ans[i][bcw.ind] = new Frac(1, 1);
                        continue;
                    }
                    ans[i][i] = new Frac(1, 1);
                }
            }
            return ans;
        }

        public void set(SubHexCore sub, int i, boolean b) throws HexException {
            subhex[getInd(row, cell)] = new SubHex(sub, i, b);
        }

        /**
         * no effect if the operation is out of bound
         */
        public void toggle(Direction dir) {
            if (!canWalk(dir))
                return;
            int val = cells[row][cell];
            if (isCorner() && val != 0 && val != dir.mask()) {
                cells[row][cell] = 0;
                for (Direction d : Direction.values())
                    if ((val & dir.mask()) != 0) {
                        int dr = d.getRowOffset(radius, row, cell);
                        int dc = d.getCellOffset(radius, row, cell);
                        cells[row + dr][cell + dc] ^= d.next(3).mask();
                    }
            }
            int dr = dir.getRowOffset(radius, row, cell);
            int dc = dir.getCellOffset(radius, row, cell);
            cells[row][cell] ^= dir.mask();
            cells[row + dr][cell + dc] ^= dir.next(3).mask();
        }

        public void walk(Direction dir) {
            if (!canWalk(dir))
                return;
            int dr = dir.getRowOffset(radius, row, cell);
            int dc = dir.getCellOffset(radius, row, cell);
            row += dr;
            cell += dc;
        }

        public void walk(Direction dir, int n) {
            for (int i = 0; i < n; i++)
                walk(dir);
        }

        boolean isInvalid() {
            for (Direction dir : Direction.values()) {
                if (canWalk(dir)) {
                    int dr = dir.getRowOffset(radius, row, cell);
                    int dc = dir.getCellOffset(radius, row, cell);
                    boolean b0 = (cells[row][cell] & dir.mask()) != 0;
                    boolean b1 = (cells[row + dr][cell + dc] & dir.next(3).mask()) != 0;
                    if (b0 != b1)
                        return true;
                } else if ((cells[row][cell] & dir.mask()) != 0)
                    return true;
            }
            SubHex sub = subhex[getInd(row, cell)];
            return sub != null && sub.isInvalid(this) != 0;
        }

    }

    public static class CellResult extends LocateResult {

        public static CellResult get(int row, int cell, HexHandler hex) {
            if (row < 0 || row >= hex.getRowCount())
                return null;
            if (cell < 0 || cell >= hex.getCellCount(row))
                return null;
            return new CellResult(row, cell, hex);
        }

        public final int row, cell;

        private final HexHandler hex;

        CellResult(int row, int cell, HexHandler hex) {
            this.row = row;
            this.cell = cell;
            this.hex = hex;
        }

        @Override
        public ResultType getType() {
            return ResultType.CELL;
        }

        @Override
        public int getX() {
            return hex.getX(row, cell);
        }

        @Override
        public int getY() {
            return hex.getY(row, cell);
        }

    }

    public enum Direction {
        /**
         * the right direction of a cell, with an offset of (1,0)
         */
        RIGHT(0, 0, 1, 1),
        /**
         * the lower right direction of a cell, with an offset of (0.5,1.732)
         */
        LOWER_RIGHT(1, 1, 1, 0),
        /**
         * the lower left direction of a cell, with an offset of (-0.5,1.732)
         */
        LOWER_LEFT(2, 1, 0, -1),
        /**
         * the left direction of a cell, with an offset of (-1,0)
         */
        LEFT(3, 0, -1, -1),
        /**
         * the upper left direction of a cell, with an offset of (-0.5,-1.732)
         */
        UPPER_LEFT(4, -1, -1, 0),
        /**
         * the upper right direction of a cell, with an offset of (0.5,-1.732)
         */
        UPPER_RIGHT(5, -1, 0, 1);

        public final int ind;
        private final int dr, dc0, dc1;

        Direction(int ind, int dr, int dc0, int dc1) {
            this.ind = ind;
            this.dr = dr;
            this.dc0 = dc0;
            this.dc1 = dc1;
        }

        /**
         * get the cell offset of this direction
         *
         * @param radius the origin of the row axis, can be relative
         * @param row    the row coordinate
         * @param cell   the cell coordinate
         */
        public int getCellOffset(int radius, int row, int cell) {
            // if the path is above the central line, use dc0
            // if the path is below the central line, use dc1
            // for the central row, upper paths are above the central line
            // lower paths are below the central line
            return radius > row ? dc0 : radius < row ? dc1 : dr < 0 ? dc0 : dc1;
        }

        /**
         * get the row offset of this direction
         */
        public int getRowOffset(int radius, int row, int cell) {
            return dr;
        }

        public byte mask() {
            return (byte) (1 << ind);
        }

        /**
         * the next direction, positive to be clockwise
         */
        public Direction next(int next) {
            if (next < 0)
                next += (-next / 6 + 1) * 6;
            return values()[(ind + next) % 6];
        }
    }

    /**
     * the summary of the flow in a diagram <br>
     * each {@code Frac[]} represents the output formula <br>
     * each {@code Frac} represents the coefficient of each input <br>
     * the order is the same as direction: starts from the right, clockwise <br>
     * <hr>
     * {@code matrix} is the output flow in the diagram <br>
     * {@code flows} is the intermediate flow in the diagram
     */
    public class FlowChart {

        public class Flow {
            public final ArrowResult arrow;
            public Frac[] forward, backward;

            Flow(ArrowResult arrow) {
                flows.add(this);
                this.arrow = arrow;
            }
        }

        public final List<Flow> flows = new ArrayList<Flow>();

        public final Frac[][] matrix;

        public FlowChart(Frac[][] matrix) {
            this.matrix = matrix;
        }

    }

    public static class HexException extends Exception {

        private static final long serialVersionUID = 1L;

        public HexException(Calc.Arrow arrow) {
            super("full loop at " + arrow.toString());
        }

        public HexException(String str) {
            super(str);
        }

    }

    public static abstract class LocateResult {

        public enum ResultType {
            CELL, ARROW
        }

        public abstract ResultType getType();

        public abstract int getX();

        public abstract int getY();

    }

    public static class SubHex {

        public final SubHexCore core;
        public int rotation;
        public boolean flip;

        public SubHex(SubHexCore core, int rotation, boolean flip) {
            this.core = core;
            this.rotation = (rotation % 6 + 6) % 6;
            this.flip = flip;
        }

        public Frac[][] getMatrix() {
            Frac[][] ans = new Frac[6][];
            for (int i = 0; i < 6; i++) {
                int ni = ((flip ? 6 - i : i) + rotation) % 6;
                for (int j = 0; j < 6; j++) {
                    int nj = ((flip ? 6 - j : j) + rotation) % 6;
                    if (core.otho[i][j] != null) {
                        if (ans[nj] == null)
                            ans[nj] = new Frac[6];
                        ans[nj][ni] = core.otho[i][j];
                    }
                }
            }
            return ans;
        }

        /**
         * invalid sides: bit mask
         */
        public int isInvalid(Cell c) {
            int ans = 0;
            for (int i = 0; i < 6; i++) {
                int ni = ((flip ? 6 - i : i) + rotation) % 6;
                Direction dir = Direction.values()[ni];
                boolean set = (core.exist & 1 << i) != 0;
                if (!c.canWalk(dir)) {
                    if (set)
                        ans |= 1 << ni;
                    continue;
                }
                if (c.isConnected(dir) != set)
                    return ans |= 1 << ni;
            }
            return ans;
        }

    }

    public class SubHexCore {

        public final HexHandler hex;
        public final Frac[][] otho;
        public final int exist, index;

        public SubHexCore(HexHandler hex) throws HexException {
            this.hex = hex;
            this.otho = hex.getMatrix(false).matrix;
            int exi = 0;
            for (int i = 0; i < 6; i++) {
                Direction dir = Direction.values()[i];
                int dr = dir.getRowOffset(hex.radius, hex.radius, hex.radius);
                int dc = dir.getCellOffset(hex.radius, hex.radius, hex.radius);
                int r = (dr + 1) * hex.radius;
                int c = (dc + 1) * hex.radius;
                if (hex.cells[r][c] != 0)
                    exi |= 1 << i;
            }
            this.exist = exi;
            for (int i = 0; i < cores.length; i++)
                if (cores[i] == null) {
                    cores[i] = this;
                    this.index = i;
                    return;
                }
            throw new HexException("no core space");
        }
    }

    /**
     * calculation utility
     */
    private class Calc {

        /**
         * represents a flow from src to dst
         */
        private class Arrow {

            CalcCell src, dst;
            Direction dir;
            final Map<Arrow, Frac> map = new HashMap<>();
            final Set<Arrow> user = new HashSet<>();

            int rely = 0;

            Arrow(CalcCell src, Direction dir) {
                this.src = src;
                this.dir = dir;
            }

            @Override
            public String toString() {
                return "from " + src + " to " + dst;
            }

            void clear() throws HexException {
                Frac frac = map.remove(this);
                user.remove(this);
                if (frac == null)
                    return;
                if (frac.den == frac.num)
                    throw new HexException(this);
                Frac base = new Frac(frac.den, frac.den - frac.num);
                for (Frac f : map.values())
                    f.times(base);

            }

            void put(Arrow var, Frac frac) throws HexException {
                if (map.containsKey(var))
                    frac.add(map.get(var));
                map.put(var, frac);
                var.user.add(this);
            }

            void remove() throws HexException {
                clear();
                for (Arrow v : user) {
                    Frac base = v.map.remove(this);
                    for (Entry<Arrow, Frac> ent : map.entrySet())
                        v.put(ent.getKey(), Frac.mult(base, ent.getValue()));
                    v.rely--;
                    if (v.rely == 0)
                        head.add(v);
                }
                for (Arrow v : map.keySet())
                    v.user.remove(this);
            }

        }

        /**
         * represents a cell during calculation
         */
        private class CalcCell extends Cell {

            boolean origin;
            Arrow[] input, output;

            CalcCell(int row, int cell) {
                super(row, cell);
                output = new Arrow[6];
                for (int i = 0; i < 6; i++) {
                    Direction dir = Direction.values()[i];
                    if (isConnected(dir))
                        output[i] = new Arrow(this, dir);
                }
            }

            @Override
            public String toString() {
                return (origin ? "origin " : "") + "(r = " + row + ", c = " + cell + ")";
            }

            void init() throws HexException {
                for (Arrow a : output)
                    if (a != null)
                        pool.add(a);
                input = new Arrow[6];
                Frac[][] vars = this.matrix();
                for (int i = 0; i < 6; i++) {
                    Direction dir = Direction.values()[i];
                    if (vars[i] != null) {
                        input[i] = neighbor(dir).output[dir.next(3).ind];
                        input[i].dst = this;
                        if (!origin)
                            for (int j = 0; j < 6; j++)
                                if (vars[i][j] != null)
                                    output[j].put(input[i], vars[i][j]);
                    }
                }
            }

            CalcCell neighbor(Direction dir) {
                if (!canWalk(dir))
                    return null;
                int nr = dir.getRowOffset(radius, row, cell);
                int nc = dir.getCellOffset(radius, row, cell);
                return ccell[row + nr][cell + nc];
            }

            void remove() throws HexException {
                if (origin)
                    return;
                for (Arrow v : output)
                    if (v != null && !v.dst.origin)
                        v.remove();
            }

        }

        final CalcCell[][] ccell;
        final List<CalcCell> list;
        final Set<Arrow> pool = new HashSet<>();

        private Arrow[] in;
        private Map<CalcCell, Map<Direction, FlowChart.Flow>> flowmap;

        Queue<Arrow> head = new ArrayDeque<>();

        public Calc() throws HexException {
            ccell = new CalcCell[cells.length][];
            boolean[][] used = new boolean[cells.length][];
            for (int i = 0; i < ccell.length; i++) {
                ccell[i] = new CalcCell[cells[i].length];
                used[i] = new boolean[cells[i].length];
                for (int j = 0; j < ccell[i].length; j++)
                    ccell[i][j] = new CalcCell(i, j);
            }

            list = new ArrayList<>();
            list.add(ccell[radius][getCellCount(radius) - 1]);
            list.add(ccell[radius * 2][getCellCount(radius * 2) - 1]);
            list.add(ccell[radius * 2][0]);
            list.add(ccell[radius][0]);
            list.add(ccell[0][0]);
            list.add(ccell[0][getCellCount(0) - 1]);
            for (CalcCell cc : list)
                cc.origin = true;

            Queue<CalcCell> queue = new ArrayDeque<>();
            for (CalcCell cc : list)
                if (cc.exists()) {
                    used[cc.row][cc.cell] = true;
                    queue.add(cc);
                }
            while (queue.size() > 0) {
                CalcCell cc = queue.poll();
                for (Direction dir : Direction.values()) {
                    CalcCell cx = cc.neighbor(dir);
                    if (cx == null)
                        continue;
                    if (used[cx.row][cx.cell])
                        continue;
                    used[cx.row][cx.cell] = true;
                    queue.add(cx);
                }
            }
            for (int i = 0; i < used.length; i++)
                for (int j = 0; j < used[i].length; j++)
                    if (!used[i][j])
                        ccell[i][j] = null;

            for (int i = 0; i < ccell.length; i++)
                for (int j = 0; j < ccell[i].length; j++)
                    if (ccell[i][j] != null)
                        ccell[i][j].init();
            for (int i = 0; i < ccell.length; i++)
                for (int j = 0; j < ccell[i].length; j++)
                    if (ccell[i][j] != null)
                        ccell[i][j].remove();
        }

        /**
         * return a summary of the flow in this diagram
         *
         * @throws HexException
         */
        public FlowChart getMatrix(boolean withFlow) throws HexException {
            Frac[][] matrix = new Frac[6][6]; // the input-output matrix
            in = new Arrow[6]; // the input array
            Arrow[] out = new Arrow[6];
            for (int i = 0; i < 6; i++) {
                if (!list.get(i).exists())
                    continue;
                for (Arrow v : list.get(i).output)
                    if (v != null)
                        in[i] = v;
                for (Arrow v : list.get(i).input)
                    if (v != null)
                        out[i] = v;
            }
            for (int i = 0; i < 6; i++) {
                if (out[i] == null)
                    continue;
                for (int j = 0; j < 6; j++) {
                    if (in[j] == null)
                        continue;
                    matrix[i][j] = out[i].map.get(in[j]);
                }
            }

            // reset counter
            head.clear();
            Set<Arrow> origins = new HashSet<>();
            for (Arrow v : pool) {
                v.user.clear();
                v.rely = 0;
                if (v.src.origin)
                    origins.add(v);
            }
            // reset dependency
            for (Arrow v : pool)
                for (Arrow dep : v.map.keySet())
                    if (!dep.src.origin) {
                        dep.user.add(v);
                        v.rely++;
                    }
            for (Arrow v : pool)
                if (!v.src.origin && !v.dst.origin && v.rely == 0)
                    head.add(v);

            FlowChart ans = new FlowChart(matrix);
            if (!withFlow)
                return ans;
            flowmap = new HashMap<>();
            while (head.size() > 0) {
                Arrow next = head.poll();
                next.remove();
                addFlow(ans, next);
            }
            for (int i = 0; i < 6; i++) {
                addFlow(ans, in[i]);
                addFlow(ans, out[i]);
            }

            return ans;
        }

        private void addFlow(FlowChart ans, Arrow next) throws HexException {
            HexHandler hex = HexHandler.this;
            Frac[] formula = new Frac[6];
            for (int i = 0; i < 6; i++)
                if (in[i] != null)
                    if (in[i] == next)
                        formula[i] = new Frac(1, 1);
                    else
                        formula[i] = next.map.get(in[i]);
            CalcCell src = next.src;
            Direction dir = next.dir;
            if (dir.ind >= 3) {
                src = next.dst;
                dir = dir.next(3);
            }
            Map<Direction, FlowChart.Flow> sub = null;
            if (flowmap.containsKey(src))
                sub = flowmap.get(src);
            else
                flowmap.put(src, sub = new HashMap<>());
            FlowChart.Flow flow = null;
            if (sub.containsKey(dir))
                flow = sub.get(dir);
            else
                sub.put(dir, flow = ans.new Flow(new ArrowResult(src.row, src.cell, dir, hex)));
            if (src == next.src)
                flow.forward = formula;
            else
                flow.backward = formula;
        }

    }

    private static final int CORE_LIMIT = 6;
    private static final int WIDTH = 14, HEIGHT = 12;

    private static CellResult getCoordinate(double x, double y) {
        // row number relative to center in rectangular grid
        int row = (int) Math.floor(y / HEIGHT + 0.5);
        // relative y coordinate of the point in rectangular grid
        double rel_y = y + (0.5 - row) * HEIGHT;
        // cell number relative to center in rectangular grid
        int cell = (int) Math.floor(x / WIDTH + 0.5 + Math.abs(row) * 0.5);
        // relative x coordinate of the point in rectangular grid
        double rel_x = x + (0.5 + Math.abs(row) * 0.5 - cell) * WIDTH;

        double xoff = WIDTH / 4.0;
        double yoff = HEIGHT / 6.0;

        if (rel_x / xoff + rel_y / yoff < 1) {
            cell += Direction.UPPER_LEFT.getCellOffset(0, row, cell);
            row--;
        }
        if ((WIDTH - rel_x) / xoff + rel_y / yoff < 1) {
            cell += Direction.UPPER_RIGHT.getCellOffset(0, row, cell);
            row--;
        }
        if (rel_x / xoff + (HEIGHT - rel_y) / yoff < 1) {
            cell += Direction.UPPER_LEFT.getCellOffset(0, row, cell);
            row++;
        }
        if ((WIDTH - rel_x) / xoff + (HEIGHT - rel_y) / yoff < 1) {
            cell += Direction.UPPER_RIGHT.getCellOffset(0, row, cell);
            row++;
        }

        return new CellResult(row / 2, cell / 2, null);
    }

    public final int radius;

    final byte[][] cells;

    public final SubHex[] subhex;
    public final SubHexCore[] cores;

    public HexHandler(int r) {
        radius = r;
        cells = new byte[getRowCount()][];
        for (int i = 0; i < getRowCount(); i++)
            cells[i] = new byte[getCellCount(i)];
        subhex = new SubHex[getArea()];
        cores = new SubHexCore[CORE_LIMIT];
    }

    public HexHandler(NBTObj tag) {
        byte[] data = tag.tag.getByteArray("data");
        byte[] subs = tag.tag.getByteArray("subs");
        NBTList<?> list = tag.getList("cores");
        radius = data[0] & 0xF;
        cells = new byte[getRowCount()][];
        for (int i = 0; i < getRowCount(); i++)
            cells[i] = new byte[getCellCount(i)];
        subhex = new SubHex[getArea()];
        cores = new SubHexCore[CORE_LIMIT];
        for (int i = 0; i < list.size(); i++)
            try {
                cores[i] = new SubHexCore(new HexHandler(list.get(i)));
            } catch (HexException e) {
                throw new RuntimeException(e);
            }
        int k = 1, s = 0;
        for (int i = 0; i < getRowCount(); i++)
            for (int j = 0; j < getCellCount(i); j++) {
                int val = data[k >> 1] >> ((k & 1) * 4);
                cells[i][j] = (byte) (val & 7);
                if ((val & 8) != 0) {
                    byte sval = subs[s++];
                    SubHexCore core = cores[sval & 7];
                    int rot = sval >> 3 & 7;
                    boolean flip = (sval >> 6 & 1) != 0;
                    subhex[getInd(i, j)] = new SubHex(core, rot, flip);
                }
            }
    }

    /**
     * get the total cell count of the hexagon
     */
    public int getArea() {
        return 3 * radius * (radius + 1) + 1;
    }

    public int getCellCount(int row) {
        return Math.min(row, radius * 2 - row) + radius + 1;
    }

    public CellResult getCellOnHex(double x, double y) {
        CellResult pos = getCoordinate(x, y);
        return CellResult.get(pos.row + radius, pos.cell + radius, this);
    }

    public LocateResult getElementOnHex(double x, double y) {
        CellResult pos = getCoordinate(x * 2, y * 2);
        int trow = Math.floorDiv(pos.row, 2) + radius;
        int tcel = Math.floorDiv(pos.cell, 2) + radius;
        if (pos.row % 2 == 0 && pos.cell % 2 == 0)
            return CellResult.get(trow, tcel, this);
        if (pos.row % 2 == 0) {
            return ArrowResult.get(trow, tcel, Direction.RIGHT, this);
        }
        Direction dir = (pos.row < 0) ^ (pos.cell % 2 == 0) ? Direction.LOWER_RIGHT : Direction.LOWER_LEFT;
        return ArrowResult.get(trow, tcel, dir, this);

    }

    /**
     * get the index of a cell in special cell array
     */
    public int getInd(int row, int cell) {
        if (row <= radius)
            return (2 * radius + 1 + row) * row / 2 + cell;
        return getArea() - (4 * radius + 2 - row) * (2 * radius + 1 - row) / 2 + cell;
    }

    public FlowChart getMatrix(boolean withFlow) throws HexException {
        return new Calc().getMatrix(withFlow);
    }

    public int getRowCount() {
        return radius * 2 + 1;
    }

    /**
     * get the X position of a cell relative to the center
     */
    public int getX(int row, int cell) {
        return (cell - radius) * WIDTH + (radius * 2 + 1 - getCellCount(row)) * WIDTH / 2;
    }

    /**
     * get the Y position of a cell relative to the center
     */
    public int getY(int row, int cell) {
        return (row - radius) * HEIGHT;
    }

    public boolean isInvalid() {
        Cell cell = new Cell(0, 0);
        for (int r = 0; r < cells.length; r++)
            for (int c = 0; c < cells[r].length; c++) {
                cell.row = r;
                cell.cell = c;
                if (cell.isInvalid())
                    return true;
            }
        return false;
    }

    public NBTObj write(NBTObj tag) {
        int tot = getArea() * 4 + 4;
        int len = tot >> 3;
        byte[] data = new byte[len];
        SubHex[] sub = new SubHex[getArea()];
        data[0] |= radius & 0xF;
        int k = 1, s = 0;
        for (int i = 0; i < getRowCount(); i++)
            for (int j = 0; j < getCellCount(i); j++) {
                data[k >> 1] |= (cells[i][j] & 7) << ((k & 1) * 4);
                if (subhex[getInd(i, j)] != null) {
                    data[k >> 1] |= 4 << ((k & 1) * 4);
                    sub[s++] = subhex[getInd(i, j)];
                }
                k++;
            }
        tag.tag.putByteArray("data", data);
        byte[] subs = new byte[s];
        for (int i = 0; i < sub.length; i++) {
            subs[i] |= sub[i].core.index;
            subs[i] |= (sub[i].rotation + 6) % 6 << 3;
            subs[i] |= (sub[i].flip ? 1 : 0) << 6;
        }
        tag.tag.putByteArray("subs", subs);
        NBTList<?> list = tag.getList("cores");
        for (SubHexCore core : cores)
            core.hex.write(list.add());
        return tag;
    }

}
