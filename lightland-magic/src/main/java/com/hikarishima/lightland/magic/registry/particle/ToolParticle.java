package com.hikarishima.lightland.magic.registry.particle;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ToolParticle extends SpriteTexturedParticle {

    protected ToolParticle(ClientWorld world, double x0, double y0, double z0, double x1, double y1, double z1) {
        super(world, x0, y0, z0, x1, y1, z1);
        xd = x1;
        yd = y1;
        zd = z1;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
        xd *= 0.95;
        yd *= 0.95;
        zd *= 0.95;
    }

    public float getQuadSize(float partial) {
        float f = ((float) this.age + partial) / (float) this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(BasicParticleType type, ClientWorld world, double x0, double y0, double z0, double x1, double y1, double z1) {
            ToolParticle part = new ToolParticle(world, x0, y0, z0, x1, y1, z1);
            part.pickSprite(this.sprite);
            return part;
        }
    }
}
