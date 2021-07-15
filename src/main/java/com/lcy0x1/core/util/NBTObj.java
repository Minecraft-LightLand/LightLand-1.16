package com.lcy0x1.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * @author arthur
 * @Date 2020-9-24
 */
public class NBTObj {

    public static final String BASE = "_base";
    public static final int TYPE_STRING = 8;
    public final CompoundNBT tag;

    public NBTObj() {
        tag = new CompoundNBT();
    }

    public NBTObj(ItemStack is, String key) {
        tag = is.getOrCreateTagElement(key);
    }

    public NBTObj(CompoundNBT data) {
        tag = data;
    }

    private NBTObj(NBTObj parent, String key) {
        boolean old = parent.tag.contains(key);
        tag = parent.tag.getCompound(key);
        if (!old)
            parent.tag.put(key, tag);
    }

    public void fromBlockPos(BlockPos pos) {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
    }

    public <T> NBTList<T> getList(String key) {
        return new NBTList<>(this, key);
    }

    public ResourceLocation getRL(String key) {
        return new ResourceLocation(tag.getString(key));
    }

    public NBTObj getSub(String key) {
        return new NBTObj(this, key);
    }

    public BlockPos toBlockPos() {
        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");
        return new BlockPos(x, y, z);
    }

}
