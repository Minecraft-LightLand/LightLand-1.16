package com.lcy0x1.core.util;

import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arthur
 * @Date 2020-9-24
 */
public class NBTList<T> {

    public final List<T> list = new ArrayList<>();

    private final ListNBT tag;

    NBTList(NBTObj parent, String key) {
        boolean old = parent.tag.contains(key);
        tag = parent.tag.getList(key, 10);
        if (!old)
            parent.tag.put(key, tag);
    }

    public NBTObj add() {
        NBTObj ans = new NBTObj();
        tag.add(ans.tag);
        return ans;
    }

    public NBTObj get(int i) {
        return new NBTObj(tag.getCompound(i));
    }

    public void remove(int i) {
        if (list.size() > i)
            list.remove(i);
        tag.remove(i);
        tag.set(0, null);
    }

    public void remove(T t) {
        int ind = list.indexOf(t);
        if (ind >= 0)
            remove(ind);
    }

    public int size() {
        return tag.size();
    }

}
