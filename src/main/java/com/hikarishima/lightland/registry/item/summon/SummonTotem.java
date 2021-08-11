package com.hikarishima.lightland.registry.item.summon;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SummonTotem<E extends LivingEntity> extends Item {

    public final EntityType<E> type;

    public SummonTotem(Properties props, EntityType<E> type) {
        super(props.stacksTo(1));
        this.type = type;
    }

    @Override
    public final ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        if (!stack.getOrCreateTag().contains("entity"))
            return super.useOn(context);
        if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
            ServerWorld world = (ServerWorld) context.getLevel();
            Team team = context.getPlayer().getTeam();
            if (team != null) {
                E e = type.create(
                        world,
                        stack.getOrCreateTagElement("entity"),
                        null,
                        context.getPlayer(),
                        context.getClickedPos(),
                        SpawnReason.MOB_SUMMONED,
                        false,
                        false
                );
                if (e != null) {
                    e.moveTo(context.getClickLocation());
                    team.getPlayers().add(e.getScoreboardName());
                    specialAction(e);
                    world.addFreshEntity(e);
                }
            }
        }
        return super.useOn(context);
    }

    protected void specialAction(E e) {
    }

    protected boolean canCatchEntity(E e) {
        return true;
    }

    @Override
    public final ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (canCatch(stack, player, target)) {
            if (!player.level.isClientSide()) {
                CompoundNBT tag = target.serializeNBT();
                stack.getOrCreateTag().put("entity", tag);
                target.remove();
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @SuppressWarnings("unchecked")
    private boolean canCatch(ItemStack stack, PlayerEntity player, LivingEntity target) {
        if (target.getType() != type)
            return false;
        if (target.getTeam() == null || player.getTeam() == target.getTeam())
            return false;
        if (stack.getOrCreateTag().contains("entity"))
            return false;
        return canCatchEntity((E) target);
    }

}
