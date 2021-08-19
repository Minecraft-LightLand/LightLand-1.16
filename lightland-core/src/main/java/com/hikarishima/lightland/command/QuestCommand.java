package com.hikarishima.lightland.command;

import com.hikarishima.lightland.npc.player.QuestHandler;
import com.hikarishima.lightland.npc.player.QuestToClient;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class QuestCommand extends BaseCommand {

    public QuestCommand(LiteralArgumentBuilder<CommandSource> lightland) {
        super(lightland, "quest");
    }

    public void register() {
        registerCommand("sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    QuestHandler handler = QuestHandler.get(e);
                    PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.ALL, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        registerCommand("debug_sync", getPlayer()
                .executes(withPlayer((context, e) -> {
                    QuestHandler handler = QuestHandler.get(e);
                    PacketHandler.toClient(e, new QuestToClient(QuestToClient.Action.DEBUG, handler));
                    send(context, ACTION_SUCCESS);
                    return 1;
                })));

        registerCommand("reset", getPlayer()
                .then(Commands.argument("quest_id", QuestParser.QUEST)
                        .executes(withPlayer((context, e) -> {
                            QuestHandler handler = QuestHandler.get(e);
                            String str = context.getArgument("quest_id", String.class);
                            if (str.equals(QuestParser.ALL))
                                str = "";
                            handler.reset(str);
                            PacketHandler.toClient(e, QuestToClient.onReset(str));
                            send(context, ACTION_SUCCESS);
                            return 1;
                        }))));
    }

}
