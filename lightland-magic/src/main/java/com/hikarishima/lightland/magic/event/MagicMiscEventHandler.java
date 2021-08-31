package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.weight.WeightCalculator;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.mobspawn.config.ArmorConfig;
import com.hikarishima.lightland.proxy.Proxy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicMiscEventHandler {

    @SubscribeEvent
    public void onAnvilChange(AnvilUpdateEvent event) {
        Proxy.getWorld().getRecipeManager().getAllRecipesFor(MagicRecipeRegistry.RT_ANVIL)
                .stream().filter(e -> e.matches(event)).findFirst().ifPresent(e -> e.setEvent(event));
    }

    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ArmorItem) {
            int weight = WeightCalculator.getWeight(event.getItemStack());
            event.getToolTip().add(Translator.get("tooltip.weight", weight));
        }
    }

    @SubscribeEvent
    public void onSpecialSpawnEvent(LivingSpawnEvent.SpecialSpawn event) {
        LivingEntity e = event.getEntityLiving();
        if (e instanceof MobEntity) {
            MobEntity mob = (MobEntity) e;
            float diff = event.getWorld().getCurrentDifficultyAt(mob.blockPosition()).getEffectiveDifficulty();
            ArmorConfig.getInstance().fillEntity(mob, diff, new Random());
        }

    }

}
