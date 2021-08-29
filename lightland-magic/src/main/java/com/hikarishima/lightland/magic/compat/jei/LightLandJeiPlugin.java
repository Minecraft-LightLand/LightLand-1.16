package com.hikarishima.lightland.magic.compat.jei;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.magic.compat.jei.ingredients.*;
import com.hikarishima.lightland.magic.compat.jei.recipes.AnvilCraftRecipeCategory;
import com.hikarishima.lightland.magic.compat.jei.recipes.ChemRecipeCategory;
import com.hikarishima.lightland.magic.compat.jei.recipes.DisEnchanterRecipeCategory;
import com.hikarishima.lightland.magic.compat.jei.recipes.MagicCraftRecipeCategory;
import com.hikarishima.lightland.magic.compat.jei.screen.ExtraInfoScreen;
import com.hikarishima.lightland.magic.gui.container.ArcaneInjectScreen;
import com.hikarishima.lightland.magic.gui.container.ChemScreen;
import com.hikarishima.lightland.magic.gui.container.DisEnchanterScreen;
import com.hikarishima.lightland.magic.gui.container.SpellCraftScreen;
import com.hikarishima.lightland.magic.recipe.IMagicRecipe;
import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LightLandJeiPlugin implements IModPlugin {

    public static LightLandJeiPlugin INSTANCE;

    public final ResourceLocation UID = new ResourceLocation(LightLandMagic.MODID, "jei_plugin");

    public final ChemIngredientHelper CHEM_HELPER = new ChemIngredientHelper();
    public final ChemIngredientRenderer CHEM_RENDERER = new ChemIngredientRenderer();
    public final IIngredientType<ChemIngredient> CHEM_TYPE = () -> ChemIngredient.class;

    public final ElemIngredientHelper ELEM_HELPER = new ElemIngredientHelper();
    public final ElemIngredientRenderer ELEM_RENDERER = new ElemIngredientRenderer();
    public final IIngredientType<ElementIngredient> ELEM_TYPE = () -> ElementIngredient.class;

    public final DisEnchanterRecipeCategory DISENCHANT = new DisEnchanterRecipeCategory();
    public final ChemRecipeCategory CHEM_CATEGORY = new ChemRecipeCategory();
    public final MagicCraftRecipeCategory MAGIC_CRAFT = new MagicCraftRecipeCategory();
    public final AnvilCraftRecipeCategory ANVIL_CRAFT = new AnvilCraftRecipeCategory();

    public final ExtraInfoScreen EXTRA_INFO = new ExtraInfoScreen();

    public LightLandJeiPlugin() {
        INSTANCE = this;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(ELEM_TYPE, MagicRegistry.ELEMENT.getValues().stream()
                        .map(ElementIngredient::new).collect(Collectors.toList()),
                ELEM_HELPER, ELEM_RENDERER);
        registration.register(CHEM_TYPE, getPool().objects.values().stream()
                        .map(ChemIngredient::asChem).filter(Objects::nonNull).collect(Collectors.toList()),
                CHEM_HELPER, CHEM_RENDERER);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(DISENCHANT.init(helper));
        registration.addRecipeCategories(CHEM_CATEGORY.init(helper));
        registration.addRecipeCategories(MAGIC_CRAFT.init(helper));
        registration.addRecipeCategories(ANVIL_CRAFT.init(helper));
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(IMagicRecipe.getMap(Proxy.getWorld(), MagicRegistry.MPT_ENCH).values(), DISENCHANT.getUid());
        registration.addRecipes(Arrays.asList(getPool().equations), CHEM_CATEGORY.getUid());
        registration.addRecipes(Proxy.getWorld().getRecipeManager().getAllRecipesFor(MagicRecipeRegistry.RT_CRAFT), MAGIC_CRAFT.getUid());
        registration.addRecipes(Proxy.getWorld().getRecipeManager().getAllRecipesFor(MagicRecipeRegistry.RT_ANVIL), ANVIL_CRAFT.getUid());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(MagicItemRegistry.DISENCHANT_BOOK.getDefaultInstance(), DISENCHANT.getUid());
        registration.addRecipeCatalyst(MagicItemRegistry.CHEM_BOOK.getDefaultInstance(), CHEM_CATEGORY.getUid());
        registration.addRecipeCatalyst(MagicItemRegistry.I_RITUAL_CORE.getDefaultInstance(), MAGIC_CRAFT.getUid());
        for (Block b : BlockTags.ANVIL.getValues()) {
            registration.addRecipeCatalyst(b.asItem().getDefaultInstance(), ANVIL_CRAFT.getUid());
        }
        registration.addRecipeCatalyst(MagicItemRegistry.I_ANVIL.getDefaultInstance(), VanillaRecipeCategoryUid.ANVIL);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ExtraInfoScreen.init();
        registration.addGuiContainerHandler(DisEnchanterScreen.class, EXTRA_INFO);
        registration.addGuiContainerHandler(SpellCraftScreen.class, EXTRA_INFO);
        registration.addGuiContainerHandler(ArcaneInjectScreen.class, EXTRA_INFO);
        registration.addGuiContainerHandler(ChemScreen.class, EXTRA_INFO);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }

    public static HashEquationPool getPool() {
        return Objects.requireNonNull(ConfigRecipe.getObject(Proxy.getWorld(), MagicRecipeRegistry.CHEM, "pool"));
    }

}
