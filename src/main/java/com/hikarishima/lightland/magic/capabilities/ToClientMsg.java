package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SerialClass
public class ToClientMsg extends PacketHandler.BaseSerialMsg {

    @SerialClass.SerialField
    public Action action;

    @SerialClass.SerialField
    public CompoundNBT tag;

    @Deprecated
    public ToClientMsg() {

    }

    public ToClientMsg(Action action, MagicHandler handler) {
        this.action = action;
        this.tag = action.server.apply(handler);
    }

    public static void handle(ToClientMsg msg, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> msg.action.client.accept(msg.tag));
        context.get().setPacketHandled(true);
    }

    public enum Action {
        DEBUG((m) -> ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true)), (tag) -> {
            MagicHandler m = MagicHandler.get(Proxy.getPlayer());
            CompoundNBT comp = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true));
            LogManager.getLogger().info("client: " + comp.toString());
            LogManager.getLogger().info("server: " + tag.toString());
        }),
        ALL((m) -> ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true)), (tag) -> {
            MagicHandler m = MagicHandler.get(Proxy.getPlayer());
            m.reset();
            ExceptionHandler.run(() -> Automator.fromTag(tag, MagicHandler.class, m, f -> true));
            m.init();
        }), ARCANE_TYPE((m) -> m.magicAbility.arcane_type, (tag) -> {
            MagicAbility abi = MagicHandler.get(Proxy.getPlayer()).magicAbility;
            abi.arcane_type = tag;
            abi.arcane_manager = new NBTObj(tag);
        });

        public final Function<MagicHandler, CompoundNBT> server;
        public final Consumer<CompoundNBT> client;


        Action(Function<MagicHandler, CompoundNBT> server, Consumer<CompoundNBT> client) {
            this.server = server;
            this.client = client;
        }
    }

}
