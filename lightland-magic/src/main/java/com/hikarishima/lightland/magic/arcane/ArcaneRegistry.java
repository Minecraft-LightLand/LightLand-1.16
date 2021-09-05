package com.hikarishima.lightland.magic.arcane;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.magic.arcane.internal.IArcaneItem;
import com.hikarishima.lightland.magic.arcane.magic.*;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import net.minecraft.entity.LightLandFakeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;

import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class ArcaneRegistry {

    public static final int ARCANE_TIME = 600;

    public static final ThunderAxe MERAK_THUNDER = reg("thunder_axe", new ThunderAxe(10, 64f));
    public static final ThunderSword ALKAID_THUNDER = reg("thunder_sword", new ThunderSword(20, 64f));
    public static final WindBladeSword ALIOTH_WINDBLADE = reg("wind_blade", new WindBladeSword(5f, 1f, 64f));
    public static final MarkerSword ALIOTH_MARKER = reg("marker", new MarkerSword(30, 32));
    public static final DamageAxe DUBHE_DAMAGE = reg("damage_axe", new DamageAxe(100, 30));

    private static <T extends Arcane> T reg(String str, T a) {
        a.setRegistryName(LightLandMagic.MODID, str);
        return a;
    }


    public static BiConsumer<LivingEntity, Float> postDamage(ItemStack stack) {
        return (e, f) -> {
            if (stack.getItem() instanceof IArcaneItem) {
                ArcaneItemUseHelper.addArcaneMana(stack, (int) (float) f);
            }
            LightLandFakeEntity.addEffect(e, new EffectInstance(VanillaMagicRegistry.EFF_ARCANE.get(), ARCANE_TIME));
        };
    }
}
