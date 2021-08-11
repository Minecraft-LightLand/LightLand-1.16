package com.hikarishima.lightland.compat.recipes;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.compat.LightLandJeiPlugin;
import com.hikarishima.lightland.compat.ingredients.Countable;
import com.hikarishima.lightland.compat.ingredients.ElementIngredient;
import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.ItemRegistry;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DisEnchanterRecipeCategory implements IRecipeCategory<IMagicRecipe<?>> {

    private static final ResourceLocation BG = new ResourceLocation(LightLand.MODID, "textures/gui/container/disenchant.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public DisEnchanterRecipeCategory() {
        this.id = new ResourceLocation(LightLand.MODID, "disenchant");
    }

    public DisEnchanterRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 0, 174, 18);
        icon = guiHelper.createDrawableIngredient(new ItemStack(ItemRegistry.CHEM_BOOK));
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return IMagicRecipe.class;
    }

    @Override
    public String getTitle() {
        return StringSubstitution.toString(Translator.getContainer("disenchant"));
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(IMagicRecipe<?> r, IIngredients list) {
        Enchantment ench = (Enchantment) r.getProduct().item;
        ItemStack stack = EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, 1));
        list.setInputIngredients(Lists.newArrayList(Ingredient.of(stack)));
        list.setInput(VanillaTypes.ITEM, Items.GOLD_NUGGET.getDefaultInstance());
        list.setOutput(VanillaTypes.ITEM, ItemRegistry.ENCHANT_GOLD_NUGGET.getDefaultInstance());
        list.setOutputs(LightLandJeiPlugin.INSTANCE.ELEM_TYPE, Countable.collect(Arrays.stream(r.getElements()).map(ElementIngredient::new)));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, IMagicRecipe<?> iMagicRecipe, IIngredients iIngredients) {
        //TODO
    }

}
