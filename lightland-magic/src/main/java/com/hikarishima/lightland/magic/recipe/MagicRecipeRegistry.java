package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.block.RitualCore;
import com.lcy0x1.base.BaseRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MagicRecipeRegistry {

    public static final DeferredRegister<IRecipeSerializer<?>> REC = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LightLandMagic.MODID);

    public static final IRecipeType<IMagicRecipe<?>> RT_MAGIC = IRecipeType.register("lightland-magic:magic");
    public static final IRecipeType<MagicCraftRecipe> RT_CRAFT = IRecipeType.register("lightland-magic:craft");
    public static final IRecipeType<AnvilCraftRecipe> RT_ANVIL = IRecipeType.register("lightland-magic:anvil");

    public static final RegistryObject<BaseRecipe.RecType<DefMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv>> RSM_DEF =
            REC.register("magic_default", () -> new BaseRecipe.RecType<>(DefMagicRecipe.class, RT_MAGIC));

    public static final RegistryObject<BaseRecipe.RecType<ShortMagicRecipe, IMagicRecipe<?>, IMagicRecipe.Inv>> RSM_SHORT =
            REC.register("magic_short", () -> new BaseRecipe.RecType<>(ShortMagicRecipe.class, RT_MAGIC));

    public static final RegistryObject<BaseRecipe.RecType<MagicCraftRecipe, MagicCraftRecipe, RitualCore.Inv>> RSM_CRAFT =
            REC.register("craft_shapeless", () -> new BaseRecipe.RecType<>(MagicCraftRecipe.class, RT_CRAFT));

    public static final RegistryObject<BaseRecipe.RecType<AnvilCraftRecipe, AnvilCraftRecipe, AnvilCraftRecipe.Inv>> RSM_ANVIL =
            REC.register("anvil", () -> new BaseRecipe.RecType<>(AnvilCraftRecipe.class, RT_ANVIL));

    public static final ResourceLocation SPELL = new ResourceLocation(LightLandMagic.MODID, "config_spell");
    public static final ResourceLocation PRODUCT_TYPE_DISPLAY = new ResourceLocation(LightLandMagic.MODID, "config_product_type");
    public static final ResourceLocation CHEM = new ResourceLocation(LightLandMagic.MODID, "config_chemistry");
    public static final ResourceLocation SPELL_ENTITY = new ResourceLocation(LightLandMagic.MODID, "config_spell_entity");
    public static final ResourceLocation WEIGHT = new ResourceLocation(LightLandMagic.MODID, "config_weight");
    public static final ResourceLocation SPAWN = new ResourceLocation(LightLandMagic.MODID, "config_mobspawn");

}
