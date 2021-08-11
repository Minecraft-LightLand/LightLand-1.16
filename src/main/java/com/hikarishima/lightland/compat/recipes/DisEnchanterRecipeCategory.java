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
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DisEnchanterRecipeCategory implements IRecipeCategory<IMagicRecipe<?>> {

    private static final ResourceLocation BG = new ResourceLocation(LightLand.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public DisEnchanterRecipeCategory() {
        this.id = new ResourceLocation(LightLand.MODID, "disenchant");
    }

    public DisEnchanterRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 18, 176, 18);
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
        List<ItemStack> l0 = new ArrayList<>();
        List<ItemStack> l1 = new ArrayList<>();
        List<ItemStack> l2 = new ArrayList<>();
        List<ElementIngredient> elem = Countable.collect(Arrays.stream(r.getElements()).map(ElementIngredient::new));
        for (int i = 1; i <= ench.getMaxLevel(); i++) {
            l0.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, i)));
            l1.add(new ItemStack(Items.GOLD_NUGGET, i));
            l2.add(new ItemStack(ItemRegistry.ENCHANT_GOLD_NUGGET, i));
        }
        List<List<ElementIngredient>> l3 = elem.stream().map(e -> {
            List<ElementIngredient> ans = new ArrayList<>();
            if (e.count == 0)
                e.count = 1;
            for (int i = 1; i <= ench.getMaxLevel(); i++)
                ans.add(new ElementIngredient(e.elem, e.count * i));
            return ans;
        }).collect(Collectors.toList());
        list.setInputLists(VanillaTypes.ITEM, Lists.newArrayList(l0, l1));
        list.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(l2));
        list.setOutputLists(LightLandJeiPlugin.INSTANCE.ELEM_TYPE, l3);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IMagicRecipe<?> r, IIngredients list) {
        set(layout.getItemStacks(), list.getInputs(VanillaTypes.ITEM).get(0), 0, true, 0, 0);
        set(layout.getItemStacks(), list.getInputs(VanillaTypes.ITEM).get(1), 1, true, 18, 0);
        set(layout.getItemStacks(), list.getOutputs(VanillaTypes.ITEM).get(0), 2, false, 68, 0);
        int ind = 3;
        IIngredientType<ElementIngredient> type = LightLandJeiPlugin.INSTANCE.ELEM_TYPE;
        for (List<ElementIngredient> e : list.getOutputs(type)) {
            set(layout.getIngredientsGroup(type), e, ind, false, 69 + ((ind - 2) * 18), 1);
            ind++;
        }
    }

    private static <T> void set(IGuiIngredientGroup<T> group, List<T> t, int ind, boolean bool, int x, int y) {
        group.init(ind, bool, x, y);
        group.set(ind, t);
    }

}
