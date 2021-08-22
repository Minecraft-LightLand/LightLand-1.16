package com.hikarishima.lightland.quest.dialog;

import com.hikarishima.lightland.quest.QuestRegistry;
import com.hikarishima.lightland.quest.option.Option;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class Dialog {

    public static Dialog getDialog(World world, String id) {
        if (id == null || id.length() == 0)
            return null;
        Dialog ans = ConfigRecipe.getObject(world, QuestRegistry.DIALOG, id);
        if (ans != null) {
            ans.self_id = id;
        }
        return ans;
    }

    @SerialClass.SerialField
    public String text;

    @SerialClass.SerialField
    public Option[] next;

    public String self_id;

    public IFormattableTextComponent getText() {
        return new StringTextComponent(text);
    }

    public List<ITextProperties> getOptionText() {
        List<ITextProperties> ans = new ArrayList<>();
        for (Option op : next) {
            ans.add(new StringTextComponent(op.name));
        }
        return ans;
    }

}
