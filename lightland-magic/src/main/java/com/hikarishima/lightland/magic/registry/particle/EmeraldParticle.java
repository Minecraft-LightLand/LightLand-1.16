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
public class EmeraldParticle extends SpriteTexturedParticle {

    public static final int LIFE = 20;

    protected EmeraldParticle(ClientWorld world, double x0, double y0, double z0, double x1, double y1, double z1) {
        super(world, x0, y0, z0, 0, 0, 0);
        this.xd = (x1 - x0) / LIFE;
        this.yd = (y1 - y0) / LIFE;
        this.zd = (z1 - z0) / LIFE;
        this.setSize(0.03F, 0.03F);
        this.gravity = 0;
        this.lifetime = LIFE;
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        public Particle createParticle(BasicParticleType type, ClientWorld world, double x0, double y0, double z0, double x1, double y1, double z1) {
            EmeraldParticle part = new EmeraldParticle(world, x0, y0, z0, x1, y1, z1);
            part.pickSprite(this.sprite);
            return part;
        }
    }
}
