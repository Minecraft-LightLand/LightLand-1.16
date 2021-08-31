package com.hikarishima.lightland.magic.compat.jei.recipes;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.MagicCraftRecipe;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftRecipeCategory implements IRecipeCategory<MagicCraftRecipe> {

    private static final ResourceLocation BG = new ResourceLocation(LightLandMagic.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public MagicCraftRecipeCategory() {
        this.id = new ResourceLocation(LightLandMagic.MODID, "magic_craft");
    }

    public MagicCraftRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 36, 145, 54);
        icon = guiHelper.createDrawableIngredient(MagicItemRegistry.I_RITUAL_CORE.get());
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return MagicCraftRecipe.class;
    }

    @Override
    public String getTitle() {
        return StringSubstitution.toString(Translator.getContainer("magic_craft"));
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
    public void setIngredients(MagicCraftRecipe sl, IIngredients list) {
        List<Ingredient> input = new ArrayList<>();
        input.add(Ingredient.of(sl.core.input));
        for (MagicCraftRecipe.Entry ent : sl.side) {
            if (!ent.input.isEmpty()) {
                input.add(Ingredient.of(ent.input));
            }
        }
        list.setInputIngredients(input);
        List<ItemStack> output = new ArrayList<>();
        output.add(sl.core.output);
        for (MagicCraftRecipe.Entry ent : sl.side) {
            if (!ent.output.isEmpty()) {
                output.add(ent.output);
            }
        }
        list.setOutputs(VanillaTypes.ITEM, output);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MagicCraftRecipe sl, IIngredients list) {
        List<MagicCraftRecipe.Entry> entry = new ArrayList<>(sl.side);
        while (entry.size() < 8) {
            entry.add(new MagicCraftRecipe.Entry());
        }
        entry.add(4, sl.core);

        int in = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack item = entry.get(i * 3 + j).input;
                if (!item.isEmpty())
                    set(layout.getItemStacks(),
                            Collections.singletonList(item),
                            in++, true, j * 18, i * 18);
            }
        }
        int out = in;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack item = entry.get(i * 3 + j).output;
                if (!item.isEmpty())
                    set(layout.getItemStacks(),
                            Collections.singletonList(item),
                            out++, false, 90 + j * 18, i * 18);
            }
        }
    }

    private static <T> void set(IGuiIngredientGroup<T> group, List<T> t, int ind, boolean bool, int x, int y) {
        group.init(ind, bool, x, y);
        group.set(ind, t);
    }

}
