package com.hikarishima.lightland.proxy;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.event.forge.ItemUseEventHandler;
import com.hikarishima.lightland.magic.capabilities.ToClientMsg;
import com.hikarishima.lightland.magic.capabilities.ToServerMsg;
import com.hikarishima.lightland.npc.option.OptionToClient;
import com.hikarishima.lightland.npc.option.OptionToServer;
import com.hikarishima.lightland.npc.player.QuestToClient;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {

    private static final ResourceLocation NAME = new ResourceLocation(LightLand.MODID, "main");
    private static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(NAME, () -> LightLand.NETWORK_VERSION, LightLand.NETWORK_VERSION::equals, LightLand.NETWORK_VERSION::equals);
    private static int id = 0;

    public static void registerPackets() {
        reg(IntMsg.class, IntMsg::encode, IntMsg::decode, IntMsg::handle, NetworkDirection.PLAY_TO_SERVER);
        reg(ItemUseEventHandler.Msg.class, ItemUseEventHandler.Msg::handle, NetworkDirection.PLAY_TO_SERVER);
        reg(ToClientMsg.class, ToClientMsg::handle, NetworkDirection.PLAY_TO_CLIENT);
        reg(ToServerMsg.class, ToServerMsg::handle, NetworkDirection.PLAY_TO_SERVER);
        reg(OptionToServer.class, OptionToServer::handle, NetworkDirection.PLAY_TO_SERVER);
        reg(OptionToClient.class, OptionToClient::handle, NetworkDirection.PLAY_TO_CLIENT);
        reg(QuestToClient.class, QuestToClient::handle, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void send(T msg) {
        CH.sendToServer(msg);
    }

    public static <T> void toClient(ServerPlayerEntity player, T msg) {
        NetworkManager manager = player.connection.getConnection();
        NetworkDirection dir = NetworkDirection.PLAY_TO_CLIENT;
        CH.sendTo(msg, manager, dir);
    }

    private static <T> void reg(Class<T> cls, BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder,
                                BiConsumer<T, Supplier<NetworkEvent.Context>> handler, NetworkDirection dire) {
        CH.registerMessage(id++, cls, encoder, decoder, handler, Optional.of(dire));
    }

    private static <T extends ContSerialMsg, C extends SerialMsgCont<T>> void reg(Class<T> cls, Class<C> cont, NetworkDirection dire) {
        reg(cls, (t, s) -> handle(t, cont, s.get()), dire);
    }

    private static <T extends BaseSerialMsg> void reg(Class<T> cls, BiConsumer<T, Supplier<NetworkEvent.Context>> handler, NetworkDirection dire) {
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

    public interface DataCont {

        IIntArray getData();

    }

    public interface SerialMsgCont<T extends ContSerialMsg> {

        void handle(T t);

    }

    public static class IntMsg {

        private final int wid, ind, val;

        public IntMsg(int id, int index, int value) {
            wid = id;
            ind = index;
            val = value;
        }

        public static IntMsg decode(PacketBuffer packet) {
            return new IntMsg(packet.readInt(), packet.readVarInt(), packet.readVarInt());
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
