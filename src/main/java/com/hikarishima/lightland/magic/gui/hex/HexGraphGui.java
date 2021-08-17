package com.hikarishima.lightland.magic.gui.hex;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.gui.AbstractHexGui;
import com.lcy0x1.base.WindowBox;
import com.lcy0x1.core.magic.*;
import com.lcy0x1.core.math.Frac;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HexGraphGui extends AbstractHexGui {

    private static final int PERIOD = 60;
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

    HexHandler graph;
    FlowChart flow = null;
    boolean[] wrong_flow = new boolean[6];
    final WindowBox box = new WindowBox();

    protected HexCalcException error = null;
    protected HexDirection selected = null;

    private double magn = 14;
    private double scrollX, scrollY;
    private int tick;


    public HexGraphGui(MagicHexScreen screen) {
        this.screen = screen;
        graph = screen.product.getSolution();
        if (graph == null) {
            graph = new HexHandler(3);
        }
    }

    public void render(MatrixStack matrix, double mx, double my, float partial) {
        double x0 = box.x + box.w / 2d;
        double y0 = box.y + box.h / 2d;
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
        RenderSystem.enableTexture();
        renderIcons(matrix);
        RenderSystem.popMatrix();
        RenderSystem.disableBlend();
    }

    public void renderHover(MatrixStack matrix, double mx, double my) {
        double x0 = box.x + box.w / 2d;
        double y0 = box.y + box.h / 2d;
        LocateResult hover = graph.getElementOnHex((mx - x0 - scrollX) / magn, (my - y0 - scrollY) / magn);
        renderTooltip(matrix, mx, my, hover);
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
                cell.toCorner(selected);
                double x = cell.getX() * magn;
                double y = cell.getY() * magn;
                double r = RADIUS * magn;
                renderHex(matrix, x, y, r / 4, COL_HOVER);
            }
            for (int i = 0; i < 6; i++) {
                if (wrong_flow[i]) {
                    cell.toCorner(HexDirection.values()[i]);
                    double x = cell.getX() * magn;
                    double y = cell.getY() * magn;
                    double r = RADIUS * magn;
                    renderHex(matrix, x, y, r / 4, COL_ERROR);
                }
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
                cell.walk(side.dir);
                x = cell.getX() * magn;
                y = cell.getY() * magn;
                renderHex(matrix, x, y, r / 4, col);

            }
        }
    }

    private void renderIcons(MatrixStack matrix) {
        HexCell cell = new HexCell(graph, 0, 0);
        for (int i = 0; i < 6; i++) {
            MagicElement elem = screen.result.getElem(i);
            if (elem == null)
                continue;
            Minecraft.getInstance().getTextureManager().bind(elem.getIcon());
            cell.toCorner(HexDirection.values()[i]);
            double x = cell.getX() * magn;
            double y = cell.getY() * magn;
            drawIcon(matrix, x, y, magn / 10);
        }
    }

    private void renderTooltip(MatrixStack matrix, double mx, double my, LocateResult hover) {
        if (hover == null || hover.getType() != LocateResult.ResultType.ARROW || selected == null || flow == null)
            return;
        flow.flows.stream().filter(e -> e.arrow.equals(hover)).findFirst().ifPresent(e -> {
            List<ITextComponent> list = new ArrayList<>();
            if (e.forward[selected.ind] != null)
                list.add(Translator.get(e.arrow.dir).append(e.forward[selected.ind].toString()));
            if (e.backward[selected.ind] != null)
                list.add(Translator.get(e.arrow.dir.next(3)).append(e.backward[selected.ind].toString()));
            if (list.size() > 0)
                AbstractHexGui.drawHover(matrix, list, mx, my, screen);
        });
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

    public boolean mouseClicked(double mx, double my, int button) {
        double x0 = box.x + box.w / 2d;
        double y0 = box.y + box.h / 2d;
        if (button == 0) {
            LocateResult hover = graph.getElementOnHex((mx - x0 - scrollX) / magn, (my - y0 - scrollY) / magn);
            if (click(hover)) {
                flow = null;
                error = null;
                screen.compile = HexStatus.Compile.EDITING;
                screen.updated();
                return true;
            }
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
                if (selected == cell.getCorner())
                    selected = null;
                else
                    selected = cell.getCorner();
                return false;
            }
        }
        return false;
    }

    public void tick() {
        tick++;
        tick %= PERIOD;
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        magn = MathHelper.clamp(magn + amount, 4, 20);
        return true;
    }

    public boolean charTyped(char ch) {
        if (ch == 'r') {
            compile();
            screen.updated();
            return true;
        } else if (ch == '=' && graph.radius < 7) {
            setRadius(graph.radius + 1);
            flow = null;
            error = null;
            screen.updated();
        } else if (ch == '-' && graph.radius > 2) {
            setRadius(graph.radius - 1);
            flow = null;
            error = null;
            screen.updated();
        }
        if (!Minecraft.getInstance().player.isCreative())
            return false;
        if (ch == 'f') {
            screen.forceSave(false);
            return true;
        } else
            return false;
    }

    void compile() {
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
