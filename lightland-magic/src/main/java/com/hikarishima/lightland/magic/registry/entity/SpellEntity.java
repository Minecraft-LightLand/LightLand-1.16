package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.registry.MagicEntityRegistry;
import com.hikarishima.lightland.magic.spell.internal.ActivationConfig;
import com.hikarishima.lightland.magic.spell.internal.SpellConfig;
import com.hikarishima.lightland.magic.spell.render.SpellComponent;
import com.lcy0x1.base.BaseEntity;
import com.lcy0x1.core.math.AutoAim;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

@SerialClass
public class SpellEntity extends BaseEntity {

    @SerialClass.SerialField
    public int time, setup, close;

    private Consumer<SpellEntity> action;

    public SpellEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public SpellEntity(World w) {
        this(MagicEntityRegistry.ET_SPELL.get(), w);
    }

    public void setData(double x, double y, double z, int time, int setup, int close, float xr, float yr) {
        this.setPos(x, y - 1.5f, z);
        this.time = time;
        this.setup = setup;
        this.close = close;
        this.xRot = xr;
        this.yRot = yr;
    }

    public void setData(PlayerEntity player, SpellConfig.SpellDisplay spell, SpellPlane plane) {
        Vector3d pos = player.getPosition(1f);
        float xr = player.xRot;
        float yr = player.yRot;
        if (plane == SpellPlane.VERTICAL) {
            pos = pos.add(0, 1e-3, 0);
            xr = -90;
        } else if (plane == SpellPlane.HORIZONTAL) {
            pos = pos.add(0, player.getEyeHeight(), 0);
            xr = 0;
            pos = AutoAim.getRayTerm(pos, xr, yr, -0.5);
        } else if (plane == SpellPlane.NORMAL) {
            pos = pos.add(0, player.getEyeHeight(), 0);
            pos = AutoAim.getRayTerm(pos, xr, yr, 0.5);
        }
        setData(pos.x, pos.y, pos.z, spell.duration, spell.setup, spell.close, xr, yr);
    }

    public void setData(ActivationConfig act, SpellConfig.SpellDisplay spell) {
        Vector3d pos = act.target == null ? act.pos : act.target.position().add(0, act.target.getBbHeight() / 2, 0);
        pos = pos.add(0, 1e-3, 0);
        float xr = -90;
        setData(pos.x, pos.y, pos.z, spell.duration, spell.setup, spell.close, xr, 0);
    }

    public void setAction(Consumer<SpellEntity> cons) {
        action = cons;
    }

    @Override
    public void tick() {
        super.tick();
        if (action != null) {
            action.accept(this);
        }
        if (!level.isClientSide() && this.tickCount >= time) {
            remove();
        }
    }

    public float getSize(float partial) {
        float t = tickCount + partial;
        if (t < setup) {
            return t / setup;
        }
        if (time - t < close) {
            return (time - t) / close;
        }
        return 1;
    }

    public SpellComponent getComponent() {
        return SpellComponent.getFromConfig("test_spell");
    }

    @Override
    protected void defineSynchedData() {

    }

    public enum SpellPlane {
        /**
         * flat plane on the ground
         */
        VERTICAL,
        /**
         * arua after head
         */
        HORIZONTAL,
        /**
         * circle in front
         */
        NORMAL;
    }

}
