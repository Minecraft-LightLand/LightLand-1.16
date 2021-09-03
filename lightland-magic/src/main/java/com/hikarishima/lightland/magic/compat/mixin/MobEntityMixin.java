package com.hikarishima.lightland.magic.compat.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "enchantSpawnedWeapon", cancellable = true, at = @At("HEAD"))
    protected void enchantSpawnedWeapon(float d, CallbackInfo info) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getItemBySlot(EquipmentSlotType.MAINHAND).isEnchanted())
            info.cancel();
    }

    @Inject(method = "enchantSpawnedArmor", cancellable = true, at = @At("HEAD"))
    protected void enchantSpawnedArmor(float d, EquipmentSlotType slot, CallbackInfo info) {
        MobEntity self = (MobEntity) (Object) this;
        if (self.getItemBySlot(slot).isEnchanted())
            info.cancel();
    }


}
