package com.hikarishima.lightland.magic.gui.hex;

import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.magic.products.info.ProductState;
import com.lcy0x1.core.magic.*;
import com.lcy0x1.core.math.Frac;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import org.apache.logging.log4j.LogManager;

import java.util.function.IntConsumer;

public class HexGraphGui extends AbstractGui {

    private static final double MARGIN = 0.9, RADIUS = 2 / Math.sqrt(3);
    private static final int COL_BG = 0xFF808080;
    private static final int COL_ENABLED = 0xFFFFFFFF;
    private static final int COL_DISABLED = 0xFF404040;
    private static final int COL_HOVER = 0xFFFFFF00;
    private static final int COL_ERROR = 0xFFFF0000;

    private static int getFlowColor(double val) {
        if (val <= 1) {
            val = Math.pow(val, 0.5);
            int col = (int) ((1 - val) * 255);
            return 0xFF0000FF | (col << 16) | (col << 8);
        } else {
            val = Math.pow(val, 1);
            int col = (int) (255 / val);
            return 0xFF000000 | ((255 - col) << 16) | col;
        }
    }

    private final MagicHexScreen screen;

    private HexHandler graph;
    private double magn = 14;
    private double scrollX, scrollY;

    private FlowChart flow = null;
    private HexCalcException error = null;
    private HexDirection selected = null;

    public HexGraphGui(MagicHexScreen screen) {
        this.screen = screen;
        graph = screen.product.getSolution();
        if (graph == null) {
            graph = new HexHandler(3);
        }
    }

    public void render(MatrixStack matrix, int x0, int y0, double mx, double my, float partial) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.pushMatrix();
        RenderSystem.translated(x0 + scrollX, y0 + scrollY, 0);

        LocateResult hover = graph.getElementOnHex((mx - x0 - scrollX) / magn, (my - y0 - scrollY) / magn);
        renderBG(matrix, hover);
        double width = RADIUS / 4 * magn;
        double length = HexHandler.WIDTH * 3 / 4 * magn;
        renderPath(matrix, width, length);
        renderFlow(matrix, width, length);
        renderError(matrix, width, length);

        RenderSystem.popMatrix();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void renderBG(MatrixStack matrix, LocateResult hover) {
        HexCell cell = new HexCell(graph, 0, 0);
        for (cell.row = 0; cell.row < graph.getRowCount(); cell.row++)
            for (cell.cell = 0; cell.cell < graph.getCellCount(cell.row); cell.cell++) {
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = MARGIN * RADIUS * magn;
                renderHex(matrix, x, y, r, COL_BG);
            }
        if (hover != null)
            renderHex(matrix, hover.getX() * magn, hover.getY() * magn, RADIUS * magn / 2, COL_HOVER);
    }

    private void renderPath(MatrixStack matrix, double width, double length) {
        HexCell cell = new HexCell(graph, 0, 0);
        for (cell.row = 0; cell.row < graph.getRowCount(); cell.row++)
            for (cell.cell = 0; cell.cell < graph.getCellCount(cell.row); cell.cell++) {
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = RADIUS * magn;
                int col;
                for (int i = 0; i < 3; i++) {
                    HexDirection dire = HexDirection.values()[i];
                    if (cell.canWalk(dire)) {
                        col = cell.isConnected(dire) ? COL_ENABLED : COL_DISABLED;
                        renderPath(matrix, x, y, HexHandler.WIDTH * magn, i, col, width, length);
                    }
                }
                col = cell.exists() ? COL_ENABLED : COL_DISABLED;
                renderHex(matrix, x, y, r / 4, col);
            }
    }

