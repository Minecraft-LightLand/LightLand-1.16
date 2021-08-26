package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MagicEntityRegistry {

    public static final EntityType<WindBladeEntity> ET_WIND_BLADE = reg("wind_blade",
            EntityType.Builder.of(WindBladeEntity::new, EntityClassification.MISC)
                    .fireImmune().sized(0.5f, 0.5f)
                    .updateInterval(20));

    public static final EntityType<SpellEntity> ET_SPELL = reg("spell",
            EntityType.Builder.of(SpellEntity::new, EntityClassification.MISC)
                    .setShouldReceiveVelocityUpdates(false)
                    .fireImmune().sized(3f, 3f)
                    .updateInterval(20));

    public static final EntityType<FireArrowEntity> ET_FIRE_ARROW = reg("fire_arrow",
            EntityType.Builder.<FireArrowEntity>of(FireArrowEntity::new, EntityClassification.MISC)
                    .sized(1f, 1f).clientTrackingRange(4).updateInterval(20));

    public static final EntityType<MagicFireBallEntity> ET_FIRE_BALL = reg("fire_ball",
            EntityType.Builder.<MagicFireBallEntity>of(MagicFireBallEntity::new, EntityClassification.MISC)
                    .sized(1f, 1f).clientTrackingRange(4).updateInterval(10));

    private static <T extends Entity> EntityType<T> reg(String name, EntityType.Builder<T> v) {
        return reg(name, v.build(name));
    }

    private static <V extends T, T extends ForgeRegistryEntry<T>> V reg(String name, V v) {
        v.setRegistryName(LightLandMagic.MODID, name);
        return v;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerClient() {
        EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
        ItemRenderer item = Minecraft.getInstance().getItemRenderer();
        manager.register(ET_WIND_BLADE, new WindBladeEntityRenderer(manager));
        manager.register(ET_SPELL, new SpellEntityRenderer(manager));
        manager.register(ET_FIRE_ARROW, new TippedArrowRenderer(manager));
        manager.register(ET_FIRE_BALL,new SpriteRenderer<>(manager, item, 3.0F, true));
    }

}
