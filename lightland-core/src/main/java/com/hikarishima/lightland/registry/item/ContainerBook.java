package com.hikarishima.lightland.registry.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContainerBook extends Item implements INamedContainerProvider {

    private final Supplier<ContainerType<?>> cont;
    private final IFac fac;

    public ContainerBook(Properties props, Supplier<ContainerType<?>> cont, IFac fac) {
        super(props);
        this.cont = cont;
        this.fac = fac;
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            player.openMenu(this);
        } else {
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
        }
        return ActionResult.success(stack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(cont.get().getRegistryName().getNamespace() + ":container." + cont.get().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int wid, PlayerInventory plInv, PlayerEntity pl) {
        return fac.create(wid, plInv, pl);
    }

    public interface IFac {

        Container create(int wid, PlayerInventory plInv, PlayerEntity pl);

    }

}
