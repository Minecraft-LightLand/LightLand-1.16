package com.hikarishima.lightland.item.arcane.internal;

import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public abstract class Arcane extends NamedEntry<Arcane> {

    public final ArcaneType type;

    public final int cost;

    public Arcane(ArcaneType type, int cost) {
        super(() -> MagicRegistry.ARCANE);
        this.type = type;
        this.cost = cost;
    }

    public abstract boolean activate(PlayerEntity player, MagicHandler magic, ItemStack stack, LivingEntity target);
}
