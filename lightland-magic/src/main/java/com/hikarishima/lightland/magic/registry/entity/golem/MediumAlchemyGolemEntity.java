package com.hikarishima.lightland.magic.registry.entity.golem;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.world.World;

@SerialClass
public class MediumAlchemyGolemEntity extends AlchemyGolemEntity {

    public MediumAlchemyGolemEntity(EntityType<? extends MediumAlchemyGolemEntity> type, World world) {
        super(type, world);
    }
    
}
