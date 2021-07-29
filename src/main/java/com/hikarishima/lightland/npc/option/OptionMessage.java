package com.hikarishima.lightland.npc.option;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@SerialClass
public class OptionMessage extends PacketHandler.BaseSerialMsg {

    @SerialClass.SerialField
    public Object[] data;

    public OptionMessage(Option option){
        data = option.getComponents().toArray();
    }

    public static void handle(OptionMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();
        for (Object obj : msg.data){
            IOptionComponent comp = (IOptionComponent) obj;
            comp.perform(player);
        }
        ctx.get().setPacketHandled(true);
    }


}
