package com.hikarishima.lightland.magic.compat.jei.recipes;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.AnvilCraftRecipe;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AnvilCraftRecipeCategory implements IRecipeCategory<AnvilCraftRecipe> {

    private static final ResourceLocation BG = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public AnvilCraftRecipeCategory() {
        this.id = new ResourceLocation(LightLandMagic.MODID, "anvil_craft");
    }

    public AnvilCraftRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(BG, 0, 168, 125, 18)
                .addPadding(0, 20, 0, 0)
                .build();
        icon = guiHelper.createDrawableIngredient(new ItemStack(MagicItemRegistry.I_ANVIL));
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return AnvilCraftRecipe.class;
    }

    @Override
    public String getTitle() {
        return StringSubstitution.toString(Translator.getContainer("anvil_craft"));
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
    public void setIngredients(AnvilCraftRecipe sl, IIngredients list) {
        list.setInputIngredients(Lists.newArrayList(Ingredient.of(sl.input), Ingredient.of(sl.consume)));
        list.setOutput(VanillaTypes.ITEM, new ItemStack(sl.output, sl.max));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, AnvilCraftRecipe sl, IIngredients list) {
        set(layout.getItemStacks(), Collections.singletonList(sl.input.getDefaultInstance()), 0, true, 0, 0);
        set(layout.getItemStacks(), Collections.singletonList(sl.consume), 1, true, 49, 0);
        set(layout.getItemStacks(), Collections.singletonList(new ItemStack(sl.output, sl.max)), 2, false, 107, 0);
    }

    @Override
    public void draw(AnvilCraftRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, matrixStack, mouseX, mouseY);
        String costText = "" + recipe.level;
        String text = I18n.get("container.repair.cost", costText);

        Minecraft minecraft = Minecraft.getInstance();
        int mainColor = 0xFF80FF20;
        ClientPlayerEntity player = minecraft.player;
        if (player != null &&
                (recipe.level >= 40 || recipe.level > player.experienceLevel) &&
                !player.isCreative()) {
            // Show red if the player doesn't have enough levels
            mainColor = 0xFFFF6060;
        }

        drawRepairCost(minecraft, matrixStack, text, mainColor);
    }

    private static <T> void set(IGuiIngredientGroup<T> group, List<T> t, int ind, boolean bool, int x, int y) {
        group.init(ind, bool, x, y);
        group.set(ind, t);
    }

    private void drawRepairCost(Minecraft minecraft, MatrixStack matrixStack, String text, int mainColor) {
        int shadowColor = 0xFF000000 | (mainColor & 0xFCFCFC) >> 2;
        int width = minecraft.font.width(text);
        int x = background.getWidth() - 2 - width;
        int y = 27;
        minecraft.font.draw(matrixStack, text, x + 1, y, shadowColor);
        minecraft.font.draw(matrixStack, text, x, y + 1, shadowColor);
        minecraft.font.draw(matrixStack, text, x + 1, y + 1, shadowColor);
        minecraft.font.draw(matrixStack, text, x, y, mainColor);
    }

}
