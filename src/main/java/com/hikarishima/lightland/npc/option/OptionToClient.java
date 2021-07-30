package com.hikarishima.lightland.npc.option;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@SerialClass
public class OptionToClient extends PacketHandler.BaseSerialMsg {

    @SerialClass.SerialField
    public Object[] data;

    @Deprecated
    public OptionToClient() {

    }

    public OptionToClient(OptionToServer msg) {
        data = msg.data;
    }

    public static void handle(OptionToClient msg, Supplier<NetworkEvent.Context> ctx) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        for (Object obj : msg.data) {
            IOptionComponent comp = (IOptionComponent) obj;
            comp.perform(player);
        }
        ctx.get().setPacketHandled(true);
    }

}
