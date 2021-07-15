package com.hikarishima.lightland.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class LavaSmokePlacementConfig implements IPlacementConfig {

    public static Codec<LavaSmokePlacementConfig> CODEC = RecordCodecBuilder.create(
            (e) -> e.group(
                    Codec.FLOAT.fieldOf("chance").forGetter((e1) -> e1.chance)
            ).apply(e, LavaSmokePlacementConfig::new));

    public final float chance;

    public LavaSmokePlacementConfig(float chance) {
        this.chance = chance;
    }

}
