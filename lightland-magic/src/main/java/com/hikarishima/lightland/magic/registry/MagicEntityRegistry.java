package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MagicEntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITIES, LightLandMagic.MODID);

    public static final RegistryObject<EntityType<WindBladeEntity>> ET_WIND_BLADE = reg("wind_blade",
            () -> EntityType.Builder.<WindBladeEntity>of(WindBladeEntity::new, EntityClassification.MISC)
                    .fireImmune().sized(0.5f, 0.5f)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<SpellEntity>> ET_SPELL = reg("spell",
            () -> EntityType.Builder.<SpellEntity>of(SpellEntity::new, EntityClassification.MISC)
                    .setShouldReceiveVelocityUpdates(false)
                    .fireImmune().sized(3f, 3f)
                    .updateInterval(20));

    public static final RegistryObject<EntityType<FireArrowEntity>> ET_FIRE_ARROW = reg("fire_arrow",
            () -> EntityType.Builder.<FireArrowEntity>of(FireArrowEntity::new, EntityClassification.MISC)
                    .sized(1f, 1f).clientTrackingRange(4).updateInterval(20));

    public static final RegistryObject<EntityType<MagicFireBallEntity>> ET_FIRE_BALL = reg("fire_ball",
            () -> EntityType.Builder.<MagicFireBallEntity>of(MagicFireBallEntity::new, EntityClassification.MISC)
                    .sized(1f, 1f).clientTrackingRange(4).updateInterval(10));


    private static <T extends Entity> RegistryObject<EntityType<T>> reg(String name, Supplier<EntityType.Builder<T>> v) {
        return ENTITY.register(name, () -> v.get().build(name));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerClient() {
        EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
        ItemRenderer item = Minecraft.getInstance().getItemRenderer();
        manager.register(ET_WIND_BLADE.get(), new WindBladeEntityRenderer(manager));
        manager.register(ET_SPELL.get(), new SpellEntityRenderer(manager));
        manager.register(ET_FIRE_ARROW.get(), new TippedArrowRenderer(manager));
        manager.register(ET_FIRE_BALL.get(), new SpecialSpriteRenderer<>(manager, item, true));
    }

}
