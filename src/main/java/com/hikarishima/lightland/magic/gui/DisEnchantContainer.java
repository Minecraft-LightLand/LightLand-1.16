package com.hikarishima.lightland.magic.gui;

import com.hikarishima.lightland.item.MagicBook;
import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DisEnchantContainer extends Container implements PacketHandler.SerialMsgCont<DisEnchantContainer.Msg> {

    public static final int SLOTS = 1;

    public enum MsgType {
        CLEAR, SET_PRODUCT
    }

    @SerialClass
    public static class Msg extends PacketHandler.BaseSerialMsg {

        @SerialClass.SerialField
        public MsgType type;

        @SerialClass.SerialField
        public String[] str;

        public Msg(int wid, MsgType type, String... str) {
            super(wid);
            this.type = type;
            this.str = str;
        }

    }

    private final ItemStack book;
    private final PlayerEntity player;

    protected IMagicRecipe<?> product = null;
    protected final MagicHandler handler;

    public void setProduct(IMagicRecipe<?> prod) {
        product = prod;
        if (prod == null)
            PacketHandler.send(new Msg(containerId, MsgType.CLEAR));
        else PacketHandler.send(new Msg(containerId, MsgType.SET_PRODUCT,
                prod.id.toString()));
    }

    public DisEnchantContainer(int id, PlayerInventory plinv) {
        super(ContainerRegistry.CT_MAGIC_BOOK, id);
        player = plinv.player;
        ItemStack main = plinv.player.getMainHandItem();
        ItemStack off = plinv.player.getOffhandItem();
        book = main.getItem() instanceof MagicBook ? main : off;
        handler = MagicHandler.get(player);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.isAlive();
    }

    public void handle(Msg msg) {
        if (msg.type == MsgType.CLEAR) {
            product = null;
        } else if (msg.type == MsgType.SET_PRODUCT) {
            ResourceLocation rl = new ResourceLocation(msg.str[0]);
            product = handler.getRecipe(rl);
        }
    }

    public void save() {
        if (book != null && book.getItem() instanceof MagicBook) {

        }
    }

}
