package com.hikarishima.lightland.magic.gui;

import com.hikarishima.lightland.item.MagicBook;
import com.hikarishima.lightland.magic.MagicProduct;
import com.hikarishima.lightland.magic.MagicProductType;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.registry.ContainerRegistry;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class MagicBookContainer extends Container implements PacketHandler.SerialMsgCont<MagicBookContainer.Msg> {

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
    private final NBTObj manager;
    private final PlayerEntity player;
    private MagicProduct<?, ?> product = null;
    private NBTObj product_nbt = null;

    public void setProduct(MagicProduct<?, ?> prod) {
        product = prod;
        if (prod == null)
            PacketHandler.send(new Msg(containerId, MsgType.CLEAR));
        else PacketHandler.send(new Msg(containerId, MsgType.SET_PRODUCT,
                prod.type.getRegistryName().toString(), prod.rl.toString()));
    }

    public MagicBookContainer(int id, PlayerInventory plinv) {
        super(ContainerRegistry.CT_MAGIC_BOOK, id);
        player = plinv.player;
        ItemStack main = plinv.player.getMainHandItem();
        ItemStack off = plinv.player.getOffhandItem();
        book = main.getItem() instanceof MagicBook ? main : off;
        manager = new NBTObj(book, "magic_book_content");
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public void handle(Msg msg) {
        if (msg.type == MsgType.CLEAR) {
            product = null;
        } else if (msg.type == MsgType.SET_PRODUCT) {
            ResourceLocation rl0 = new ResourceLocation(msg.str[0]);
            ResourceLocation rl1 = new ResourceLocation(msg.str[1]);
            MagicProductType<?, ?> type = Objects.requireNonNull(MagicRegistry.PRODUCT_TYPE.getValue(rl0)).getAsType();
            product_nbt = manager.getSub(msg.str[0]).getSub(msg.str[1]);
            product = type.fac.get(player, product_nbt, rl1);
        }
    }

    public void save() {
        if (book != null && book.getItem() instanceof MagicBook) {

        }
    }

}
