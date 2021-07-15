package com.lcy0x1.core.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.Consumer;

@SerialClass
public class SpriteManager {

    @SerialClass
    public static class Rect {

        public static final Rect ZERO = new Rect();

        @SerialClass.SerialField
        public int x, y, w, h, rx = 1, ry = 1;

        public Rect() {
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class ScreenRenderer {

        private final int x, y, w, h;
        private final Screen scr;

        public ScreenRenderer(Screen gui, int x, int y, int w, int h) {
            scr = gui;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        private ScreenRenderer(ContainerScreen<?> scrIn) {
            x = scrIn.getGuiLeft();
            y = scrIn.getGuiTop();
            w = scrIn.getXSize();
            h = scrIn.getYSize();
            scr = scrIn;
        }

        /**
         * Draw a side sprite on the location specified by the component
         */
        public void draw(MatrixStack mat, String c, String s) {
            Rect cr = getComp(c);
            Rect sr = getSide(s);
            scr.blit(mat, x + cr.x, y + cr.y, sr.x, sr.y, sr.w, sr.h);
        }

        /**
         * Draw a side sprite on the location specified by the component with offsets
         */
        public void draw(MatrixStack mat, String c, String s, int xoff, int yoff) {
            Rect cr = getComp(c);
            Rect sr = getSide(s);
            scr.blit(mat, x + cr.x + xoff, y + cr.y + yoff, sr.x, sr.y, sr.w, sr.h);
        }

        /**
         * Draw a side sprite on the location specified by the component. Draw partially
         * from bottom to top
         */
        public void drawBottomUp(MatrixStack mat, String c, String s, int prog, int max) {
            if (prog == 0 || max == 0)
                return;
            Rect cr = getComp(c);
            Rect sr = getSide(s);
            int dh = sr.h * prog / max;
            scr.blit(mat, x + cr.x, y + cr.y + sr.h - dh, sr.x, sr.y + sr.h - dh, sr.w, dh);
        }

        /**
         * Draw a side sprite on the location specified by the component. Draw partially
         * from left to right
         */
        public void drawLeftRight(MatrixStack mat, String c, String s, int prog, int max) {
            if (prog == 0 || max == 0)
                return;
            Rect cr = getComp(c);
            Rect sr = getSide(s);
            int dw = sr.w * prog / max;
            scr.blit(mat, x + cr.x, y + cr.y, sr.x, sr.y, dw, sr.h);
        }

        /**
         * fill an area with a sprite, repeat as tiles if not enough, start from lower
         * left corner
         */
        public void drawLiquid(MatrixStack mat, String c, double per, int height, int sw, int sh) {
            Rect cr = getComp(c);
            int base = cr.y + height;
            int h = (int) Math.round(per * height);
            circularBlit(mat, x + cr.x, base - h, 0, -h, cr.w, h, sw, sh);
        }

        /**
         * bind texture, draw background color, and GUI background
         */
        public void start(MatrixStack mat) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            scr.getMinecraft().getTextureManager().bind(texture);
            scr.blit(mat, x, y, 0, 0, w, h);
        }

        private void circularBlit(MatrixStack mat, int sx, int sy, int ix, int iy, int w, int h, int iw, int ih) {
            int x0 = ix, yb = iy, x1 = w, x2 = sx;
            while (x0 < 0)
                x0 += iw;
            while (yb < ih)
                yb += ih;
            while (x1 > 0) {
                int dx = Math.min(x1, iw - x0);
                int y0 = yb, y1 = h, y2 = sy;
                while (y1 > 0) {
                    int dy = Math.min(y1, ih - y0);
                    scr.blit(mat, x2, y2, x0, y0, x1, y1);
                    y1 -= dy;
                    y0 += dy;
                    y2 += dy;
                }
                x1 -= dx;
                x0 += dx;
                x2 += dx;
            }
        }

    }

    public interface SlotFactory<T extends Slot> {

        T getSlot(int x, int y);

    }

    private final String name;
    private final ResourceLocation coords, texture;

    @SerialClass.SerialField
    private final int height = 0;
    @SerialClass.SerialField(generic = {String.class, Rect.class})
    private HashMap<String, Rect> side, comp;

    private boolean loaded = false;

    public SpriteManager(String mod, String str) {
        name = mod + ":" + str;
        coords = new ResourceLocation(mod, "/textures/gui/coords/" + str + ".json");
        texture = new ResourceLocation(mod, "/textures/gui/container/" + str + ".png");
        check();
    }

    /**
     * get the location of the component on the GUI
     */
    public Rect getComp(String key) {
        check();
        return comp.getOrDefault(key, Rect.ZERO);
    }

    /**
     * Height of this GUI
     */
    public int getHeight() {
        check();
        return height;
    }

    /**
     * The X position of the player inventory
     */
    public int getPlInvX() {
        check();
        return 8;// TODO
    }

    /**
     * The Y position of the player inventory
     */
    public int getPlInvY() {
        check();
        return height - 82;
    }

    @OnlyIn(Dist.CLIENT)
    public ScreenRenderer getRenderer(ContainerScreen<?> gui) {
        check();
        return new ScreenRenderer(gui);
    }

    @OnlyIn(Dist.CLIENT)
    public ScreenRenderer getRenderer(Screen gui, int x, int y, int w, int h) {
        check();
        return new ScreenRenderer(gui, x, y, w, h);
    }

    /**
     * get the rectangle representing the sprite element on the sprite
     */
    public Rect getSide(String key) {
        check();
        return side.getOrDefault(key, Rect.ZERO);
    }

    /**
     * configure the coordinate of the slot
     */
    public <T extends Slot> void getSlot(String key, SlotFactory<T> fac, Consumer<Slot> con) {
        check();
        Rect c = getComp(key);
        for (int i = 0; i < c.rx; i++)
            for (int j = 0; j < c.ry; j++)
                con.accept(fac.getSlot(c.x + i * c.w, c.y + j * c.h));
    }

    public int getWidth() {
        check();
        return 176;// TODO
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * return if the coordinate is within the rectangle represented by the key
     */
    public boolean within(String key, double x, double y) {
        check();
        Rect c = getComp(key);
        return x > c.x && x < c.x + c.w && y > c.y && y < c.y + c.h;
    }

    private void check() {
        if (!loaded && FMLEnvironment.dist.isClient())
            load();
    }

    private void load() {
        try {
            IResource r = Minecraft.getInstance().getResourceManager().getResource(coords);
            JsonObject jo = new JsonParser().parse(new InputStreamReader(r.getInputStream())).getAsJsonObject();
            Serializer.from(jo, SpriteManager.class, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
    }

}
