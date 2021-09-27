package com.hikarishima.lightland.magic.registry.item.summon;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.entity.golem.GolemMaterial;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class GolemCore extends Item {

    public static void buildText(List<ITextComponent> list, List<GolemMaterial> material) {
        GolemMaterial merge = new GolemMaterial(material);
        GolemFrame.buildText(list, merge);
    }

    public GolemCore(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && world != null && world.isClientSide()) {
            ListNBT ltag = tag.getList("materials", NBTObj.TYPE_STRING);
            List<GolemMaterial> mats = new ArrayList<>();
            for (INBT i : ltag) {
                String mat = i.getAsString();
                GolemMaterial material = ConfigRecipe.getObject(world, MagicRecipeRegistry.GOLEM, mat);
                if (material != null) {
                    mats.add(material);
                    list.add(Translator.get("golem.material." + mat));
                }
            }
            if (list.size() > 0) {
                buildText(list, mats);
            }
        }
    }
}
