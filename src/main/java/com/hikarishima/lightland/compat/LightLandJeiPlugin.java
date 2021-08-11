package com.hikarishima.lightland.compat;

import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.compat.ingredients.*;
import com.hikarishima.lightland.compat.recipes.ChemRecipeCategory;
import com.hikarishima.lightland.compat.recipes.DisEnchanterRecipeCategory;
import com.hikarishima.lightland.compat.screen.ExtraInfoScreen;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.chem.HashEquationPool;
import com.hikarishima.lightland.magic.gui.container.ArcaneInjectScreen;
import com.hikarishima.lightland.magic.gui.container.ChemScreen;
import com.hikarishima.lightland.magic.gui.container.DisEnchanterScreen;
import com.hikarishima.lightland.magic.gui.container.SpellCraftScreen;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.hikarishima.lightland.registry.ItemRegistry;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
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

    public final ResourceLocation UID = new ResourceLocation(LightLand.MODID, "jei_plugin");

    public final ChemIngredientHelper CHEM_HELPER = new ChemIngredientHelper();
    public final ChemIngredientRenderer CHEM_RENDERER = new ChemIngredientRenderer();
    public final IIngredientType<ChemIngredient> CHEM_TYPE = () -> ChemIngredient.class;

    public final ElemIngredientHelper ELEM_HELPER = new ElemIngredientHelper();
    public final ElemIngredientRenderer ELEM_RENDERER = new ElemIngredientRenderer();
    public final IIngredientType<ElementIngredient> ELEM_TYPE = () -> ElementIngredient.class;

    public final DisEnchanterRecipeCategory DISENCHANT = new DisEnchanterRecipeCategory();
    public final ChemRecipeCategory CHEM_CATEGORY = new ChemRecipeCategory();

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
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(IMagicRecipe.getMap(Proxy.getWorld(), MagicRegistry.MPT_ENCH).values(), DISENCHANT.getUid());
        registration.addRecipes(Arrays.asList(getPool().equations), CHEM_CATEGORY.getUid());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ItemRegistry.DISENCHANT_BOOK.getDefaultInstance(), DISENCHANT.getUid());
        registration.addRecipeCatalyst(ItemRegistry.CHEM_BOOK.getDefaultInstance(), CHEM_CATEGORY.getUid());
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
        return Objects.requireNonNull(ConfigRecipe.getObject(Proxy.getWorld(), ConfigRecipe.CHEM, "pool"));
    }

}
