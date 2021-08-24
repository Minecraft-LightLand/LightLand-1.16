package com.hikarishima.lightland.magic.compat.recipes;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.IMagicCraftRecipe;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftRecipeCategory implements IRecipeCategory<IMagicCraftRecipe<?>> {

    private static final ResourceLocation BG = new ResourceLocation(LightLandMagic.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public MagicCraftRecipeCategory() {
        this.id = new ResourceLocation(LightLandMagic.MODID, "magic_craft");
    }

    public MagicCraftRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 18, 176, 18);//FIXME
        icon = guiHelper.createDrawableIngredient(new ItemStack(MagicItemRegistry.I_RITUAL_CORE));
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return IMagicCraftRecipe.class;
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
    public void setIngredients(IMagicCraftRecipe<?> r, IIngredients list) {
        //FIXME
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IMagicCraftRecipe<?> r, IIngredients list) {
        //FIXME
    }

}
