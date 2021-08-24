package com.lcy0x1.base;

import com.lcy0x1.core.util.NBTObj;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BaseItem extends Item {

    public final ItemManager manager;

    public BaseItem(ItemManager manager) {
        super(manager.props);
        this.manager = manager;
    }

    public final void appendHoverText(ItemStack stack, World w, List<ITextComponent> list, ITooltipFlag flag) {
        NBTObj nbt = new NBTObj(stack, "_base");
        super.appendHoverText(stack, w, list, flag);
        for (ItemTextImpl text : manager.tooltips) {
            text.appendHoverText(stack, w, list, flag, nbt);
        }
    }

    public final void inventoryTick(ItemStack stack, World w, Entity e, int slot, boolean selected) {
        super.inventoryTick(stack, w, e, slot, selected);
        for (InvTickImpl impl : manager.invTicks) {
            impl.inventoryTick(stack, w, e, slot, selected);
        }
    }

    public final ActionResult<ItemStack> use(World w, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return manager.quick == null ? super.use(w, player, hand) : manager.quick.use(w, player, hand, stack);
    }

    public final boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return manager.weapon == null ? super.hurtEnemy(stack, target, attacker) : manager.weapon.hurtEnemy(stack, target, attacker);
    }

    public final ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return manager.quick == null ? super.interactLivingEntity(stack, player, entity, hand) : manager.quick.interactLivingEntity(stack, player, entity, hand);
    }

    public final boolean useOnRelease(ItemStack stack) {
        return manager.hold == null ? super.useOnRelease(stack) : manager.hold.useOnRelease(stack);
    }

    public void releaseUsing(ItemStack stack, World w, LivingEntity user, int time) {
        if (manager.hold != null)
            manager.hold.releaseUsing(stack, w, user, time);
        else super.releaseUsing(stack, w, user, time);
    }

    public final ActionResultType useOn(ItemUseContext context) {
        return manager.quick == null ? super.useOn(context) : manager.quick.useOn(context);
    }

    public final ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return manager.hold == null ? super.onItemUseFirst(stack, context) : manager.hold.onItemUseFirst(stack, context);
    }

    public final void onUseTick(World w, LivingEntity e, ItemStack stack, int time) {
        if (manager.hold != null)
            manager.hold.onUseTick(w, e, stack, time);
        else super.onUseTick(w, e, stack, time);
    }

    public final boolean doesSneakBypassUse(ItemStack stack, IWorldReader w, BlockPos pos, PlayerEntity player) {
        return manager.quick != null ? super.doesSneakBypassUse(stack, w, pos, player) : manager.quick.doesSneakBypassUse(stack, w, pos, player);
    }

    public final int getUseDuration(ItemStack stack) {
        return manager.hold == null ? super.getUseDuration(stack) : manager.hold.getUseDuration(stack);
    }

    public interface IImpl extends ProxyMethod {

    }

    public interface QuickUseImpl extends IImpl {

        ActionResult<ItemStack> use(World w, PlayerEntity player, Hand hand, ItemStack stack);

        ActionResultType useOn(ItemUseContext context);

        boolean doesSneakBypassUse(ItemStack stack, IWorldReader w, BlockPos pos, PlayerEntity player);

        ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand);

    }

    public interface LongUseImpl extends IImpl {

        boolean useOnRelease(ItemStack stack);

        ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context);

        void onUseTick(World w, LivingEntity e, ItemStack stack, int time);

        int getUseDuration(ItemStack stack);

        void releaseUsing(ItemStack stack, World w, LivingEntity user, int time);
    }

    public interface ItemTextImpl extends IImpl {

        void appendHoverText(ItemStack stack, World w, List<ITextComponent> list, ITooltipFlag flag, NBTObj nbt);

    }

    public interface InvTickImpl extends IImpl {

        void inventoryTick(ItemStack stack, World w, Entity e, int slot, boolean selected);

    }

    public interface WeaponImpl extends IImpl {

        boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker);

    }

    public static class ItemManager {

        private final Properties props;
        private final List<ItemTextImpl> tooltips = new ArrayList<>();
        private final List<InvTickImpl> invTicks = new ArrayList<>();
        private QuickUseImpl quick;
        private LongUseImpl hold;
        private WeaponImpl weapon;

        public ItemManager(Properties prop) {
            this.props = prop;
        }

        public ItemManager addImpl(IImpl impl) {
            if (impl instanceof ItemTextImpl)
                tooltips.add((ItemTextImpl) impl);
            if (impl instanceof InvTickImpl)
                invTicks.add((InvTickImpl) impl);
            for (Field f : ItemManager.class.getFields()) {
                if (IImpl.class.isAssignableFrom(f.getType()) && f.getType().isAssignableFrom(impl.getClass())) {
                    try {
                        f.setAccessible(true);
                        if (f.get(this) != null)
                            throw new RuntimeException("implementation conflict between " + f.get(this).getClass().getSimpleName() + " and " + impl.getClass().getSimpleName());
                        f.set(this, impl);
                    } catch (Exception e) {
                        throw new RuntimeException("security error", e);
                    }
                }
            }
            return this;
        }

    }

}
