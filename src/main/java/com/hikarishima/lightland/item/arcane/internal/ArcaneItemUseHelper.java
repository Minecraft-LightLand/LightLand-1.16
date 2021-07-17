package com.hikarishima.lightland.item.arcane.internal;

import com.hikarishima.lightland.event.forge.ItemUseEventHandler;
import com.hikarishima.lightland.item.arcane.ArcaneAxe;
import com.hikarishima.lightland.item.arcane.ArcaneSword;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ArcaneItemUseHelper implements ItemUseEventHandler.ItemClickHandler {

    public static final ArcaneItemUseHelper INSTANCE = new ArcaneItemUseHelper();

    private ArcaneItemUseHelper() {
        ItemUseEventHandler.LIST.add(this);
    }

    public static boolean isArcaneItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ArcaneSword || item instanceof ArcaneAxe;
    }

    public static boolean executeArcane(
            PlayerEntity player, MagicHandler magic,
            ItemStack stack, ArcaneType type, LivingEntity target) {
        if (!magic.isArcaneTypeUnlocked(type))
            return false;
        CompoundNBT tag = stack.getTagElement("arcane");
        if (tag == null || !tag.contains(type.getRegistryName().toString()))
            return false;
        String str = tag.getString(type.getRegistryName().toString());
        ResourceLocation rl = new ResourceLocation(str);
        Arcane arcane = MagicRegistry.ARCANE.getValue(rl);
        if (arcane == null || arcane.cost > magic.arcane_mana)
            return false;
        if (arcane.activate(player, magic, stack, target)) {
            magic.magic_mana -= arcane.cost;
            return true;
        }
        return false;
    }

    public static void rightClickAxe(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("arcane");
        tag.putBoolean("charged", !tag.getBoolean("charged"));
    }

    public static boolean isAxeCharged(ItemStack stack) {
        return stack.getOrCreateTagElement("arcane").getBoolean("charged");
    }

    private static void handleLeftClickEvent(PlayerInteractEvent event, LivingEntity target) {
        ItemStack stack = event.getItemStack();
        PlayerEntity player = event.getPlayer();
        MagicHandler magic = MagicHandler.get(player);
        if (stack.getItem() instanceof ArcaneAxe) {
            ArcaneType type = isAxeCharged(stack) ? ArcaneType.DUBHE : ArcaneType.MEGREZ;
            if (executeArcane(player, magic, stack, type, target)) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        } else if (stack.getItem() instanceof ArcaneSword) {
            if (executeArcane(player, magic, stack, ArcaneType.ALIOTH, target)) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    private static void handleRightClickEvent(PlayerInteractEvent event) {
        if (event.getItemStack().getItem() instanceof ArcaneAxe) {
            rightClickAxe(event.getItemStack());
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        } else if (event.getItemStack().getItem() instanceof ArcaneSword) {
            if (executeArcane(event.getPlayer(),
                    MagicHandler.get(event.getPlayer()),
                    event.getItemStack(), ArcaneType.ALKAID, event.getEntityLiving())) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    @Override
    public boolean predicate(ItemStack stack, Class<? extends PlayerEvent> cls, PlayerEvent event) {
        return isArcaneItem(stack);
    }

    @Override
    public void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleLeftClickEvent(event, null);
    }

    @Override
    public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        handleLeftClickEvent(event, null);
    }

    @Override
    public void onPlayerLeftClickEntity(AttackEntityEvent event) {
        float charge = event.getPlayer().getAttackStrengthScale(0.5f);
        if (event.getEntityLiving() != null && charge > 0.9f) {
            MagicHandler.get(event.getPlayer()).giveArcaneMana(1);
        }
    }

    @Override
    public void onCriticalHit(CriticalHitEvent event) {

        PlayerEntity player = event.getPlayer();
        MagicHandler magic = MagicHandler.get(player);
        ItemStack stack = player.getMainHandItem();
        Entity e = event.getTarget();
        LivingEntity le = e instanceof LivingEntity ? (LivingEntity) e : null;
        ArcaneType type = null;
        boolean cr = event.isVanillaCritical();
        if (stack.getItem() instanceof ArcaneAxe) {
            boolean ch = isAxeCharged(stack);
            type = cr ? ch ? ArcaneType.MERAK : ArcaneType.PHECDA : ch ? ArcaneType.DUBHE : ArcaneType.MEGREZ;
        } else if (stack.getItem() instanceof ArcaneSword) {
            type = cr ? ArcaneType.MIZAR : ArcaneType.ALIOTH;
        }
        if (type != null)
            executeArcane(player, magic, stack, type, le);
    }

    @Override
    public void onPlayerRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        handleRightClickEvent(event);
    }

    @Override
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleRightClickEvent(event);
    }

    @Override
    public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        handleRightClickEvent(event);
    }

}
