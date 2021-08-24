package com.lcy0x1.base.block;

import com.lcy0x1.base.BlockProp;
import com.lcy0x1.base.block.impl.AllDireBlock;
import com.lcy0x1.base.block.impl.HorizontalBlock;
import com.lcy0x1.base.block.impl.Power;
import com.lcy0x1.base.block.impl.TriggerBlock;
import com.lcy0x1.base.block.mult.*;
import com.lcy0x1.base.block.one.*;
import com.lcy0x1.base.block.type.IImpl;
import com.lcy0x1.base.block.type.IMultImpl;
import com.lcy0x1.base.block.type.IOneImpl;
import com.lcy0x1.base.block.type.STE;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BaseBlock extends Block {

    public static BaseBlock newBaseBlock(BlockProp p, IImpl... impl) {
        return new ImplBaseBlock(p, impl);
    }

    protected BaseBlock(Properties props){
        super(props);
    }

}