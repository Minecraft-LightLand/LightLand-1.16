package com.hikarishima.lightland.npc.player;

import com.hikarishima.lightland.npc.token.MobKillToken;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SerialClass
public class QuestToClient extends PacketHandler.BaseSerialMsg {

    public static QuestToClient onKill(String quest_id) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("quest_id", quest_id);
        return new QuestToClient(Action.MOB_KILL, tag);
    }

    @SerialClass.SerialField
    public CompoundNBT tag;

    @SerialClass.SerialField
    public Action action;

    @Deprecated
    public QuestToClient() {

    }

    public QuestToClient(Action action, QuestHandler q) {
        this.action = action;
        tag = Automator.toTag(new CompoundNBT(), q);
    }

    private QuestToClient(Action action, CompoundNBT tag) {
        this.action = action;
        this.tag = tag;
    }

    public static void handle(QuestToClient msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        PlayerEntity pl = ctx.get().getSender();
        if (pl != null) {
            QuestHandler q = QuestHandler.get(pl);
            msg.action.cons.accept(q, msg.tag);
        }
    }

    public enum Action {
        ALL((q, tag) -> ExceptionHandler.run(() -> Automator.fromTag(tag, QuestHandler.class, q, f -> true))),
        DEBUG((q, tag) -> {
            CompoundNBT ctag = Automator.toTag(new CompoundNBT(), q);
            LogManager.getLogger().info("server quest data: " + tag);
            LogManager.getLogger().info("client quest data: " + ctag);
        }),
        MOB_KILL((q, tag) -> {
            MobKillToken token = q.getToken(tag.getString("quest_id"));
            if (token != null)
                token.onKill();
        });

        public final BiConsumer<QuestHandler, CompoundNBT> cons;

        Action(BiConsumer<QuestHandler, CompoundNBT> cons) {
            this.cons = cons;
        }


    }

}
