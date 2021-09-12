package com.hikarishima.lightland.event.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.command.BaseCommand;
import com.hikarishima.lightland.event.BaseJsonReloadListener;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SpriteManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class GenericEventHandler {

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSource> lightland = Commands.literal("lightland");
        for (Consumer<LiteralArgumentBuilder<CommandSource>> command : BaseCommand.LIST) {
            command.accept(lightland);
        }
        event.getDispatcher().register(lightland);
    }

    @SubscribeEvent
    public void onAddReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(new BaseJsonReloadListener(map -> {
            SpriteManager.CACHE.clear();
            SpriteManager.CACHE.putAll(map);
        }));
    }

}
