package com.hikarishima.lightland.command;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.spell.internal.AbstractSpell;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public class MagicCommand extends BaseCommand {

    private static final String ID_SPELL_SLOT = "chat.show_spell_slot";

    public MagicCommand(LiteralArgumentBuilder<CommandSource> lightland) {
        super(lightland, "magic");
    }

    public void register() {
        reg("sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    MagicHandler handler = MagicHandler.get(e);
                    PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        reg("debug_sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    MagicHandler handler = MagicHandler.get(e);
                    PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.DEBUG, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        reg("set_spell", getPlayer()
                .then(Commands.argument("spell", RegistryParser.SPELL)
                        .executes(withPlayer((context, e) -> {
                            ItemStack stack = e.getMainHandItem();
                            Spell<?, ?> spell = context.getArgument("spell", AbstractSpell.class).cast();
                            ServerWorld world = context.getSource().getLevel();
                            if (spell == null || stack.isEmpty() ||
                                    !(stack.getItem() instanceof MagicScroll) ||
                                    spell.getConfig(world).type != ((MagicScroll) stack.getItem()).type) {
                                send(context, WRONG_ITEM);
                                return 0;
                            }
                            MagicScroll.initItemStack(spell, stack);
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

        reg("add_spell_slot", getPlayer()
                .then(Commands.argument("slot", IntegerArgumentType.integer(0, 10))
                        .executes(withPlayer((context, e) -> {
                            MagicHandler handler = MagicHandler.get(e);
                            int slot = context.getArgument("slot", Integer.class);
                            handler.magicAbility.spell_level += slot;
                            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.MAGIC_ABILITY, handler));
                            send(context, Translator.get(ID_SPELL_SLOT, handler.magicAbility.getMaxSpellSlot()));
                            return 1;
                        }))));

        reg("master_element", getPlayer()
                .then(Commands.argument("elem", RegistryParser.ELEMENT)
                        .executes(withPlayer((context, e) -> {
                            MagicHandler handler = MagicHandler.get(e);
                            MagicElement elem = context.getArgument("elem", MagicElement.class);
                            handler.magicHolder.addElementalMastery(elem);
                            PacketHandler.toClient(e, new ToClientMsg(ToClientMsg.Action.ALL, handler));
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));

    }

}
