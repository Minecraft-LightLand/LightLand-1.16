package com.hikarishima.lightland.npc.option;

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
    public Request request = null;

    @SerialClass.SerialField
    public Reward reward = null;

    @SerialClass.SerialField
    public Lock lock = null;

    @SerialClass.SerialField
    public IOptionComponent[] other = null;

    public List<IOptionComponent> getComponents() {
        List<IOptionComponent> ans = new ArrayList<>();
        if (request != null) ans.add(request);
        if (reward != null) ans.add(reward);
        if (lock != null) ans.add(lock);
        if (other != null)
            ans.addAll(Arrays.asList(other));
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
        PacketHandler.send(new OptionMessage(this));
        for (IOptionComponent comp : getComponents()) {
            comp.perform(player);
        }
    }

}
