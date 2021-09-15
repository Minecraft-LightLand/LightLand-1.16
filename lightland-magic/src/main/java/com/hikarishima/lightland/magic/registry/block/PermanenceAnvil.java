package com.hikarishima.lightland.magic.registry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class PermanenceAnvil extends Block {

    public PermanenceAnvil(Properties props) {
        super(props);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return ModList.get().isLoaded("apotheosis") && ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation("apotheosis", "anvil")) != null;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (ModList.get().isLoaded("apotheosis")) {
            TileEntityType<?> type = ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation("apotheosis", "anvil"));
            return type == null ? null : type.create();
        }
        return null;
    }

}
