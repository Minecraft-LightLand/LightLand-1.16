package com.hikarishima.lightland.command;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.internal.*;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.registry.item.magic.ArcaneAxe;
import com.hikarishima.lightland.registry.item.magic.ArcaneSword;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import java.util.function.BiFunction;

public class MagicCommand {

    private static final ITextComponent PLAYER_NOT_FOUND = Translator.get("chat.player_not_found");
    private static final ITextComponent ACTION_SUCCESS = Translator.get("chat.action_success");
    private static final ITextComponent WRONG_ITEM = Translator.get("chat.wrong_item");

    private static final String ID_LIST_LOCKED = "chat.list_arcane_type.locked";
    private static final String ID_LIST_UNLOCKED = "chat.list_arcane_type.unlocked";
    private static final String ID_GET_ARCANE_MANA = "chat.show_arcane_mana";

    private final LiteralArgumentBuilder<CommandSource> arcane;
    private final LiteralArgumentBuilder<CommandSource> magic;

    public MagicCommand(LiteralArgumentBuilder<CommandSource> lightland) {
        arcane = Commands.literal("arcane");
        magic = Commands.literal("magic");
        regArcane();
        regMagic();
        lightland.then(arcane);
        lightland.then(magic);
    }

    private static RequiredArgumentBuilder<CommandSource, ?> getPlayer() {
        return Commands.argument("player", GameProfileArgument.gameProfile());
    }

    private static Command<CommandSource> withPlayer(BiFunction<CommandContext<CommandSource>, ServerPlayerEntity, Integer> then) {
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

    private static void send(CommandContext<CommandSource> context, ITextComponent comp) {
        context.getSource().getServer().getPlayerList().broadcastMessage(comp, ChatType.CHAT, context.getSource().getEntity().getUUID());
    }

    public void regArcane() {
        reg(arcane, "unlock", getPlayer()
                .then(Commands.argument("type", RegistryParser.ARCANE_TYPE)
                        .executes(withPlayer((context, e) -> {
                            ArcaneType type = context.getArgument("type", ArcaneType.class);
                            MagicHandler magic = MagicHandler.get(e);
                            magic.magicAbility.unlockArcaneType(type);
                            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ARCANE_TYPE, magic));
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

        reg(arcane, "list", getPlayer()
                .executes(withPlayer((context, e) -> {
                    MagicHandler magic = MagicHandler.get(e);
                    TextComponent comps = new StringTextComponent("[");
                    for (ArcaneType type : MagicRegistry.ARCANE_TYPE.getValues()) {
                        boolean bool = magic.magicAbility.isArcaneTypeUnlocked(type);
                        ITextComponent lock = Translator.get(bool ? ID_LIST_UNLOCKED : ID_LIST_LOCKED);
                        comps.append(type.getDesc().append(": ").append(lock).append(",\n"));
                    }
                    ITextComponent comp = comps.append("]");
                    send(context, comp);
                    return 1;
                })));


        reg(arcane, "give_mana", getPlayer()
                .then(Commands.argument("number", IntegerArgumentType.integer())
                        .executes(withPlayer((context, e) -> {
                            ItemStack stack = e.getMainHandItem();
                            if (!ArcaneItemUseHelper.isArcaneItem(stack)) {
                                send(context, WRONG_ITEM);
                                return 0;
                            }
                            ArcaneItemUseHelper.addArcaneMana(stack, context.getArgument("number", Integer.class));
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

        reg(arcane, "get_mana", getPlayer()
                .executes(withPlayer((context, e) -> {
                    ItemStack stack = e.getMainHandItem();
                    if (!ArcaneItemUseHelper.isArcaneItem(stack)) {
                        send(context, WRONG_ITEM);
                        return 0;
                    }
                    IArcaneItem item = (IArcaneItem) stack.getItem();
                    int mana = ArcaneItemUseHelper.getArcaneMana(stack);
                    int max = item.getMaxMana(stack);
                    send(context, Translator.get(ID_GET_ARCANE_MANA, mana, max));
                    return 1;
                })));

        reg(arcane, "set_arcane", getPlayer()
                .then(Commands.argument("arcane", RegistryParser.ARCANE)
                        .executes(withPlayer((context, e) -> {
                            ItemStack stack = e.getMainHandItem();
                            Arcane arcane = context.getArgument("arcane", Arcane.class);
                            if (arcane == null || stack.isEmpty() ||
                                    arcane.type.weapon == ArcaneType.Weapon.AXE && !(stack.getItem() instanceof ArcaneAxe) ||
                                    arcane.type.weapon == ArcaneType.Weapon.SWORD && !(stack.getItem() instanceof ArcaneSword)) {
                                send(context, WRONG_ITEM);
                                return 0;
                            }
                            ArcaneItemCraftHelper.setArcaneOnItem(stack, arcane);
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

        reg(arcane, "get_arcane", getPlayer()
                .executes(withPlayer((context, e) -> {
                    ItemStack stack = e.getMainHandItem();
                    if (!ArcaneItemUseHelper.isArcaneItem(stack)) {
                        send(context, WRONG_ITEM);
                        return 0;
                    }
                    TextComponent list = new StringTextComponent("[");
                    for (Arcane a : ArcaneItemCraftHelper.getAllArcanesOnItem(stack)) {
                        list.append(a.type.getDesc().append(": ").append(a.getDesc()).append(",\n"));
                    }
                    send(context, list.append("]"));
                    return 1;
                })));
    }

    public void regMagic() {
        reg(magic, "sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    MagicHandler handler = MagicHandler.get(e);
                    PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        reg(magic, "debug_sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    MagicHandler handler = MagicHandler.get(e);
                    PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.DEBUG, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));
    }

    private <T extends ArgumentBuilder<CommandSource, T>> void reg(LiteralArgumentBuilder<CommandSource> arg, String act, ArgumentBuilder<CommandSource, T> builder) {
        arg.then(Commands.literal(act)
                .requires(e -> e.hasPermission(2))
                .then(builder));
    }

}
