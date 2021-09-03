package com.hikarishima.lightland.magic.compat.mixin;

import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.world.DifficultyInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {

    @Inject(at = @At("HEAD"), method = "populateDefaultEquipmentSlots", cancellable = true)
    protected void populateDefaultEquipmentSlots(DifficultyInstance ins, CallbackInfo info) {
        AbstractSkeletonEntity self = (AbstractSkeletonEntity) (Object) this;
        if (!self.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()){
            info.cancel();
        }
    }

}
