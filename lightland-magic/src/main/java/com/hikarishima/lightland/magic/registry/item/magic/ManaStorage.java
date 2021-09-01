package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.registry.item.FoiledItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ManaStorage extends FoiledItem {

    public static final int ARCANE_COST = 16;

    public final Item container;
    public final int mana;

    public ManaStorage(Properties props, Item container, int mana) {
        super(props);
        this.container = container;
        this.mana = mana;
    }

    public ItemStack finishUsingItem(ItemStack stack, World w, LivingEntity e) {
        if (e instanceof PlayerEntity) {
            if (stack.isEdible()) {
                MagicHandler.get((PlayerEntity) e).magicAbility.giveMana(mana);
                MagicHandler.get((PlayerEntity) e).magicAbility.addSpellLoad(-mana);
            }
        }
        return super.finishUsingItem(stack, w, e);
    }

}
