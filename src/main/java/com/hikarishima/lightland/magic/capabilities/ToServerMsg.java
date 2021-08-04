package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SerialClass
public class ToServerMsg extends PacketHandler.BaseSerialMsg {

    public enum Action {
        HEX((handler, tag) -> {
            MagicHolder holder = handler.magicHolder;
            String str = tag.getString("product");
            MagicProduct<?, ?> prod = holder.getProduct(holder.getRecipe(new ResourceLocation(str)));
            CompoundNBT ctag = prod.tag.tag;
            Set<String> set = new HashSet<>(ctag.getAllKeys());
            for (String key : set) {
                ctag.remove(key);
            }
            CompoundNBT dtag = tag.getCompound("data");
            for (String key : dtag.getAllKeys()) {
                ctag.put(key, Objects.requireNonNull(dtag.get(key)));
            }
        }), DEBUG((handler, tag) -> {
            LogManager.getLogger().info(tag.getString("server"));
            LogManager.getLogger().info(tag.getString("client"));
        }), LEVEL((handler, tag) -> {
            AbilityPoints.LevelType.values()[tag.getInt("type")].doLevelUp(handler);
        }), PROFESSION((handler, tag) -> {
            Profession prof = MagicRegistry.PROFESSION.getValue(new ResourceLocation(tag.getString("id")));
            if (prof == null)
                return;
            handler.abilityPoints.setProfession(prof);
        }), ELEMENTAL((handler, tag) -> {
            MagicElement elem = MagicRegistry.ELEMENT.getValue(new ResourceLocation(tag.getString("id")));
            if (elem == null)
                return;
            if (handler.abilityPoints.canLevelElement() && handler.magicHolder.addElementalMastery(elem))
                handler.abilityPoints.levelElement();
        }), ARCANE((handler, tag) -> {
            ArcaneType type = MagicRegistry.ARCANE_TYPE.getValue(new ResourceLocation(tag.getString("id")));
            if (type == null)
                return;
            if (handler.abilityPoints.canLevelArcane() && !handler.magicAbility.isArcaneTypeUnlocked(type)) {
                handler.magicAbility.unlockArcaneType(type, false);
            }
        });

        private final BiConsumer<MagicHandler, CompoundNBT> cons;

        Action(BiConsumer<MagicHandler, CompoundNBT> cons) {
            this.cons = cons;
        }
    }

    public static void sendHexUpdate(MagicProduct<?, ?> prod) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("product", prod.recipe.id.toString());
        tag.put("data", prod.tag.tag);
        ToServerMsg msg = new ToServerMsg(Action.HEX, tag);
        PacketHandler.send(msg);
    }

    public static void sendDebugInfo(String s, String c) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("server", s);
        tag.putString("client", c);
        PacketHandler.send(new ToServerMsg(Action.DEBUG, tag));
    }

    public static void levelUpAbility(AbilityPoints.LevelType type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("type", type.ordinal());
        PacketHandler.send(new ToServerMsg(Action.LEVEL, tag));
    }

    public static void setProfession(Profession prof) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", prof.getID());
        PacketHandler.send(new ToServerMsg(Action.PROFESSION, tag));
    }

    public static void addElemMastery(MagicElement elem) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", elem.getID());
        PacketHandler.send(new ToServerMsg(Action.ELEMENTAL, tag));
    }

    public static void unlockArcaneType(ArcaneType type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", type.getID());
        PacketHandler.send(new ToServerMsg(Action.ARCANE, tag));
    }

    @SerialClass.SerialField
    public Action action;
    @SerialClass.SerialField
    public CompoundNBT tag;

    @Deprecated
    public ToServerMsg() {

    }

    private ToServerMsg(Action act, CompoundNBT tag) {
        this.action = act;
        this.tag = tag;
    }

    public static void handle(ToServerMsg msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();
        if (player == null)
            return;
        MagicHandler handler = MagicHandler.get(player);
        msg.action.cons.accept(handler, msg.tag);
        ctx.get().setPacketHandled(true);
    }

}
