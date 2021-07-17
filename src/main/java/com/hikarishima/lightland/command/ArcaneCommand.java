package com.hikarishima.lightland.command;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.item.arcane.ArcaneAxe;
import com.hikarishima.lightland.item.arcane.ArcaneSword;
import com.hikarishima.lightland.item.arcane.internal.Arcane;
import com.hikarishima.lightland.item.arcane.internal.ArcaneItemCraftHelper;
import com.hikarishima.lightland.item.arcane.internal.ArcaneItemUseHelper;
import com.hikarishima.lightland.item.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ArcaneCommand {

    private static final ITextComponent PLAYER_NOT_FOUND = Translator.get("chat.player_not_found");
    private static final ITextComponent ACTION_SUCCESS = Translator.get("chat.action_success");
    private static final ITextComponent WRONG_ITEM = Translator.get("chat.wrong_item");

    private static final String ID_LIST = "chat.list_arcane_type";
    private static final String ID_LIST_ENTRY = "chat.list_arcane_type.item";
    private static final String ID_LIST_LOCKED = "chat.list_arcane_type.locked";
    private static final String ID_LIST_UNLOCKED = "chat.list_arcane_type.unlocked";
    private static final String ID_GET_ARCANE_MANA = "chat.show_arcane_mana";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        dispatcher.register(getPlayer("arcane", "unlock")
                .then(Commands.argument("type", RegistryParser.ARCANE_TYPE))
                .executes(withPlayer((context, e) -> {
                    ArcaneType type = context.getArgument("type", ArcaneType.class);
                    MagicHandler magic = MagicHandler.get(e);
                    magic.unlockArcaneType(type);
                    return 1;
                })));

        dispatcher.register(getPlayer("arcane", "list")
                .executes(withPlayer((context, e) -> {
                    MagicHandler magic = MagicHandler.get(e);
                    List<ITextComponent> comps = new ArrayList<>();
                    for (ArcaneType type : MagicRegistry.ARCANE_TYPE.getValues()) {
                        boolean bool = magic.isArcaneTypeUnlocked(type);
                        ITextComponent lock = Translator.get(bool ? ID_LIST_UNLOCKED : ID_LIST_LOCKED);
                        comps.add(Translator.get(ID_LIST_ENTRY, type, lock));
                    }
                    ITextComponent comp = Translator.get(ID_LIST, comps);
                    send(context, comp);
                    return 1;
                })));

        dispatcher.register(getPlayer("arcane", "set_max_mana")
                .then(Commands.argument("number", IntegerArgumentType.integer()))
                .executes(withPlayer((context, e) -> {
                    MagicHandler.get(e).arcane_mana_max = context.getArgument("number", Integer.class);
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        dispatcher.register(getPlayer("arcane", "set_mana")
                .then(Commands.argument("number", IntegerArgumentType.integer()))
                .executes(withPlayer((context, e) -> {
                    MagicHandler.get(e).arcane_mana = context.getArgument("number", Integer.class);
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        dispatcher.register(getPlayer("arcane", "get_mana")
                .executes(withPlayer((context, e) -> {
                    MagicHandler magic = MagicHandler.get(e);
                    send(context, Translator.get(ID_GET_ARCANE_MANA, magic.arcane_mana, magic.arcane_mana_max));
                    return 1;
                })));

        dispatcher.register(getPlayer("arcane", "set_arcane")
                .then(Commands.argument("arcane", RegistryParser.ARCANE))
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
                })));

        dispatcher.register(getPlayer("arcane", "get_arcane")
                .executes(withPlayer((context, e) -> {
                    ItemStack stack = e.getMainHandItem();
                    if (!ArcaneItemUseHelper.isArcaneItem(stack)) {
                        send(context, WRONG_ITEM);
                        return 0;
                    }
                    List<ITextComponent> list = new ArrayList<>();
                    for (Arcane a : ArcaneItemCraftHelper.getAllArcanesOnItem(stack)) {
                        list.add(Translator.get(ID_LIST_ENTRY, a.type, a));
                    }
                    send(context, Translator.get(ID_LIST, list));
                    return 1;
                })));


    }

    private static LiteralArgumentBuilder<CommandSource> getPlayer(String cls, String act) {
        LiteralArgumentBuilder<CommandSource> c = Commands.literal("lightland");
        c = c.then(Commands.literal(cls));
        c = c.then(Commands.literal(act));
        c = c.requires(e -> e.hasPermission(2));
        c = c.then(Commands.argument("player", GameProfileArgument.gameProfile()));
        return c;
    }

    private static Command<CommandSource> withPlayer(BiFunction<CommandContext<CommandSource>, PlayerEntity, Integer> then) {
        return (context) -> {
            GameProfile profile = context.getArgument("player", GameProfile.class);
            PlayerEntity e = context.getSource().getLevel().getPlayerByUUID(profile.getId());
            if (e == null) {
                send(context, PLAYER_NOT_FOUND);
                return 0;
            }
            return then.apply(context, e);
        };
    }

    private static void send(CommandContext<CommandSource> context, ITextComponent comp) {
        context.getSource().getServer().getPlayerList().broadcastMessage(comp, ChatType.GAME_INFO, context.getSource().getEntity().getUUID());
    }

}
