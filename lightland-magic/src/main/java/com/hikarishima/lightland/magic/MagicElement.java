package com.hikarishima.lightland.magic;

import com.hikarishima.lightland.LightLand;
import com.lcy0x1.base.NamedEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicElement extends NamedEntry<MagicElement> {

    public MagicElement() {
        super(() -> MagicRegistry.ELEMENT);
    }

    public String getName() {
        String domain = getRegistryName().getNamespace();
        String name = getRegistryName().getPath();
        return domain + ":" + LightLand.MODID + ".magic_element." + name;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIcon() {
        ResourceLocation rl = getRegistryName();
        return new ResourceLocation(rl.getNamespace(), "textures/magic_elements/" + rl.getPath() + ".png");
    }

}
