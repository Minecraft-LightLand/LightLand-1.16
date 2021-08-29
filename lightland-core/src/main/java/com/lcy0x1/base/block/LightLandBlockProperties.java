package com.lcy0x1.base.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

public class LightLandBlockProperties {

    public static final LightLandBlockProperties ORE_0 = new LightLandBlockProperties(Material.STONE, 3, 3).setTool(ToolType.PICKAXE, 0);

    public static LightLandBlockProperties copy(Block b) {
        return new LightLandBlockProperties(AbstractBlock.Properties.copy(b));
    }

    private final Block.Properties props;

    private LightLandBlockProperties(Material mat, float hard, float rest) {
        this(Block.Properties.of(mat), hard, rest);
    }

    private LightLandBlockProperties(Block.Properties mat) {
        props = mat;
    }

    private LightLandBlockProperties(Block.Properties mat, float hard, float rest) {
        props = mat;
        props.strength(hard, rest);
    }

    public Block.Properties getProps() {
        return props;
    }

    private LightLandBlockProperties setTool(ToolType tool, int level) {
        props.harvestTool(tool);
        props.harvestLevel(level);
        return this;
    }

    public LightLandBlockProperties make(Consumer<AbstractBlock.Properties> cons) {
        cons.accept(props);
        return this;
    }

}
