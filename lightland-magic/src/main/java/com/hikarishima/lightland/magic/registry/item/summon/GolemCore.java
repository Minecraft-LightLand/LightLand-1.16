package com.hikarishima.lightland.magic.registry.item.summon;

import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
import com.hikarishima.lightland.magic.registry.entity.golem.GolemMaterial;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.NBTObj;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
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

    public static void buildText(EntityType<? extends AlchemyGolemEntity> type, List<ITextComponent> list, List<GolemMaterial> material) {
        GolemMaterial base = new GolemMaterial();
        AttributeModifierMap map = GlobalEntityTypeAttributes.getSupplier(type);
        base.hp += map.getBaseValue(Attributes.MAX_HEALTH);
        base.speed += map.getBaseValue(Attributes.MOVEMENT_SPEED);
        base.kb += map.getBaseValue(Attributes.KNOCKBACK_RESISTANCE);
        base.atk += map.getBaseValue(Attributes.ATTACK_DAMAGE);
        base.def += map.getBaseValue(Attributes.ARMOR);
        base.tough += map.getBaseValue(Attributes.ARMOR_TOUGHNESS);
        material.add(base);
        GolemMaterial merge = new GolemMaterial(material);
        GolemFrame.buildText(list, merge);
    }

    public GolemCore(Properties props) {
        super(props.stacksTo(1));
    }

    public ArrayList<String> getMaterials(ItemStack stack) {
        ArrayList<String> ans = new ArrayList<>();
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("materials")) {
            for (INBT i : tag.getList("materials", NBTObj.TYPE_STRING)) {
                ans.add(i.getAsString());
            }
        }
        return ans;
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
            buildText(getType(), list, mats);
        }
    }

    protected EntityType<? extends AlchemyGolemEntity> getType() {
        return this == MagicItemRegistry.SMALL_GOLEM_TOTEM.get() ? MagicEntityRegistry.ALCHEMY_SMALL.get() :
                this == MagicItemRegistry.MEDIUM_GOLEM_TOTEM.get() ? MagicEntityRegistry.ALCHEMY_MEDIUM.get() :
                        MagicEntityRegistry.ALCHEMY_LARGE.get();
    }
}
