package com.hikarishima.lightland.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class BaseCommand {

    public static final ITextComponent ACTION_SUCCESS = new TranslationTextComponent("lightland-core:chat.action_success");
    public static final ITextComponent PLAYER_NOT_FOUND = new TranslationTextComponent("lightland-core:chat.player_not_found");
    public static final ITextComponent WRONG_ITEM = new TranslationTextComponent("lightland-core:chat.wrong_item");

    public static final List<Consumer<LiteralArgumentBuilder<CommandSource>>> LIST = new ArrayList<>();

    public static RequiredArgumentBuilder<CommandSource, ?> getPlayer() {
        return Commands.argument("player", GameProfileArgument.gameProfile());
    }

    public static Command<CommandSource> withPlayer(BiFunction<CommandContext<CommandSource>, ServerPlayerEntity, Integer> then) {
        return (context) -> {
            GameProfileArgument.IProfileProvider profile = context.getArgument("player", GameProfileArgument.IProfileProvider.class);
            if (profile.getNames(context.getSource()).size() != 1) {
                send(context, PLAYER_NOT_FOUND);
                return 0;
            }
            GameProfile name = profile.getNames(context.getSource()).iterator().next();
            ServerPlayerEntity e = (ServerPlayerEntity) context.getSource().getLevel().getPlayerByUUID(name.getId());
            if (e == null) {
                send(context, PLAYER_NOT_FOUND);
                return 0;
            }
            return then.apply(context, e);
        };
    }

    public static void send(CommandContext<CommandSource> context, ITextComponent comp) {
        context.getSource().getServer().getPlayerList().broadcastMessage(comp, ChatType.CHAT, context.getSource().getEntity().getUUID());
    }

    private final LiteralArgumentBuilder<CommandSource> base;

    public BaseCommand(LiteralArgumentBuilder<CommandSource> lightland, String id) {
        base = Commands.literal(id);
        register();
        lightland.then(base);
    }

    public abstract void register();

    public <T extends ArgumentBuilder<CommandSource, T>> void registerCommand(String act, ArgumentBuilder<CommandSource, T> builder) {
        base.then(Commands.literal(act)
                .requires(e -> e.hasPermission(2))
                .then(builder));
    }


}
