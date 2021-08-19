package com.hikarishima.lightland.npc.gui;

import com.hikarishima.lightland.npc.dialog.DefaultDialog;
import com.hikarishima.lightland.npc.dialog.DialogHolder;
import com.hikarishima.lightland.npc.dialog.DialogSelector;
import com.hikarishima.lightland.npc.player.QuestHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class GUIDispatcher {

    public static boolean onClick(PlayerEntity player, Entity target) {
        //TODO npc only
        //if (!(target instanceof PlayerEntity)) return false;
        //PlayerEntity npc = (PlayerEntity) target;
        String name = target.getDisplayName().getContents();
        DialogHolder holder;
        QuestHandler handler = QuestHandler.get(player);
        holder = handler.getDialog(name);
        if (holder == null) {
            DefaultDialog def = DefaultDialog.get(player.level);
            DialogSelector sel = def.getSelector(player.level, name);
            if (sel != null)
                holder = new DialogHolder(sel, player.level);
        }
        if (holder != null) {
            if (player.level.isClientSide()) {
                //TODO move it to server
                Minecraft.getInstance().setScreen(new DialogScreen(holder));
            }
            return true;
        }
        return false;
    }

}
