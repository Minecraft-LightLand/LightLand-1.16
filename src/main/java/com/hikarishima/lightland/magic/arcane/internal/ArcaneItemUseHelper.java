package com.hikarishima.lightland.magic.arcane.internal;

import com.hikarishima.lightland.event.forge.ItemUseEventHandler;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.registry.item.ArcaneAxe;
import com.hikarishima.lightland.registry.item.ArcaneSword;
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
        if (!magic.magicAbility.isArcaneTypeUnlocked(type))
            return false;
        CompoundNBT tag = stack.getTagElement("arcane");
        if (tag == null || !tag.contains(type.getID()))
            return false;
        String str = tag.getString(type.getID());
        ResourceLocation rl = new ResourceLocation(str);
        Arcane arcane = MagicRegistry.ARCANE.getValue(rl);
        if (arcane == null || arcane.cost > magic.magicAbility.getArcaneMana())
            return false;
        if (arcane.activate(player, magic, stack, target)) {
            magic.magicAbility.giveArcaneMana(-arcane.cost);
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

    private static void handleLeftClickEvent(ItemStack stack, PlayerInteractEvent event, LivingEntity target) {
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

    private static void handleRightClickEvent(ItemStack stack, PlayerInteractEvent event) {
        boolean cancellable = event.isCancelable();
        if (stack.getItem() instanceof ArcaneAxe) {
            rightClickAxe(stack);
            if (cancellable) event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        } else if (stack.getItem() instanceof ArcaneSword) {
            if (executeArcane(event.getPlayer(),
                    MagicHandler.get(event.getPlayer()),
                    stack, ArcaneType.ALKAID, event.getEntityLiving())) {
                if (cancellable) event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    @Override
    public boolean predicate(ItemStack stack, Class<? extends PlayerEvent> cls, PlayerEvent event) {
        return isArcaneItem(stack);
    }

    @Override
    public void onPlayerLeftClickEmpty(ItemStack stack, PlayerInteractEvent.LeftClickEmpty event) {
        handleLeftClickEvent(stack, event, null);
    }

    @Override
    public void onPlayerLeftClickBlock(ItemStack stack, PlayerInteractEvent.LeftClickBlock event) {
        handleLeftClickEvent(stack, event, null);
    }

    @Override
    public void onPlayerLeftClickEntity(ItemStack stack, AttackEntityEvent event) {
        float charge = event.getPlayer().getAttackStrengthScale(0.5f);
        if (event.getEntityLiving() != null && charge > 0.9f) {
            MagicHandler.get(event.getPlayer()).magicAbility.giveArcaneMana(1);
        }
    }

    @Override
    public void onCriticalHit(ItemStack stack, CriticalHitEvent event) {
        PlayerEntity player = event.getPlayer();
        MagicHandler magic = MagicHandler.get(player);
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
    public void onPlayerRightClickEmpty(ItemStack stack, PlayerInteractEvent.RightClickEmpty event) {
        handleRightClickEvent(stack, event);
    }

    @Override
    public void onPlayerRightClickBlock(ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        handleRightClickEvent(stack, event);
    }

    @Override
    public void onPlayerRightClickEntity(ItemStack stack, PlayerInteractEvent.EntityInteract event) {
        handleRightClickEvent(stack, event);
    }

}
