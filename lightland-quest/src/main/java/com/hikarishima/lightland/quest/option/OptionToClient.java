package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void handle(OptionToClient msg, NetworkEvent.Context ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientHandle(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientHandle(OptionToClient msg, NetworkEvent.Context ctx) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        for (Object obj : msg.data) {
            IOptionComponent comp = (IOptionComponent) obj;
            comp.perform(player);
        }
    }

}
