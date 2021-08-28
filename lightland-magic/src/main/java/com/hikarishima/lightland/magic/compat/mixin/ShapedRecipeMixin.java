package com.hikarishima.lightland.magic.compat.mixin;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {

    @Inject(method = "itemFromJson", at = @At("HEAD"), cancellable = true)
    private void injectItemFromJson(JsonObject obj, CallbackInfoReturnable<ItemStack> info) {
        if (obj.has("enchant_book")) {
            JsonObject book = obj.getAsJsonObject("enchant_book");
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(book.get("id").getAsString()));
            int lvl = book.get("lvl").getAsInt();
            assert ench != null;
            info.setReturnValue(EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, lvl)));
            info.cancel();
        }
    }

}
