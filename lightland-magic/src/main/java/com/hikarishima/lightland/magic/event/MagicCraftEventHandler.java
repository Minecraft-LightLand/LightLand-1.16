package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftEventHandler {

    @SubscribeEvent
    public void onAnvilChange(AnvilUpdateEvent event) {
        Proxy.getWorld().getRecipeManager().getAllRecipesFor(MagicRecipeRegistry.RT_ANVIL)
                .stream().filter(e -> e.matches(event)).findFirst().ifPresent(e -> e.setEvent(event));
    }

}
