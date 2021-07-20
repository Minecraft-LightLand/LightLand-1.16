package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SerialClass
public class ToClientMsg extends PacketHandler.BaseSerialMsg {

    public enum Action {
        DEBUG((m) -> ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true)), (tag) -> {
            MagicHandler m = MagicHandler.get(getPlayer());
            CompoundNBT comp = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true));
            LogManager.getLogger().info("client: " + comp.toString());
            LogManager.getLogger().info("server: " + tag.toString());
        }),
        ALL((m) -> ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true)), (tag) -> {
            MagicHandler m = MagicHandler.get(getPlayer());
            m.state = null;
            m.magicAbility = null;
            m.magicHolder = null;
            m.abilityPoints = null;
            ExceptionHandler.run(() -> Automator.fromTag(tag, MagicHandler.class, m, f -> true));
            m.init();
        }), ARCANE_TYPE((m) -> m.magicAbility.arcane_type, (tag) -> {
            MagicAbility abi = MagicHandler.get(getPlayer()).magicAbility;
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

    public Action action;
    public CompoundNBT tag;

    public ToClientMsg(Action action, MagicHandler handler) {
        this.action = action;
        this.tag = action.server.apply(handler);
    }

    public static void handle(ToClientMsg msg, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> msg.action.client.accept(msg.tag));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static ClientPlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

}
