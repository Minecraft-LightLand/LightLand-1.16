package com.hikarishima.lightland.npc.dialog;

import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.world.World;

import java.util.Random;

@SerialClass
public class DialogSelector {

    public static DialogSelector getDialogSelector(World world, String id) {
        if (id == null || id.length() == 0)
            return null;
        return ConfigRecipe.getObject(world, ConfigRecipe.DIALOG, id);
    }

    @SerialClass.SerialField
    public DialogEntry[] dialog_list;

    @SerialClass.SerialField
    public NextSelector[] next_selector;

    public Dialog getDialogSelector(World w, Random r) {
        int total = 0;
        for (DialogEntry ent : dialog_list) {
            total += ent.weight;
        }
        int sel = r.nextInt(total);
        for (DialogEntry ent : dialog_list) {
            sel -= ent.weight;
            if (sel < 0)
                return Dialog.getDialog(w, ent.id);
        }
        return null;
    }

    public DialogSelector getNext(World w, Random r) {
        int total = 0;
        for (NextSelector ent : next_selector) {
            total += ent.weight;
        }
        int sel = r.nextInt(total);
        for (DialogEntry ent : dialog_list) {
            sel -= ent.weight;
            if (sel < 0)
                return getDialogSelector(w, ent.id);
        }
        return null;
    }

    @SerialClass
    public static class DialogEntry {

        @SerialClass.SerialField
        public int weight = 1;

        @SerialClass.SerialField
        public String id;

    }

    @SerialClass
    public static class NextSelector {

        @SerialClass.SerialField
        public int weight = 1;

        @SerialClass.SerialField
        public String id;

    }

}
