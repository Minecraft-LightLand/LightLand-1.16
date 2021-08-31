package com.hikarishima.lightland.magic.capabilities.weight;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;

import java.util.HashMap;

@SerialClass
public class ArmorWeight {

    public static ArmorWeight getInstance() {
        return ConfigRecipe.getObject(Proxy.getWorld(), MagicRecipeRegistry.WEIGHT, "armor_weight");
    }

    @SerialClass.SerialField(generic = {String.class, Entry.class})
    public HashMap<String, Entry> entries = new HashMap<>();

    @SerialClass.SerialField(generic = {String.class, String.class})
    public HashMap<String, String> materials = new HashMap<>();

    @SerialClass.SerialField
    public String[] suffixes;

    @SerialClass

    public static class Entry {

        @SerialClass.SerialField
        public int ingredient_weight;

        @SerialClass.SerialField
        public int extra_weight;

    }


}
