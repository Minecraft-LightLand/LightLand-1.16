package com.hikarishima.lightland.magic.registry.entity.golem;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.world.World;

@SerialClass
public class SmallAlchemyGolemEntity extends AlchemyGolemEntity {

    public SmallAlchemyGolemEntity(EntityType<? extends SmallAlchemyGolemEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean isBaby() {
        return true;
    }

}
