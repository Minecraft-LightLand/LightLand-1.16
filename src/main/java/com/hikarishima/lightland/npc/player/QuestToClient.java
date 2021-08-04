package com.hikarishima.lightland.npc.player;

import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.npc.token.MobKillToken;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SerialClass
public class QuestToClient extends PacketHandler.BaseSerialMsg {

    public static QuestToClient onKill(String quest_id) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("quest_id", quest_id);
        return new QuestToClient(Action.MOB_KILL, tag);
    }

    public static QuestToClient onReset(String quest_id) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("quest_id", quest_id);
        return new QuestToClient(Action.RESET, tag);
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
        PlayerEntity pl = Minecraft.getInstance().player;
        if (pl != null) {
            msg.action.cons.accept(msg.tag);
        }
    }

    public enum Action {
        ALL(tag -> QuestHandler.cacheSet(tag, false)),
        CLONE(tag -> QuestHandler.cacheSet(tag, true)),
        DEBUG((tag) -> {
            QuestHandler q = QuestHandler.get(Proxy.getPlayer());
            CompoundNBT ctag = Automator.toTag(new CompoundNBT(), q);
            ToServerMsg.sendDebugInfo("server quest data: " + tag, "client quest data: " + ctag);
        }),
        RESET((tag) -> {
            QuestHandler q = QuestHandler.get(Proxy.getPlayer());
            String str = tag.getString("quest_id");
            q.reset(str);
        }),
        MOB_KILL((tag) -> {
            QuestHandler q = QuestHandler.get(Proxy.getPlayer());
            MobKillToken token = q.getToken(tag.getString("quest_id"));
            if (token != null)
                token.onKill();
        });

        public final Consumer<CompoundNBT> cons;

        Action(Consumer<CompoundNBT> cons) {
            this.cons = cons;
        }


    }

}
