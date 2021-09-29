package com.hikarishima.lightland.magic.compat.jei.recipes;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicProxy;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.recipe.ritual.AbstractMagicCraftRecipe;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.recipe.ritual.PotionBoostRecipe;
import com.hikarishima.lightland.magic.recipe.ritual.PotionSpellRecipe;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.magic.registry.item.magic.MagicWand;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicCraftRecipeCategory implements IRecipeCategory<AbstractMagicCraftRecipe<?>> {

    private static final ResourceLocation BG = new ResourceLocation(LightLandMagic.MODID, "textures/jei/background.png");

    private final ResourceLocation id;
    private IDrawable background, icon;

    public MagicCraftRecipeCategory() {
        this.id = new ResourceLocation(LightLandMagic.MODID, "magic_craft");
    }

    public MagicCraftRecipeCategory init(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(BG, 0, 36, 145, 54);
        icon = guiHelper.createDrawableIngredient(MagicItemRegistry.I_RITUAL_CORE.get().getDefaultInstance());
        return this;
    }

    @Override
    public ResourceLocation getUid() {
        return id;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getRecipeClass() {
        return AbstractMagicCraftRecipe.class;
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
    public void setIngredients(AbstractMagicCraftRecipe<?> sl, IIngredients list) {
        List<Ingredient> input = new ArrayList<>();
        input.add(Ingredient.of(sl.core.input));
        for (AbstractMagicCraftRecipe.Entry ent : sl.side) {
            if (!ent.input.isEmpty()) {
                input.add(Ingredient.of(ent.input));
            }
        }
        input.add(Ingredient.of(MagicItemRegistry.GILDED_WAND.get().getDefaultInstance()));
        list.setInputIngredients(input);
        List<ItemStack> output = new ArrayList<>();
        output.add(sl.core.output);
        for (AbstractMagicCraftRecipe.Entry ent : sl.side) {
            if (!ent.output.isEmpty()) {
                output.add(ent.output);
            }
        }
        list.setOutputs(VanillaTypes.ITEM, output);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, AbstractMagicCraftRecipe<?> sl, IIngredients list) {
        List<AbstractMagicCraftRecipe.Entry> entry = new ArrayList<>(sl.side);
        while (entry.size() < 8) {
            entry.add(new AbstractMagicCraftRecipe.Entry());
        }
        entry.add(4, sl.core);

        int in = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack item = specialProcess(sl, entry.get(i * 3 + j).input, i * 3 + j == 4);
                if (!item.isEmpty())
                    set(layout.getItemStacks(),
                            Collections.singletonList(item),
                            in++, true, j * 18, i * 18);
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack item = specialProcess(sl, entry.get(i * 3 + j).output, i * 3 + j == 4);
                if (!item.isEmpty())
                    set(layout.getItemStacks(),
                            Collections.singletonList(item),
                            in++, false, 90 + j * 18, i * 18);
            }
        }
        IMagicRecipe<?> magic = sl.getMagic() == null ? null : MagicProxy.getHandler().magicHolder.getRecipe(sl.getMagic());
        MagicWand wand = MagicItemRegistry.GILDED_WAND.get();
        ItemStack wand_stack = wand.getDefaultInstance();
        if (magic != null) {
            wand.setMagic(magic, wand_stack);
        }
        set(layout.getItemStacks(), Collections.singletonList(wand_stack), in, true, 63, 0);
    }

    private static ItemStack specialProcess(AbstractMagicCraftRecipe<?> sl, ItemStack stack, boolean isCore) {
        if (sl instanceof PotionBoostRecipe) {
            if (isCore) {
                stack = stack.copy();
                List<EffectInstance> list = PotionUtils.getCustomEffects(stack);
                Effect eff = ForgeRegistries.POTIONS.getValue(((PotionBoostRecipe) sl).effect);
                list = list.stream().map(e -> {
                    if (e.getEffect() != eff) {
                        return new EffectInstance(eff, e.getDuration(), e.getAmplifier());
                    }
                    return e;
                }).collect(Collectors.toList());
                PotionUtils.setCustomEffects(stack, list);
            }
        }
        if (sl instanceof PotionSpellRecipe) {
            if (!isCore) {
                stack = stack.copy();
                CompoundNBT compoundnbt = stack.getOrCreateTag();
                ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);
                compoundnbt.put("CustomPotionEffects", listnbt);
                return stack;
            }
        }
        return stack;
    }

    private static <T> void set(IGuiIngredientGroup<T> group, List<T> t, int ind, boolean bool, int x, int y) {
        group.init(ind, bool, x, y);
        group.set(ind, t);
    }

}
