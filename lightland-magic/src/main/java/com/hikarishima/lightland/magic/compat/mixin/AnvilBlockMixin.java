package com.hikarishima.lightland.magic.compat.mixin;

import com.hikarishima.lightland.magic.registry.MagicItemRegistry;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void injectDamage(BlockState state, CallbackInfoReturnable<BlockState> info) {
        if (state.getBlock() == MagicItemRegistry.B_ANVIL.get()) {
            info.setReturnValue(state);
            info.cancel();
        }
    }
}