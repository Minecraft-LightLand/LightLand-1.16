package com.hikarishima.lightland.magic.registry.item.summon;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.entity.golem.GolemMaterial;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GolemFrame extends Item {

    public static void buildText(List<ITextComponent> list, GolemMaterial material) {
        if (material.hp > 0) list.add(Translator.get("golem.attr.hp", material.hp));
        if (material.speed < 0) list.add(Translator.get("golem.attr.speed", material.speed));
        if (material.kb > 0) list.add(Translator.get("golem.attr.kb", material.kb * 100));
        if (material.atk > 0) list.add(Translator.get("golem.attr.atk", material.atk));
        if (material.def > 0) list.add(Translator.get("golem.attr.def", material.def));
        if (material.tough > 0) list.add(Translator.get("golem.attr.tough", material.tough));
        if (material.restore > 0) list.add(Translator.get("golem.attr.restore", material.restore * 20));
        if (material.thorn > 0) list.add(Translator.get("golem.attr.thorn", material.thorn * 100));
        if (material.bypass_armor > 0) list.add(Translator.get("golem.attr.bypass_armor", material.bypass_armor * 100));
        if (material.bypass_magic > 0) list.add(Translator.get("golem.attr.bypass_magic", material.bypass_magic * 100));
        if (material.fire_reduce > 0) list.add(Translator.get("golem.attr.fire_reduce", material.fire_reduce * 100));
        if (material.fire_tick > 0) list.add(Translator.get("golem.attr.fire_tick", material.fire_tick / 20d));
        if (material.fire_thorn_tick > 0) list.add(Translator.get("golem.attr.fire_thorn_tick", material.fire_thorn_tick / 20d));
    }

    public GolemFrame(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && world != null && world.isClientSide()) {
            String mat = tag.getString("item");
            GolemMaterial material = ConfigRecipe.getObject(world, MagicRecipeRegistry.GOLEM, mat);
            if (material != null) {
                list.add(Translator.get("golem.material." + mat));
                buildText(list, material);
            }
        }
    }
}
