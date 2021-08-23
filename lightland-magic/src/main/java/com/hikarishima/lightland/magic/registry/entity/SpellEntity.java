package com.hikarishima.lightland.magic.registry.entity;

import com.hikarishima.lightland.magic.spell.render.SpellComponent;
import com.lcy0x1.base.BaseEntity;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@SerialClass
public class SpellEntity extends BaseEntity {

    public SpellEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public void setData(){

    }

    public SpellComponent getComponent(){
        return SpellComponent.getFromConfig("test_spell");
    }

    @Override
    protected void defineSynchedData() {

    }

}
