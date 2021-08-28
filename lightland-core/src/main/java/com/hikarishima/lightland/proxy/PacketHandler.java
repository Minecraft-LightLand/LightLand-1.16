package com.hikarishima.lightland.proxy;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketHandler {

    private static final ResourceLocation NAME = new ResourceLocation(LightLand.MODID, "main");
    private static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(NAME, () -> LightLand.NETWORK_VERSION, LightLand.NETWORK_VERSION::equals, LightLand.NETWORK_VERSION::equals);
    private static int id = 0;

    public static <T> void send(T msg) {
        CH.sendToServer(msg);
    }

    public static <T> void toClient(ServerPlayerEntity player, T msg) {
        NetworkManager manager = player.connection.getConnection();
        NetworkDirection dir = NetworkDirection.PLAY_TO_CLIENT;
        CH.sendTo(msg, manager, dir);
    }

    public static <T> void distribute(Entity e, T msg) {
        CH.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), msg);
    }

    public static <T> void reg(Class<T> cls, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
                               BiConsumer<T, NetworkEvent.Context> handler, NetworkDirection dire) {
        CH.registerMessage(id++, cls, encoder, decoder, (t, sup) -> {
            NetworkEvent.Context u = sup.get();
            u.enqueueWork(() -> handler.accept(t, u));
            u.setPacketHandled(true);
        }, Optional.of(dire));
    }

    public static <T extends ContSerialMsg, C extends SerialMsgCont<T>> void reg(Class<T> cls, Class<C> cont, NetworkDirection dire) {
        reg(cls, (t, s) -> handle(t, cont, s), dire);
    }

    public static <T extends BaseSerialMsg> void reg(Class<T> cls, BiConsumer<T, NetworkEvent.Context> handler, NetworkDirection dire) {
        reg(cls, (msg, p) -> Serializer.to(p, msg), (p) -> Serializer.from(p, cls, null), handler, dire);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ContSerialMsg, C extends SerialMsgCont<T>> void handle(T msg, Class<C> cls, NetworkEvent.Context ctx) {
        if (ctx == null)
            return;
        ServerPlayerEntity pl = ctx.getSender();
        if (pl == null)
            return;
        Container c = pl.containerMenu;
        if (c != null && c.containerId == msg.wid && cls.isInstance(c)) {
            ((C) c).handle(msg);
        }
    }

    public interface SerialMsgCont<T extends ContSerialMsg> {

        void handle(T t);

    }

    @SerialClass
    public static class BaseSerialMsg {

    }

    @SerialClass
    public static class ContSerialMsg extends BaseSerialMsg {

        @SerialClass.SerialField
        public int wid;

        @Deprecated
        public ContSerialMsg() {

        }

        public ContSerialMsg(int wid) {
            this.wid = wid;
        }

    }

}
