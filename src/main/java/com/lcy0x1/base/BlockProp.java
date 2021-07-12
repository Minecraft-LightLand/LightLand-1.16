package com.lcy0x1.base;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockProp {

	public static final BlockProp ORE_0 = new BlockProp(Material.STONE, 3, 3).setTool(ToolType.PICKAXE, 0);


	private final Block.Properties props;

	private BlockProp(Material mat, float hard, float rest) {
		this(Block.Properties.of(mat), hard, rest);
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

}
