package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.entity.golem.AlchemyGolemEntity;
import com.hikarishima.lightland.magic.registry.entity.golem.LargeAlchemyGolemEntity;
import com.hikarishima.lightland.magic.registry.entity.golem.MediumAlchemyGolemEntity;
import com.hikarishima.lightland.magic.registry.entity.golem.SmallAlchemyGolemEntity;
import com.hikarishima.lightland.magic.registry.entity.golem.render.LargeAlchemyGolemRenderer;
import com.hikarishima.lightland.magic.registry.entity.misc.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings("unused")
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

    public static final RegistryObject<EntityType<SmallAlchemyGolemEntity>> ALCHEMY_SMALL = reg("alchemy_golem_small",
            () -> EntityType.Builder.of(SmallAlchemyGolemEntity::new,
                    EntityClassification.MISC)
                    .sized(0.3F, 0.975F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<MediumAlchemyGolemEntity>> ALCHEMY_MEDIUM = reg("alchemy_golem_medium",
            () -> EntityType.Builder.of(MediumAlchemyGolemEntity::new,
                    EntityClassification.MISC)
                    .sized(0.6F, 1.95F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<LargeAlchemyGolemEntity>> ALCHEMY_LARGE = reg("alchemy_golem_large",
            () -> EntityType.Builder.of(LargeAlchemyGolemEntity::new,
                    EntityClassification.MISC)
                    .sized(1.4F, 2.7F).clientTrackingRange(10));


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

        manager.register(ALCHEMY_LARGE.get(), new LargeAlchemyGolemRenderer(manager));

    }

    @SubscribeEvent
    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ALCHEMY_SMALL.get(), AlchemyGolemEntity.createAttributes(10, 0.35, 0, 2, 0).build());
        event.put(ALCHEMY_MEDIUM.get(), AlchemyGolemEntity.createAttributes(20, 0.3, 0.25, 3, 4).build());
        event.put(ALCHEMY_LARGE.get(), AlchemyGolemEntity.createAttributes(40, 0.25, 0.5, 4, 8).build());
    }

}
