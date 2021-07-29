package com.hikarishima.lightland.npc.event;

import com.hikarishima.lightland.npc.dialog.DefaultDialog;
import com.hikarishima.lightland.npc.dialog.DialogHolder;
import com.hikarishima.lightland.npc.dialog.DialogSelector;
import com.hikarishima.lightland.npc.gui.DialogScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class GUIDispatcher {

    public static boolean onClick(PlayerEntity player, Entity target) {
        DialogSelector sel;
        //TODO event scene
        sel = NPCDialog.INSTANCE.get(player, target);
        if (sel != null) {
            if (player.level.isClientSide()) {
                Minecraft.getInstance().setScreen(new DialogScreen(new DialogHolder(sel, player.level)));
            }
            return true;
        }
        return false;
    }

    public static class NPCDialog implements Handler {

        public static NPCDialog INSTANCE = new NPCDialog();

        @Override
        public DialogSelector get(PlayerEntity player, Entity target) {
            if (!(target instanceof PlayerEntity))
                return null;
            PlayerEntity npc = (PlayerEntity) target;
            String name = npc.getStringUUID();
            DefaultDialog def = DefaultDialog.get(player.level);
            return def.getSelector(player.level, name);
        }
    }

    public interface Handler {

        DialogSelector get(PlayerEntity player, Entity target);

    }

}
