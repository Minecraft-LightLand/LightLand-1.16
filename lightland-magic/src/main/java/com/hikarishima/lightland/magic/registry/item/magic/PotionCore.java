package com.hikarishima.lightland.magic.registry.item.magic;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.registry.item.FoiledItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PotionCore extends FoiledItem {

    public PotionCore(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        list.add(Translator.get("tooltip.potion.target."+MagicScroll.getTarget(stack).name().toLowerCase()));
        list.add(Translator.get("tooltip.potion.radius",MagicScroll.getRadius(stack)));
        PotionUtils.addPotionTooltip(stack, list, 1);
    }
}
