package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.capabilities.weight.WeightCalculator;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.mobspawn.config.ArmorConfig;
import com.hikarishima.lightland.mobspawn.config.WeaponConfig;
import com.hikarishima.lightland.proxy.Proxy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof ArmorItem) {
            int weight = WeightCalculator.getWeight(event.getItemStack());
            event.getToolTip().add(Translator.get("tooltip.weight", weight));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSpecialSpawnEvent(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getWorld().isClientSide())
            return;
        if (Proxy.getWorld() == null) {
            return;
        } else {
            Proxy.getWorld().getRecipeManager();
        }
        Entity e = event.getEntity();
        if (e instanceof MobEntity) {
            MobEntity mob = (MobEntity) e;
            float diff = event.getWorld().getCurrentDifficultyAt(mob.blockPosition()).getEffectiveDifficulty();
            Optional.ofNullable(ArmorConfig.getInstance()).ifPresent(x -> x.fillEntity(mob, diff, new Random()));
            Optional.ofNullable(WeaponConfig.getInstance()).ifPresent(x -> x.fillEntity(mob, diff, new Random()));
        }
    }

}