    private void renderFlow(MatrixStack matrix, double width, double length) {
        if (flow != null) {
            HexCell cell = new HexCell(graph, 0, 0);
            double[][] vals = new double[graph.getRowCount()][];
            for (cell.row = 0; cell.row < graph.getRowCount(); cell.row++) {
                vals[cell.row] = new double[graph.getCellCount(cell.row)];
            }
            for (FlowChart.Flow f : flow.flows) {
                double val;
                cell.row = f.arrow.row;
                cell.cell = f.arrow.cell;
                if (selected == null) {
                    double fval = 0;
                    for (Frac fr : f.forward)
                        if (fr != null)
                            fval += fr.getVal();
                    updateNodeVal(vals, cell, f.arrow.dir, fval / 6);
                    double bval = 0;
                    for (Frac fr : f.backward)
                        if (fr != null)
                            bval += fr.getVal();
                    updateNodeVal(vals, cell, f.arrow.dir, bval / 6);
                    val = (fval + bval) / 6;
                } else {
                    val = 0;
                    if (f.forward[selected.ind] != null) {
                        double fval = f.forward[selected.ind].getVal();
                        updateNodeVal(vals, cell, f.arrow.dir, fval);
                        val += fval;
                    }

                    if (f.backward[selected.ind] != null) {
                        double bval = f.backward[selected.ind].getVal();
                        updateNodeVal(vals, cell, f.arrow.dir, bval);
                        val += bval;
                    }
                }
                int col = getFlowColor(val);
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = RADIUS * magn;
                int dire = f.arrow.dir.ind;
                renderPath(matrix, x, y, HexHandler.WIDTH * magn, dire, col, width, length);
            }
            for (cell.row = 0; cell.row < graph.getRowCount(); cell.row++)
                for (cell.cell = 0; cell.cell < graph.getCellCount(cell.row); cell.cell++) {
                    double x = cell.getX() * magn;
                    double y = cell.getY() * magn;
                    double r = RADIUS * magn;
                    double val = vals[cell.row][cell.cell];
                    int col;
                    if (cell.exists())
                        col = getFlowColor(val);
                    else col = COL_DISABLED;
                    renderHex(matrix, x, y, r / 4, col);
                }
            if (selected != null) {
                cell.row = graph.radius;
                cell.cell = graph.radius;
                while (cell.canWalk(selected))
                    cell.walk(selected);
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = RADIUS * magn;
                renderHex(matrix, x, y, r / 4, COL_HOVER);
            }
        }

    }

    private void renderError(MatrixStack matrix, double width, double length) {
        if (error != null) {
            HexCell cell = new HexCell(graph, 0, 0);
            for (HexCalcException.Side side : error.error) {
                cell.row = side.row;
                cell.cell = side.cell;
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = RADIUS * magn;
                int col = COL_ERROR;
                int dire = side.dir.ind;
                renderPath(matrix, x, y, HexHandler.WIDTH * magn, dire, col, width, length);
                renderHex(matrix, x, y, r / 4, col);
            }
        }
    }

    private void updateNodeVal(double[][] vals, HexCell cell, HexDirection dir, double val) {
        vals[cell.row][cell.cell] += cell.isCorner() ? val : val / 2;
        cell.walk(dir);
        vals[cell.row][cell.cell] += cell.isCorner() ? val : val / 2;
        cell.walk(dir.next(3));
    }

    public void setRadius(int radius) {
        graph = new HexHandler(radius);
    }

    public int getRadius() {
        return graph.radius;
    }

    public void scroll(double dx, double dy) {
        scrollX += dx;
        scrollY += dy;
    }

    private void renderHex(MatrixStack matrix, double x, double y, double r, int color) {
        Matrix4f last = matrix.last().pose();
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        float ca = (float) (color >> 24 & 255) / 255.0F;
        float cr = (float) (color >> 16 & 255) / 255.0F;
        float cg = (float) (color >> 8 & 255) / 255.0F;
        float cb = (float) (color & 255) / 255.0F;
        IntConsumer c = i -> {
            double a = (i + 0.5) * Math.PI / 3;
            float px = (float) (x + r * Math.cos(a));
            float py = (float) (y + r * Math.sin(a));
            builder.vertex(last, px, py, 0).color(cr, cg, cb, ca).endVertex();
        };
        c.accept(0);
        c.accept(3);
        c.accept(2);
        c.accept(1);
        c.accept(0);
        c.accept(5);
        c.accept(4);
        c.accept(3);

        builder.end();
        WorldVertexBufferUploader.end(builder);
    }

