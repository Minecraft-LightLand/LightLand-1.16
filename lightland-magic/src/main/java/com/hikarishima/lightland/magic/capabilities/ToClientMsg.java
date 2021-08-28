package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static void handle(ToClientMsg msg, NetworkEvent.Context context) {
        if (!Proxy.getClientPlayer().isAlive())
            return;
        msg.action.client.accept(msg.tag);
    }

    public static void reset(ServerPlayerEntity e, MagicHandler.Reset reset) {
        ToClientMsg msg = new ToClientMsg(Action.RESET, null);
        msg.tag.putInt("ordinal", reset.ordinal());
        PacketHandler.toClient(e, msg);
    }

    public enum Action {
        DEBUG((m) -> Automator.toTag(new CompoundNBT(), m), (tag) -> {
            MagicHandler m = MagicProxy.getHandler();
            CompoundNBT comp = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true));
            ToServerMsg.sendDebugInfo(tag, comp);
        }),
        ALL((m) -> Automator.toTag(new CompoundNBT(), m), tag -> MagicHandler.cacheSet(tag, false)),
        CLONE((m) -> Automator.toTag(new CompoundNBT(), m), tag -> MagicHandler.cacheSet(tag, true)),
        ARCANE_TYPE((m) -> m.magicAbility.arcane_type, (tag) -> {
            MagicAbility abi = MagicProxy.getHandler().magicAbility;
            abi.arcane_type = tag;
        }), MAGIC_ABILITY((m) -> Automator.toTag(new CompoundNBT(), m.magicAbility), (tag) -> {
            MagicHandler h = MagicProxy.getHandler();
            h.magicAbility = new MagicAbility(h);
            ExceptionHandler.run(() -> Automator.fromTag(tag, MagicAbility.class, h.magicAbility, f -> true));
            h.reInit();
        }), ABILITY_POINT((m) -> Automator.toTag(new CompoundNBT(), m.abilityPoints), (tag) -> {
            MagicHandler h = MagicProxy.getHandler();
            h.abilityPoints = new AbilityPoints(h);
            ExceptionHandler.run(() -> Automator.fromTag(tag, AbilityPoints.class, h.abilityPoints, f -> true));
            h.reInit();
        }), RESET(m -> new CompoundNBT(), tag -> {
            MagicHandler h = MagicProxy.getHandler();
            h.reset(MagicHandler.Reset.values()[tag.getInt("ordinal")]);
        });

        public final Function<MagicHandler, CompoundNBT> server;
        public final Consumer<CompoundNBT> client;


        Action(Function<MagicHandler, CompoundNBT> server, Consumer<CompoundNBT> client) {
            this.server = server;
            this.client = client;
        }
    }

}
