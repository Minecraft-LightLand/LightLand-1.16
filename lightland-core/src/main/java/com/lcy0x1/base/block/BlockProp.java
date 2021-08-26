package com.lcy0x1.base.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

public class BlockProp {

    public static final BlockProp ORE_0 = new BlockProp(Material.STONE, 3, 3).setTool(ToolType.PICKAXE, 0);

    public static BlockProp copy(Block b) {
        return new BlockProp(AbstractBlock.Properties.copy(b));
    }

    private final Block.Properties props;

    private BlockProp(Material mat, float hard, float rest) {
        this(Block.Properties.of(mat), hard, rest);
    }

    private BlockProp(Block.Properties mat) {
        props = mat;
    }

    private BlockProp(Block.Properties mat, float hard, float rest) {
        props = mat;
        props.strength(hard, rest);
    }

    public Block.Properties getProps() {
        return props;
    }

    private BlockProp setTool(ToolType tool, int level) {
        props.harvestTool(tool);
        props.harvestLevel(level);
        return this;
    }

    public BlockProp make(Consumer<AbstractBlock.Properties> cons) {
        cons.accept(props);
        return this;
    }

}
