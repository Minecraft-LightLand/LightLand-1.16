package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProduct;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.item.magic.MagicWand;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
            holder.checkUnlocks();
        }),
        DEBUG((handler, tag) -> {
            LogManager.getLogger().info("server: " + tag.getCompound("server"));
            LogManager.getLogger().info("client: " + tag.getCompound("client"));
        }),
        LEVEL((handler, tag) -> {
            AbilityPoints.LevelType.values()[tag.getInt("type")].doLevelUp(handler);
        }),
        PROFESSION((handler, tag) -> {
            Profession prof = MagicRegistry.PROFESSION.getValue(new ResourceLocation(tag.getString("id")));
            if (prof == null)
                return;
            handler.abilityPoints.setProfession(prof);
        }),
        ELEMENTAL((handler, tag) -> {
            MagicElement elem = MagicRegistry.ELEMENT.getValue(new ResourceLocation(tag.getString("id")));
            if (elem == null)
                return;
            if (handler.abilityPoints.canLevelElement() && handler.magicHolder.addElementalMastery(elem))
                handler.abilityPoints.levelElement();
        }),
        ARCANE((handler, tag) -> {
            ArcaneType type = MagicRegistry.ARCANE_TYPE.getValue(new ResourceLocation(tag.getString("id")));
            if (type == null)
                return;
            if (handler.abilityPoints.canLevelArcane() && !handler.magicAbility.isArcaneTypeUnlocked(type)) {
                handler.magicAbility.unlockArcaneType(type, false);
            }
        }),
        WAND((handler, tag) -> {
            PlayerEntity player = handler.player;
            IMagicRecipe<?> recipe = handler.magicHolder.getRecipe(new ResourceLocation(tag.getString("recipe")));
            if (recipe == null)
                return;
            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof MagicWand)) {
                stack = player.getOffhandItem();
            }
            if (!(stack.getItem() instanceof MagicWand))
                return;
            MagicWand wand = (MagicWand) stack.getItem();
            wand.setMagic(recipe, stack);
        });

        private final BiConsumer<MagicHandler, CompoundNBT> cons;

        Action(BiConsumer<MagicHandler, CompoundNBT> cons) {
            this.cons = cons;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendHexUpdate(MagicProduct<?, ?> prod) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("product", prod.recipe.id.toString());
        tag.put("data", prod.tag.tag);
        ToServerMsg msg = new ToServerMsg(Action.HEX, tag);
        MagicHandler.get(Proxy.getClientPlayer()).magicHolder.checkUnlocks();
        PacketHandler.send(msg);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendDebugInfo(CompoundNBT s, CompoundNBT c) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("server", s);
        tag.put("client", c);
        PacketHandler.send(new ToServerMsg(Action.DEBUG, tag));
    }

    @OnlyIn(Dist.CLIENT)
    public static void levelUpAbility(AbilityPoints.LevelType type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("type", type.ordinal());
        PacketHandler.send(new ToServerMsg(Action.LEVEL, tag));
    }

    @OnlyIn(Dist.CLIENT)
    public static void setProfession(Profession prof) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", prof.getID());
        PacketHandler.send(new ToServerMsg(Action.PROFESSION, tag));
    }

    @OnlyIn(Dist.CLIENT)
    public static void addElemMastery(MagicElement elem) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", elem.getID());
        PacketHandler.send(new ToServerMsg(Action.ELEMENTAL, tag));
    }

    @OnlyIn(Dist.CLIENT)
    public static void unlockArcaneType(ArcaneType type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", type.getID());
        PacketHandler.send(new ToServerMsg(Action.ARCANE, tag));
    }

    @OnlyIn(Dist.CLIENT)
    public static void activateWand(IMagicRecipe<?> recipe) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("recipe", recipe.id.toString());
        Action.WAND.cons.accept(MagicHandler.get(Proxy.getPlayer()), tag);
        PacketHandler.send(new ToServerMsg(Action.WAND, tag));
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
        if (player == null || !player.isAlive())
            return;
        MagicHandler handler = MagicHandler.get(player);
        msg.action.cons.accept(handler, msg.tag);
        ctx.get().setPacketHandled(true);
    }

}
