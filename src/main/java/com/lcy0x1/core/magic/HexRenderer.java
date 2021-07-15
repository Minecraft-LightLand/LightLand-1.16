package com.lcy0x1.core.magic;

import com.lcy0x1.core.magic.HexHandler.*;
import com.lcy0x1.core.magic.HexHandler.FlowChart.Flow;

public class HexRenderer {

    public interface Renderer {

        void render(int x, int y, String sprite);

    }

    public final Renderer renderer;
    public HexHandler hex;
    public LocateResult result;
    public FlowChart flow;

    private Cell cell;

    private int time;

    public HexRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * renders the content of the hexagon, not including tooltips and cursor
     */
    public void renderHex() {
        if (hex == null)
            return;
        cell = hex.new Cell(0, 0);

        // render sides
        for (cell.row = 0; cell.row < hex.getRowCount(); cell.row++)
            for (cell.cell = 0; cell.cell < hex.getCellCount(cell.row); cell.cell++)
                for (int i = 0; i < 3; i++) { // only render right, lower right, and lower left
                    Direction dir = Direction.values()[i];
                    if (!cell.canWalk(dir))
                        continue;
                    boolean chosen = result instanceof ArrowResult;
                    if (chosen) {
                        ArrowResult ar = (ArrowResult) result;
                        chosen = ar.row == cell.row && ar.cell == cell.cell && ar.dir == dir;
                    }
                    renderSide(dir.ind, cell.isConnected(dir), chosen);
                }

        // render background cells
        for (cell.row = 0; cell.row < hex.getRowCount(); cell.row++)
            for (cell.cell = 0; cell.cell < hex.getCellCount(cell.row); cell.cell++)
                if (cell.getSubHex() == null)
                    renderTile(cell.exists());

        // render subhex
        for (cell.row = 0; cell.row < hex.getRowCount(); cell.row++)
            for (cell.cell = 0; cell.cell < hex.getCellCount(cell.row); cell.cell++)
                if (cell.getSubHex() != null) {
                    SubHex subhex = cell.getSubHex();
                    int color = subhex.core.index;
                    int rot = subhex.rotation;
                    boolean flip = subhex.flip;
                    int err = subhex.isInvalid(cell);
                    renderCore(color, rot, flip, err != 0);
                    for (int i = 0; i < 6; i++)
                        if ((err & 1 << i) != 0)
                            renderErrorNode(color, i);
                }
        // render flows
        if (flow != null && flow.flows != null)
            for (Flow f : flow.flows) {
                cell.row = f.arrow.row;
                cell.cell = f.arrow.cell;
                int forward = 0;
                int backward = 0;
                for (int i = 0; i < 6; i++) {
                    if (f.forward[i] != null)
                        forward |= 1 << i;
                    if (f.backward[i] != null)
                        backward |= 1 << i;
                }
                renderFlow(f.arrow.dir.ind, f.arrow.equals(result), forward, backward);
            }
        time++;
        cell = null;
    }

    /**
     * @param color   color of this core, index in the range of 0-5
     * @param dir     represents the angle in the range of 0-5, in the multiple of
     *                60 degrees
     * @param flip    if this sub-hex is flipped
     * @param invalid if this sub-hex configuration is invalid
     */
    private void renderCore(int color, int dir, boolean flip, boolean invalid) {
        renderer.render(cell.getX() - 4, cell.getY() - 4, "core_" + dir + "_" + dir + (flip ? "f" : ""));
    }

    /**
     * @param color color of this core, index in the range of 0-5
     * @param dir   represents the angle in the range of 0-5, in the multiple of 60
     *              degrees
     */
    private void renderErrorNode(int color, int dir) {
        renderer.render(cell.getX() - 6, cell.getY() - 6, "err_" + dir);
    }

    /**
     * @param dir      represents the angle in the range of 0-2, in the multiple of
     *                 60 degrees
     * @param choosen  true if this line is hovered
     * @param forward  bitmask of forward flow, 6 bits
     * @param backward bitmask of backward flow, 6 bits
     */
    private void renderFlow(int dir, boolean choosen, int forward, int backward) {
        int dx0 = (dir == 0 ? 2 : dir == 1 ? 1 : -1) - 3;
        int dx1 = (dir == 0 ? 2 : dir == 2 ? -1 : 1);
        int dx2 = dir == 0 ? 0 : dir == 1 ? 1 : -1;
        int dy0 = (dir == 0 ? 0 : 1) - 3;
        int dy1 = dir == 0 ? 0 : 2;
        int dy2 = dir == 0 ? 1 : 0;
        for (int i = 0; i < 6; i++) {
            int t0 = (time + i) % 6;
            int t1 = 5 - t0;
            int x = cell.getX() + dx0;
            int y = cell.getY() + dy0;
            if ((forward & 1 << i) != 0)
                renderer.render(x + dx1 * t0 + dx2, y + dy1 * t0 + dy2, "flow_" + i);
            if ((backward & 1 << i) != 0)
                renderer.render(x + dx1 * t1 - dx2, y + dy1 * t1 - dy2, "flow_" + i);
        }
    }

    /**
     * @param dir     represents the angle in the range of 0-2, in the multiple of
     *                60 degrees
     * @param connect true if this line in connected, false for background shadow
     * @param choosen true if this line is hovered
     */
    private void renderSide(int dir, boolean connect, boolean choosen) {
        int dx = dir == 2 ? -7 : 0;
        int dy = dir == 0 ? -1 : 0;
        renderer.render(cell.getX() + dx, cell.getY() + dy, "line_" + dir + "_" + (connect ? "light" : "dark"));
    }

    /**
     * @param enabled true if this node is connected
     */
    private void renderTile(boolean enabled) {
        renderer.render(cell.getX() - 3, cell.getY() - 3, "tile_" + (enabled ? "light" : "dark"));
    }

}
