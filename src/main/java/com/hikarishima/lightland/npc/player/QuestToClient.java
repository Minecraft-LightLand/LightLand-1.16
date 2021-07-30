package com.hikarishima.lightland.npc.player;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@SerialClass
public class QuestToClient extends PacketHandler.BaseSerialMsg {

    @SerialClass.SerialField
    public CompoundNBT tag;

    @Deprecated
    public QuestToClient() {

    }

    public QuestToClient(QuestHandler q) {
        tag = Automator.toTag(new CompoundNBT(), q);
    }

    public static void handle(QuestToClient msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        PlayerEntity pl = ctx.get().getSender();
        if (pl != null) {
            QuestHandler q = QuestHandler.get(pl);
            ExceptionHandler.run(() -> Automator.fromTag(msg.tag, QuestHandler.class, q, f -> true));
        }
    }

}
