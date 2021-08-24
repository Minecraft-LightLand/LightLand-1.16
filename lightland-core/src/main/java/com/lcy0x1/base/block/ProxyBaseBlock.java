package com.lcy0x1.base.block;

import com.google.common.collect.Lists;
import com.lcy0x1.base.BlockProp;
import com.lcy0x1.base.block.type.IImpl;
import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.block.impl.*;
import com.lcy0x1.base.block.mult.*;
import com.lcy0x1.base.block.one.*;
import com.lcy0x1.core.util.ExceptionHandler;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.sf.cglib.proxy.Enhancer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.lcy0x1.base.block.BlockProxy.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ProxyBaseBlock extends BaseBlock implements ProxyContainer<ProxyMethod> {

    private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

    private static final Enhancer ENHANCER = new Enhancer();
    private static final Class<?>[] CONSTRUCTOR = {BlockProp.class, IImpl[].class};

    static {
        ENHANCER.setSuperclass(BaseBlock.class);
        ENHANCER.setCallback(new ProxyInterceptor());
    }

    public static BaseBlock newBaseBlock(BlockProp p, IImpl... impl) {
        return (BaseBlock) ENHANCER.create(CONSTRUCTOR, new Object[]{p, impl});
    }

    private BlockImplementor impl;

    private ProxyBaseBlock(BlockProp p, IImpl... impl) {
        super(handler(construct(p).addImpls(impl)));
    }

    public static BlockImplementor construct(BlockProp bb) {
        return new BlockImplementor(bb.getProps());
    }

    private static Properties handler(BlockImplementor bi) {
        if (TEMP.get() != null)
            throw new RuntimeException("concurrency error");
        TEMP.set(bi);
        return bi.props;
    }

    @Override
    public final boolean isSignalSource(BlockState bs) {
        return impl.one(IPower.class).isPresent();
    }

    @Override
    @ForFirstProxy(value = ITE.class, name = "createTileEntity")
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }

    @Override
    public final int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        return impl.one(ITE.class).map(e -> Optional.ofNullable(worldIn.getBlockEntity(pos))
                .map(Container::getRedstoneSignalFromBlockEntity).orElse(0)).orElse(0);
    }

    @Override
    public final int getLightValue(BlockState bs, IBlockReader w, BlockPos pos) {
        return impl.one(ILight.class).map(e -> e.getLightValue(bs, w, pos))
                .orElse(super.getLightValue(bs, w, pos));
    }

    @Override
    @ForFirstProxy(value = IFace.class, name = "getStateForPlacement")
    public final BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState();
    }

    @Override
    public final int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
        return impl.one(IPower.class)
                .map(e -> e.getSignal(bs, r, pos, d))
                .orElse(0);
    }

    @Override
    public final boolean hasTileEntity(BlockState state) {
        return impl.one(ITE.class).isPresent();
    }

    @Override
    public final BlockState mirror(BlockState state, Mirror mirrorIn) {
        return impl.one(IRotMir.class).map(e -> e.mirror(state, mirrorIn)).orElse(state);
    }

    @Override
    public final ActionResultType use(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
        return impl.execute(IClick.class)
                .map(e -> e.onClick(bs, w, pos, pl, h, r))
                .filter(e -> e != ActionResultType.PASS)
                .findFirst().orElse(ActionResultType.PASS);
    }

    @Override
    public final void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (impl.one(ITE.class).isPresent() && state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity != null) {
                if (tileentity instanceof IInventory) {
                    InventoryHelper.dropContents(worldIn, pos, (IInventory) tileentity);
                    worldIn.updateNeighbourForOutputSignal(pos, this);
                }
                worldIn.removeBlockEntity(pos);
            }
        }
        impl.execute(IRep.class).forEach(e -> e.onReplaced(state, worldIn, pos, newState, isMoving));
    }

    @Override
    public final BlockState rotate(BlockState state, Rotation rot) {
        return impl.one(IRotMir.class).map(e -> e.rotate(state, rot)).orElse(state);
    }

    @Override
    @ForEachProxy(value = IState.class, name = "createBlockStateDefinition")
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    }

    @Override
    public final void neighborChanged(BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
        impl.execute(INeighborUpdate.class).forEach(e -> e.neighborChanged(this, state, world, pos, nei_block, nei_pos, moving));
        super.neighborChanged(state, world, pos, nei_block, nei_pos, moving);
    }

    @Override
    public final void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        impl.execute(IRandomTick.class).forEach(e -> e.randomTick(state, world, pos, random));
    }

    @Override
    public final void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        impl.execute(IScheduleTick.class).forEach(e -> e.tick(state, world, pos, random));
    }

    @NotNull
    @Override
    public Proxy<ProxyMethod> getProxy() {
        final BlockImplementor blockImplementor = TEMP.get();
        if (blockImplementor != null) {
            impl = blockImplementor;
            TEMP.remove();
        }
        return this.impl.proxy;
    }

    public static class BlockImplementor implements Proxy<ProxyMethod> {

        private final Properties props;
        private final MutableProxy<ProxyMethod> proxy = new ListProxy<>();

        public BlockImplementor(Properties p) {
            props = p;
        }

        public BlockImplementor addImpls(IImpl... impls) {
            for (IImpl impl : impls)
                if (impl instanceof STE)
                    proxy.addProxy(new TEPvd((STE) impl));
                else if (impl != null)
                    proxy.addProxy(impl);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends IImpl> Stream<T> execute(Class<T> cls) {
            //FIXME
            List<ProxyMethod> list = Lists.newArrayList();
            ExceptionHandler.run(() -> proxy.forEachProxy(list::add));
            return list.stream().filter(cls::isInstance).map(e -> (T) e);
        }

        public <T extends IImpl> Optional<T> one(Class<T> cls) {
            return execute(cls).findFirst();
        }

        @NotNull
        @Override
        public Iterator<ProxyMethod> iterator() {
            // TODO
            return null;
        }

    }

    private static class AllDireBlock implements IFace, IState {

        private AllDireBlock() {
        }

        @Override
        public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
            builder.add(FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
            return def.setValue(FACING, context.getClickedFace().getOpposite());
        }
    }

    private static class HorizontalBlock implements IRotMir, IState, IFace {

        private HorizontalBlock() {
        }

        @Override
        public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
            builder.add(HORIZONTAL_FACING);
        }

        @Override
        public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
            return def.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
        }

        @Override
        public BlockState mirror(BlockState state, Mirror mirrorIn) {
            return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
        }

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
        }
    }

    private static class Power implements IState, IPower {

        private Power() {
        }

        @Override
        public void createBlockStateDefinition(Builder<Block, BlockState> builder) {
            builder.add(BlockStateProperties.POWER);
        }

        @Override
        public int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
            return bs.getValue(BlockStateProperties.POWER);
        }

    }

    private static class TEPvd implements ITE, IClick {

        private final Supplier<? extends TileEntity> f;

        private TEPvd(Supplier<? extends TileEntity> sup) {
            f = sup;
        }

        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world) {
            return f.get();
        }

        @Override
        public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
            TileEntity te = w.getBlockEntity(pos);
            if (w.isClientSide())
                return te instanceof INamedContainerProvider ? ActionResultType.SUCCESS : ActionResultType.PASS;
            if (te instanceof INamedContainerProvider) {
                pl.openMenu((INamedContainerProvider) te);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }

    }

    public static class TriggerBlock implements INeighborUpdate, IState {

        private final int delay;

        public TriggerBlock(int delay) {
            this.delay = delay;
        }

        @Override
        public void neighborChanged(Block self, BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
            boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
            boolean flag1 = state.getValue(BlockStateProperties.TRIGGERED);
            if (flag && !flag1) {
                world.getBlockTicks().scheduleTick(pos, self, delay);
                world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.TRUE), delay);
            } else if (!flag && flag1) {
                world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.FALSE), delay);
            }
        }

        @Override
        public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
            builder.add(BlockStateProperties.TRIGGERED);
        }
    }

}