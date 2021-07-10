package com.hikarishima.lightland.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class LavaLakeSmokePlacementConfig implements IPlacementConfig {

    public static Codec<LavaLakeSmokePlacementConfig> CODEC = RecordCodecBuilder.create(
            (e)->e.group(
                    Codec.FLOAT.fieldOf("chance").forGetter((e1)->e1.chance)
            ).apply(e, LavaLakeSmokePlacementConfig::new));

    public final float chance;

    public LavaLakeSmokePlacementConfig(float chance){
        this.chance = chance;
    }

}
