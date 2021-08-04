package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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

    public static void handle(ToClientMsg msg, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> msg.action.client.accept(msg.tag));
        context.get().setPacketHandled(true);
    }

    public static void reset(ServerPlayerEntity e, MagicHandler.Reset reset) {
        ToClientMsg msg = new ToClientMsg(Action.RESET, null);
        msg.tag.putInt("ordinal", reset.ordinal());
        PacketHandler.toClient(e, msg);
    }

    public enum Action {
        DEBUG((m) -> Automator.toTag(new CompoundNBT(), m), (tag) -> {
            MagicHandler m = MagicHandler.get(Proxy.getPlayer());
            CompoundNBT comp = ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, m, f -> true));
            ToServerMsg.sendDebugInfo("server: " + tag, "client: " + comp);
        }),
        ALL((m) -> Automator.toTag(new CompoundNBT(), m), tag -> MagicHandler.cacheSet(tag, false)),
        CLONE((m) -> Automator.toTag(new CompoundNBT(), m), tag -> MagicHandler.cacheSet(tag, true)),
        ARCANE_TYPE((m) -> m.magicAbility.arcane_type, (tag) -> {
            MagicAbility abi = MagicHandler.get(Proxy.getPlayer()).magicAbility;
            abi.arcane_type = tag;
        }), MAGIC_ABILITY((m) -> Automator.toTag(new CompoundNBT(), m.magicAbility), (tag) -> {
            MagicHandler h = MagicHandler.get(Proxy.getPlayer());
            h.magicAbility = new MagicAbility(h);
            ExceptionHandler.run(() -> Automator.fromTag(tag, MagicAbility.class, h.magicAbility, f -> true));
        }), ABILITY_POINT((m) -> Automator.toTag(new CompoundNBT(), m.abilityPoints), (tag) -> {
            MagicHandler h = MagicHandler.get(Proxy.getPlayer());
            h.abilityPoints = new AbilityPoints(h);
            ExceptionHandler.run(() -> Automator.fromTag(tag, AbilityPoints.class, h.abilityPoints, f -> true));
            h.abilityPoints.updateAttribute();
        }), RESET(m -> new CompoundNBT(), tag -> {
            MagicHandler h = MagicHandler.get(Proxy.getPlayer());
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
