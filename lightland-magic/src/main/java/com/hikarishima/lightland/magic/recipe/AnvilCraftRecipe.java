package com.hikarishima.lightland.magic.recipe;

import com.lcy0x1.base.BaseRecipe;
import com.lcy0x1.core.util.SerialClass;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AnvilCraftRecipe extends BaseRecipe<AnvilCraftRecipe, AnvilCraftRecipe, AnvilCraftRecipe.Inv> {

    @SerialClass.SerialField
    public Item input, output;
    @SerialClass.SerialField
    public ItemStack consume;
    @SerialClass.SerialField
    public int max, level;

    public AnvilCraftRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_ANVIL.get());
    }

    @Override
    public boolean matches(Inv inv, World world) {
        return false;
    }

    public boolean matches(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.getItem() == input && right.getItem() == consume.getItem() && right.getCount() > consume.getCount()) {
            int max_damage = input.getMaxDamage(left);
            if (max_damage > 0) {
                int damage = max_damage - input.getDamage(left);
                int count = max * damage / max_damage;
                return count > 0;
            }
        }
        return false;
    }

    public void setEvent(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.getItem() == input && right.getItem() == consume.getItem() && right.getCount() > consume.getCount()) {
            int max_damage = input.getMaxDamage(left);
            if (max_damage > 0) {
                int damage = max_damage - input.getDamage(left);
                int count = max * damage / max_damage;
                if (count > 0) {
                    event.setOutput(new ItemStack(output, count));
                    event.setMaterialCost(consume.getCount());
                    event.setCost(level);
                }
            }
        }
    }

    @Override
    public ItemStack assemble(Inv inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int r, int c) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.getDefaultInstance();
    }

    public interface Inv extends BaseRecipe.RecInv<AnvilCraftRecipe> {

        AnvilUpdateEvent getEvent();

    }

}
