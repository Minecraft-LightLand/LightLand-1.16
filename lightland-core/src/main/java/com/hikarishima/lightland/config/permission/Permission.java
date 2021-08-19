package com.hikarishima.lightland.config.permission;

import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class Permission {

    @SerialClass.SerialField
    public boolean canExplosionBreakBlock;
    @SerialClass.SerialField
    public boolean canEndermanPickBlock;
    @SerialClass.SerialField
    public boolean canMobBreakBlock;
    @SerialClass.SerialField
    public boolean canOtherPlayerBreakBlock;
    @SerialClass.SerialField
    public boolean canOtherPlayerUseBlock;

}
