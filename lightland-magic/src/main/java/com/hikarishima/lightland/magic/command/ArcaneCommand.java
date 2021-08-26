package com.hikarishima.lightland.magic.command;

import com.hikarishima.lightland.command.BaseCommand;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.arcane.internal.*;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.registry.item.magic.ArcaneAxe;
import com.hikarishima.lightland.magic.registry.item.magic.ArcaneSword;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

public class ArcaneCommand extends BaseCommand {

    private static final String ID_LIST_LOCKED = "chat.list_arcane_type.locked";
    private static final String ID_LIST_UNLOCKED = "chat.list_arcane_type.unlocked";
    private static final String ID_GET_ARCANE_MANA = "chat.show_arcane_mana";

    public ArcaneCommand(LiteralArgumentBuilder<CommandSource> lightland) {
        super(lightland, "arcane");
    }

    public void register() {
        registerCommand("unlock", getPlayer()
                .then(Commands.argument("type", RegistryParser.ARCANE_TYPE)
                        .executes(withPlayer((context, e) -> {
                            ArcaneType type = context.getArgument("type", ArcaneType.class);
                            MagicHandler magic = MagicHandler.get(e);
                            magic.magicAbility.unlockArcaneType(type, true);
                            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ARCANE_TYPE, magic));
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

        registerCommand("list", getPlayer()
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


        registerCommand("give_mana", getPlayer()
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

        registerCommand("get_mana", getPlayer()
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

        registerCommand("set_arcane", getPlayer()
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

        registerCommand("get_arcane", getPlayer()
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

}
