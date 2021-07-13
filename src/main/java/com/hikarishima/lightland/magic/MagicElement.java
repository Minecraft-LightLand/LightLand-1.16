package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.LightLand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MagicElement extends ForgeRegistryEntry<MagicElement> {

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIcon() {
        ResourceLocation rl = getRegistryName();
        return new ResourceLocation(rl.getNamespace(), "textures/" + LightLand.MODID + "/magic/elements/" + rl.getPath());

    }

    public String getName() {
        String domain = getRegistryName().getNamespace();
        String name = getRegistryName().getPath();
        return domain + ":" + LightLand.MODID + ".magic_element." + name;
    }

}
