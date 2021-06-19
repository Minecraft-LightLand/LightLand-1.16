package com.lcy0x1.base;

import com.lcy0x1.core.util.SerialClass;
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
            extends SerialClass.RecSerializer<Rec, Inv>{

        public final ResourceLocation rl;
        public final IRecipeType<Rec> type;

        public RecType(ResourceLocation rl, Class<Rec> rec, IRecipeType<Rec> type) {
            super(rec);
            this.rl = rl;
            this.type = type;
        }

    }

    private final RecType<Rec, SRec, Inv> factory;

    public ResourceLocation id;

    public BaseRecipe(RecType<Rec, SRec, Inv> fac) {
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