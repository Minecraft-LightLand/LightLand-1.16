package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

@SerialClass
public class OptionToServer extends PacketHandler.BaseSerialMsg {

    @SerialClass.SerialField
    public Object[] data;

    @Deprecated
    public OptionToServer() {

    }

    public OptionToServer(Option option) {
        data = option.getComponents().toArray();
    }

    public static void handle(OptionToServer msg, NetworkEvent.Context ctx) {
        ServerPlayerEntity player = ctx.getSender();
        if (player != null) {
            for (Object obj : msg.data) {
                IOptionComponent comp = (IOptionComponent) obj;
                if (!comp.test(player))
                    return;
            }
            for (Object obj : msg.data) {
                IOptionComponent comp = (IOptionComponent) obj;
                comp.perform(player);
            }
            PacketHandler.toClient(player, new OptionToClient(msg));
        }
    }


}
