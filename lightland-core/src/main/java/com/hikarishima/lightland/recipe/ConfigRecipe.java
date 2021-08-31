package com.hikarishima.lightland.recipe;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SerialClass
public class ConfigRecipe extends IConfigRecipe<ConfigRecipe> {

    @SerialClass.SerialField(generic = {String.class, Object.class})
    public HashMap<String, Object> map = new HashMap<>();

    public ConfigRecipe(ResourceLocation id) {
        super(id, RecipeRegistry.RSM_CONFIG.get());
    }

    public static <T> T getObject(World world, ResourceLocation recipe, String id) {
        ConfigRecipe r = IConfigRecipe.getRecipe(world, ConfigRecipe.class, recipe);
        if (r == null)
            return null;
        return r.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<Map.Entry<String, T>> stream(World world, ResourceLocation recipe, Class<T> cls) {
        ConfigRecipe r = IConfigRecipe.getRecipe(world, ConfigRecipe.class, recipe);
        return r.map.entrySet().stream().filter(e -> cls.isInstance(e.getValue())).map(e -> (Map.Entry<String, T>) e);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String str) {
        return (T) map.get(str);
    }

}
