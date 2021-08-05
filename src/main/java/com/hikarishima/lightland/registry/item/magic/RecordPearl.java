package com.hikarishima.lightland.registry.item.magic;

import com.hikarishima.lightland.magic.capabilities.MagicHandler;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecordPearl extends Item {

    public RecordPearl(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        MagicHandler handler = MagicHandler.get(player);
        if (isFoil(stack)) {
            if (handler.abilityPoints.getProfession() != null)
                return ActionResult.fail(stack);
            CompoundNBT tag = stack.getTagElement("magic_cap");
            handler.reset(MagicHandler.Reset.FOR_INJECT);
            ExceptionHandler.run(() -> Automator.fromTag(tag, MagicHandler.class, handler, f -> true));
            handler.reInit();
            stack.removeTagKey("magic_cap");
            return ActionResult.success(stack);
        } else {
            if (handler.abilityPoints.getProfession() == null)
                return ActionResult.fail(stack);
            CompoundNBT tag = stack.getOrCreateTagElement("magic_cap");
            Automator.toTag(tag, handler);
            handler.reset(MagicHandler.Reset.ALL);
            return ActionResult.success(stack);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement("magic_cap") != null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (isFoil(stack)) {
            MagicHandler handler = Automator.fromTag(stack.getTagElement("magic_cap"), MagicHandler.class);
            list.add(handler.abilityPoints.profession.getDesc());
        }
        super.appendHoverText(stack, world, list, flag);
    }
}