    private void renderPath(MatrixStack matrix, double x, double y, double r, int dire, int color, double width, double length) {
        Matrix4f last = matrix.last().pose();
        float ca = (float) (color >> 24 & 255) / 255.0F;
        float cr = (float) (color >> 16 & 255) / 255.0F;
        float cg = (float) (color >> 8 & 255) / 255.0F;
        float cb = (float) (color & 255) / 255.0F;
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        double a = dire * Math.PI / 3;
        float cx = (float) (x + r * Math.cos(a) / 2);
        float cy = (float) (y + r * Math.sin(a) / 2);
        float lx = (float) (length / 2 * Math.cos(a));
        float ly = (float) (length / 2 * Math.sin(a));
        float wx = (float) (width / 2 * Math.cos(a - Math.PI / 2));
        float wy = (float) (width / 2 * Math.sin(a - Math.PI / 2));
        builder.vertex(last, cx + lx + wx, cy + ly + wy, 0).color(cr, cg, cb, ca).endVertex();
        builder.vertex(last, cx - lx + wx, cy - ly + wy, 0).color(cr, cg, cb, ca).endVertex();
        builder.vertex(last, cx - lx - wx, cy - ly - wy, 0).color(cr, cg, cb, ca).endVertex();
        builder.vertex(last, cx + lx - wx, cy + ly - wy, 0).color(cr, cg, cb, ca).endVertex();
        builder.end();
        WorldVertexBufferUploader.end(builder);
    }

    public boolean mouseClicked(int x0, int y0, double mx, double my, int button) {
        LocateResult hover = graph.getElementOnHex((mx - x0 - scrollX) / magn, (my - y0 - scrollY) / magn);
        if (click(hover)) {
            flow = null;
            error = null;
            save();
            return true;
        }
        return false;
    }

    private boolean click(LocateResult hover) {
        if (hover == null)
            return false;
        if (hover.getType() == LocateResult.ResultType.ARROW) {
            ArrowResult res = (ArrowResult) hover;
            new HexCell(graph, res.row, res.cell).toggle(res.dir);
            return true;
        } else if (hover.getType() == LocateResult.ResultType.CELL) {
            CellResult res = (CellResult) hover;
            HexCell cell = new HexCell(graph, res.row, res.cell);
            if (flow == null) {
                for (HexDirection dire : HexDirection.values()) {
                    if (cell.canWalk(dire) && cell.isConnected(dire))
                        cell.toggle(dire);
                }
                return true;
            } else if (cell.isCorner()) {
                selected = cell.getCorner();
                return false;
            }
        }
        return false;
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        magn = MathHelper.clamp(magn + amount, 4, 20);
        return true;
    }

    public boolean charTyped(char ch) {
        if (!Minecraft.getInstance().player.isCreative())
            return false;
        if (ch == 's') {
            save();
            return true;
        } else if (ch == 'r') {
            compile();
            return true;
        }
        return false;
    }

    private void save() {
        int cost = -1;
        if (screen.product.getState() == ProductState.CRAFTED) {
            //TODO check match
            return;
        }
        screen.product.updateBestSolution(graph, cost);
        ToServerMsg.sendHexUpdate(screen.product);
    }

    private void compile() {
        flow = null;
        error = null;
        try {
            flow = graph.getMatrix(true);
        } catch (HexCalcException e) {
            flow = null;
            error = e;
        } catch (Exception e) {
            flow = null;
            error = null;
            LogManager.getLogger().throwing(e);
        }
    }
}
