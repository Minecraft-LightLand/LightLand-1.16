package com.hikarishima.lightland.mobspawn.config;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;

import java.util.HashMap;

@SerialClass
public class GeneralConfig {

    public static GeneralConfig getInstance() {
        return ConfigRecipe.getObject(Proxy.getWorld(), MagicRecipeRegistry.SPAWN, "general");
    }

    @SerialClass.SerialField
    public float enchant_factor;

    @SerialClass.SerialField
    public float armor_chance;

    @SerialClass.SerialField(generic = {String.class, Double.class})
    public HashMap<String,Double> weapon_chance;

}
