package com.hikarishima.lightland.npc.dialog;

import net.minecraft.world.World;

public class DialogHolder {

    public final World world;
    public DialogSelector sel;
    public Dialog dialog;

    public DialogHolder(DialogSelector sel, World world) {
        this.sel = sel;
        this.world = world;
        dialog = sel.getDialogSelector(world, world.random);
    }

    public boolean next(int ind) {
        dialog = Dialog.getDialog(world, dialog.next[ind].next);
        if (dialog == null) {
            sel = sel.getNext(world, world.random);
            if (sel != null)
                dialog = sel.getDialogSelector(world, world.random);
        }
        return dialog != null;
    }

}
