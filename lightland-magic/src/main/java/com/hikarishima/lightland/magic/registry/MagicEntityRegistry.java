package com.hikarishima.lightland.magic.registry;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.registry.entity.WindBladeEntity;
import com.hikarishima.lightland.magic.registry.entity.WindBladeEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
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
        manager.register(ET_WIND_BLADE, new WindBladeEntityRenderer(manager));
    }

}
