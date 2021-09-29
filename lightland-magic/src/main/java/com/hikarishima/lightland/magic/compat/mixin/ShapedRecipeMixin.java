package com.hikarishima.lightland.magic.compat.mixin;

import com.google.gson.JsonObject;
import com.hikarishima.lightland.magic.recipe.ritual.AbstractMagicCraftRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {

    @Inject(method = "itemFromJson", at = @At("HEAD"), cancellable = true)
    private static void injectItemFromJson(JsonObject obj, CallbackInfoReturnable<ItemStack> info) {
        if (obj.has("enchant_book")) {
            JsonObject book = obj.getAsJsonObject("enchant_book");
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(book.get("id").getAsString()));
            int lvl = book.get("lvl").getAsInt();
            assert ench != null;
            info.setReturnValue(EnchantedBookItem.createForEnchantment(new EnchantmentData(ench, lvl)));
            info.cancel();
        }
        if (obj.has("massive_stack")) {
            JsonObject jo = obj.getAsJsonObject("massive_stack");
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jo.get("item").getAsString()));
            int count = jo.get("count").getAsInt();
            ItemStack ans = Items.SHULKER_BOX.getDefaultInstance();
            NonNullList<ItemStack> nonnulllist = AbstractMagicCraftRecipe.fill(item, count);
            ItemStackHelper.saveAllItems(ans.getOrCreateTagElement("BlockEntityTag"), nonnulllist);
            info.setReturnValue(ans);
            info.cancel();
        }
    }

}
