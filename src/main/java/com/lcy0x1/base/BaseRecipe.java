package com.lcy0x1.base;

import com.lcy0x1.core.util.RecSerializer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class BaseRecipe<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends BaseRecipe.RecInv<SRec>>
        implements IRecipe<Inv> {

    public interface RecInv<R extends BaseRecipe<?, R, ?>> extends IInventory {

    }

    public static class RecType<Rec extends SRec, SRec extends BaseRecipe<?, SRec, Inv>, Inv extends RecInv<SRec>>
            extends RecSerializer<Rec, Inv> {

        public final IRecipeType<SRec> type;

        public RecType(Class<Rec> rec, IRecipeType<SRec> type) {
            super(rec);
            this.type = type;
        }

    }

    private final RecType<Rec, SRec, Inv> factory;

    public ResourceLocation id;

    public BaseRecipe(ResourceLocation id, RecType<Rec, SRec, Inv> fac) {
        this.id = id;
        factory = fac;
    }

    @Override
    public abstract boolean matches(Inv inv, World world);

    @Override
    public abstract ItemStack assemble(Inv inv);

    @Override
    public abstract boolean canCraftInDimensions(int r, int c);

    @Override
    public abstract ItemStack getResultItem();

    @Override
    public final ResourceLocation getId() {
        return id;
    }

    @Override
    public final IRecipeSerializer<?> getSerializer() {
        return factory;
    }

    @Override
    public final IRecipeType<?> getType() {
        return factory.type;
    }

}