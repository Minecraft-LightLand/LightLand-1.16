package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemCraftHelper;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.IArcaneItem;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.Proxy;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ArcaneAxe extends AxeItem implements IArcaneItem {

    private final int mana;

    public ArcaneAxe(IItemTier tier, float attack, float speed, Properties props, int mana) {
        super(tier, attack, speed, props);
        this.mana = mana;
    }

    public static void add(ItemStack stack, List<ITextComponent> list) {
        List<Arcane> arcane = ArcaneItemCraftHelper.getAllArcanesOnItem(stack);
        PlayerEntity pl = Proxy.getPlayer();
        MagicHandler handler = pl == null ? null : MagicHandler.get(pl);
        for (Arcane a : arcane) {
            boolean red = handler != null && !handler.magicAbility.isArcaneTypeUnlocked(a.type);
            TranslationTextComponent text = a.type.getDesc();
            if (red)
                text.withStyle(TextFormatting.RED);
            list.add(text.append(": ").append(a.getDesc()));
        }
    }

    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        add(stack, list);
    }

    public boolean isFoil(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.getBoolean("foil"))
            return true;
        return ArcaneItemUseHelper.isAxeCharged(stack);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        return true;
    }

    public boolean mineBlock(ItemStack stack, World w, BlockState state, BlockPos pos, LivingEntity user) {
        return true;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - 1.0 * ArcaneItemUseHelper.getArcaneMana(stack) / getMaxMana(stack);
    }

    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public int getMaxMana(ItemStack stack) {
        return mana;
    }

}
