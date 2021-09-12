package com.hikarishima.lightland.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class BaseJsonReloadListener extends JsonReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Consumer<Map<ResourceLocation, JsonElement>> consumer;

    public BaseJsonReloadListener(Consumer<Map<ResourceLocation, JsonElement>> consumer) {
        super(GSON, "gui/coords");
        this.consumer = consumer;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager manager, IProfiler profiler) {
        consumer.accept(map);
    }

}
