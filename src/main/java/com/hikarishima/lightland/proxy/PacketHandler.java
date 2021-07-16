package com.hikarishima.lightland.proxy;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.magic.gui.DisEnchantContainer;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    public interface DataCont {

        IIntArray getData();

    }

    public static class IntMsg {

        public static IntMsg decode(PacketBuffer packet) {
            return new IntMsg(packet.readInt(), packet.readVarInt(), packet.readVarInt());
        }

        private final int wid, ind, val;

        public IntMsg(int id, int index, int value) {
            wid = id;
            ind = index;
            val = value;
        }

        public void encode(PacketBuffer packet) {
            packet.writeInt(wid);
            packet.writeVarInt(ind);
            packet.writeVarInt(val);
        }

        public void handle(Supplier<NetworkEvent.Context> sup) {
            NetworkEvent.Context ctx = sup.get();
            ctx.enqueueWork(() -> this.handle(ctx));
            ctx.setPacketHandled(true);
        }

        private void handle(NetworkEvent.Context ctx) {
            ServerPlayerEntity pl = ctx.getSender();
            if (pl == null)
                return;
            Container c = pl.containerMenu;
            if (c != null && c.containerId == wid && c instanceof DataCont)
                ((DataCont) c).getData().set(ind, val);
        }
    }

    @SerialClass
    public static class BaseSerialMsg {

        @SerialClass.SerialField
        public int wid;

        @Deprecated
        public BaseSerialMsg() {

        }

        public BaseSerialMsg(int wid) {
            this.wid = wid;
        }

    }

    public interface SerialMsgCont<T extends BaseSerialMsg> {

        void handle(T t);

    }

    private static final ResourceLocation NAME = new ResourceLocation(LightLand.MODID, "main");
    private static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(NAME, () -> LightLand.NETWORK_VERSION, LightLand.NETWORK_VERSION::equals, LightLand.NETWORK_VERSION::equals);

    private static int id = 0;

    public static void registerPackets() {
        reg(IntMsg.class, IntMsg::encode, IntMsg::decode, IntMsg::handle);
        reg(DisEnchantContainer.Msg.class, DisEnchantContainer.class);
    }

    public static <T> void send(T msg) {
        CH.sendToServer(msg);
    }

    public static <T> void toClient(T msg) {
        NetworkManager manager = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getConnection();
        NetworkDirection dir = NetworkDirection.PLAY_TO_CLIENT;
        CH.sendTo(msg, manager, dir);
    }

    private static <T> void reg(Class<T> cls, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
                                BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        CH.registerMessage(id++, cls, encoder, decoder, handler);
    }

    private static <T extends BaseSerialMsg, C extends SerialMsgCont<T>> void reg(Class<T> cls, Class<C> cont) {
        reg(cls, (msg, p) -> Serializer.to(p, msg), (p) -> Serializer.from(p, cls, null), (t, s) -> handle(t, cont, s.get()));
    }

    @SuppressWarnings("unchecked")
    private static <T extends BaseSerialMsg, C extends SerialMsgCont<T>> void handle(T msg, Class<C> cls, NetworkEvent.Context ctx) {
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

}
