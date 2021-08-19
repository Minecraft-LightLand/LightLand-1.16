package com.hikarishima.lightland.terrain.feature;

import com.hikarishima.lightland.terrain.config.VolcanoBiomeReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class LavaSmokeFeatureConfig implements IFeatureConfig {

    public static final Codec<LavaSmokeFeatureConfig> CODEC = RecordCodecBuilder.create(
            (e) -> e.group(
                    Codec.FLOAT.fieldOf("size").forGetter((e1) -> e1.size),
                    Codec.FLOAT.fieldOf("height").forGetter((e1) -> e1.height),
                    Codec.FLOAT.fieldOf("slope").forGetter((e1) -> e1.slope),
                    Codec.FLOAT.fieldOf("delta_size").forGetter((e1) -> e1.delta_size),
                    Codec.FLOAT.fieldOf("delta_height").forGetter((e1) -> e1.delta_height),
                    Codec.FLOAT.fieldOf("delta_height").forGetter((e1) -> e1.delta_slope)
            ).apply(e, LavaSmokeFeatureConfig::new));

    public final float size, height, slope, delta_size, delta_height, delta_slope;

    public LavaSmokeFeatureConfig(float size, float height, float slope, float delta_size, float delta_height, float delta_slope) {
        this.size = size;
        this.height = height;
        this.slope = slope;
        this.delta_size = delta_size;
        this.delta_height = delta_height;
        this.delta_slope = delta_slope;
    }

    public LavaSmokeFeatureConfig(VolcanoBiomeReader.VolcanoConfig.LavaWell c) {
        this(c.size, c.height, c.slope, c.delta_size, c.delta_height, c.delta_slope);
    }

}
