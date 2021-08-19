package com.hikarishima.lightland.recipe;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SerialClass
public class ConfigRecipe extends IConfigRecipe<ConfigRecipe> {

    public static final ResourceLocation SPELL = new ResourceLocation(LightLand.MODID, "config_spell");
    public static final ResourceLocation PRODUCT_TYPE_DISPLAY = new ResourceLocation(LightLand.MODID, "config_product_type");
    public static final ResourceLocation DIALOG = new ResourceLocation(LightLand.MODID, "config_dialog");
    public static final ResourceLocation CHEM = new ResourceLocation(LightLand.MODID, "config_chemistry");

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
    public static <T> Stream<Map.Entry<String, T>> stream(World world, ResourceLocation recipe, Class<T> cls) {
        ConfigRecipe r = IConfigRecipe.getRecipe(world, ConfigRecipe.class, recipe);
        return r.map.entrySet().stream().filter(e -> cls.isInstance(e.getValue())).map(e -> (Map.Entry<String, T>) e);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String str) {
        return (T) map.get(str);
    }

}
