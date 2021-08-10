package com.hikarishima.lightland.magic.gui.container;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.chem.ReactionPool;
import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class ChemPacket extends PacketHandler.ContSerialMsg {

    @SerialClass.SerialField
    public ReactionPool.Result result;

    @Deprecated
    public ChemPacket(){}

    public ChemPacket(int wid, ReactionPool.Result result) {
        super(wid);
        this.result = result;
    }
}
