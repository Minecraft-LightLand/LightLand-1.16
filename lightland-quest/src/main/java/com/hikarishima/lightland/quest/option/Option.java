package com.hikarishima.lightland.quest.option;

import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SerialClass
public class Option {

    @SerialClass.SerialField
    public String name;

    @SerialClass.SerialField
    public String next;

    @SerialClass.SerialField
    public IOptionComponent[] components = null;

    public List<IOptionComponent> getComponents() {
        List<IOptionComponent> ans = new ArrayList<>();
        if (components != null)
            ans.addAll(Arrays.asList(components));
        return ans;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean test(PlayerEntity player) {
        for (IOptionComponent comp : getComponents()) {
            if (!comp.test(player))
                return false;
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void perform(PlayerEntity player) {
        PacketHandler.send(new OptionToServer(this));
    }

}
