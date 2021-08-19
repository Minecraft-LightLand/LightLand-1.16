package com.hikarishima.lightland.quest.dialog;

import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.world.World;

import java.util.HashMap;

@SerialClass
public class DefaultDialog {

    public static DefaultDialog get(World world) {
        return ConfigRecipe.getObject(world, ConfigRecipe.DIALOG, "default_dialog");
    }

    @SerialClass.SerialField(generic = {String.class, String.class})
    public HashMap<String, String> map = new HashMap<>();

    public DialogSelector getSelector(World world, String name) {
        return DialogSelector.getDialogSelector(world, map.get(name));
    }


}
