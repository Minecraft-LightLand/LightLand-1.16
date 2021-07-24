package com.hikarishima.lightland.magic.gui.hex;

import com.lcy0x1.core.magic.HexHandler;
import net.minecraft.client.gui.AbstractGui;

public class HexGraphGui extends AbstractGui {

    private final MagicHexScreen screen;

    private HexHandler graph;
    private int radius;

    private double scrollX, scrollY;

    public HexGraphGui(MagicHexScreen screen) {
        this.screen = screen;
        graph = screen.product.getSolution();
        if (graph == null) {
            graph = new HexHandler(radius = 3);
        }
        else radius = graph.radius;
    }

    public void setRadius(int radius){
        this.radius = radius;
        graph = new HexHandler(radius);
    }

    public int getRadius(){
        return radius;
    }

}
