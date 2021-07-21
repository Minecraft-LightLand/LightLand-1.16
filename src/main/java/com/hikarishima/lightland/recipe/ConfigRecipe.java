package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;

@SerialClass
public class ConfigRecipe extends IConfigRecipe<ConfigRecipe> {

    public static final ResourceLocation SPELL = new ResourceLocation(LightLand.MODID, "spell");
    @SerialClass.SerialField(generic = {String.class, Object.class})
    public HashMap<String, Object> map = new HashMap<>();

    public ConfigRecipe(ResourceLocation id) {
        super(id, RecipeRegistry.RSM_CONFIG);
    }

    public static <T> T getObject(World world, ResourceLocation recipe, String id) {
        ConfigRecipe r = IConfigRecipe.getRecipe(world, ConfigRecipe.class, recipe);
        if (r == null)
            return null;
        return r.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String str) {
        return (T) map.get(str);
    }

}