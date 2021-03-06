package com.hikarishima.lightland.quest.player;

import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.quest.token.MobKillToken;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Consumer;

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

    public static void handle(QuestToClient msg, NetworkEvent.Context ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientHandle(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientHandle(QuestToClient msg, NetworkEvent.Context ctx) {
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
            ToServerMsg.sendDebugInfo(tag, ctag);
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
