package com.lcy0x1.base;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BaseBlock extends Block {

    public static class BlockImplementor {

        private final Properties props;
        private final List<IState> stateList = new ArrayList<>();
        private final List<IRep> repList = new ArrayList<>();

        private IRotMir rotmir;
        private IFace face;
        private ITE ite;
        private IClick click;
        private ILight light;
        private IPower power;

        public BlockImplementor(Properties p) {
            props = p;
        }

        public BlockImplementor addImpl(IImpl impl) {
            if (impl instanceof IState)
                stateList.add((IState) impl);
            if (impl instanceof IRep)
                repList.add((IRep) impl);
            if (impl instanceof STE)
                impl = new TEPvd((STE) impl);
            for (Field f : getClass().getDeclaredFields())
                if (IImpl.class.isAssignableFrom(f.getType()) && f.getType().isAssignableFrom(impl.getClass()))
                    try {
                        f.setAccessible(true);
                        if (f.get(this) != null)
                            throw new RuntimeException("implementation conflict");
                        f.set(this, impl);
                    } catch (Exception e) {
                        throw new RuntimeException("security error");
                    }
            return this;
        }

        public BlockImplementor addImpls(IImpl... impls) {
            for (IImpl impl : impls)
                if (impl != null)
                    addImpl(impl);
            return this;
        }

    }

    public interface IClick extends IImpl {

        ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r);

    }

    public interface IImpl {
    }

    public interface ILight extends IImpl {

        int getLightValue(BlockState bs, IBlockReader w, BlockPos pos);

    }

    public interface IRep extends IImpl {

        void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving);

    }

    public interface IState extends IImpl {

        void fillStateContainer(Builder<Block, BlockState> builder);

    }

    public interface STE extends IImpl, Supplier<TileEntity> {

        @Override
        TileEntity get();

    }

    private static class AllDireBlock implements IFace, IState {

        private AllDireBlock() {
        }

        @Override
        public void fillStateContainer(Builder<Block, BlockState> builder) {
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
        public void fillStateContainer(Builder<Block, BlockState> builder) {
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

    private interface IFace extends IImpl {

        BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

    }

    private interface IPower extends IImpl {

        int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d);

    }

    private interface IRotMir extends IImpl {

        BlockState mirror(BlockState state, Mirror mirrorIn);

        BlockState rotate(BlockState state, Rotation rot);
    }

    private interface ITE extends IImpl {

        TileEntity createTileEntity(BlockState state, IBlockReader world);

    }

    private static class Power implements IState, IPower {

        private Power() {
        }

        @Override
        public void fillStateContainer(Builder<Block, BlockState> builder) {
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
            if (w.isClientSide())
                return ActionResultType.SUCCESS;
            TileEntity te = w.getBlockEntity(pos);
            if (te instanceof INamedContainerProvider)
                pl.openMenu((INamedContainerProvider) te);
            return ActionResultType.SUCCESS;
        }

    }

    public static final Power POW = new Power();
    public static final AllDireBlock ALD = new AllDireBlock();
    public static final HorizontalBlock HOR = new HorizontalBlock();

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static BlockImplementor TEMP;

    public static BlockImplementor construct(BlockProp bb) {
        return new BlockImplementor(bb.getProps());
    }

    private static Properties handler(BlockImplementor bi) {
        if (TEMP != null)
            throw new RuntimeException("concurrency error");
        TEMP = bi;
        return bi.props;
    }

    private BlockImplementor impl;

    public BaseBlock(BlockImplementor bimpl) {
        super(handler(bimpl));
    }

    public BaseBlock(BlockProp p, IImpl... impl) {
        this(construct(p).addImpls(impl));
    }

    @Override
    public final boolean isSignalSource(BlockState bs) {
        return impl.power != null;
    }

    @Override
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (impl.ite != null)
            return impl.ite.createTileEntity(state, world);
        return null;
    }

    @Override
    public final int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        if (impl.ite == null)
            return 0;
        TileEntity te = worldIn.getBlockEntity(pos);
        return te == null ? 0 : Container.getRedstoneSignalFromBlockEntity(te);
    }

    @Override
    public final int getLightValue(BlockState bs, IBlockReader w, BlockPos pos) {
        return impl.light == null ? super.getLightValue(bs, w, pos) : impl.light.getLightValue(bs, w, pos);
    }

    @Override
    public final BlockState getStateForPlacement(BlockItemUseContext context) {
        if (impl.face == null)
            return defaultBlockState();
        return impl.face.getStateForPlacement(defaultBlockState(), context);
    }

    @Override
    public final int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
        return impl.power == null ? 0 : impl.power.getSignal(bs, r, pos, d);
    }

    @Override
    public final boolean hasTileEntity(BlockState state) {
        return impl.ite != null;
    }

    @Override
    public final BlockState mirror(BlockState state, Mirror mirrorIn) {
        if (impl.rotmir != null)
            return impl.rotmir.mirror(state, mirrorIn);
        return state;
    }

    @Override
    public final ActionResultType use(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
        return impl.click == null ? ActionResultType.PASS : impl.click.onClick(bs, w, pos, pl, h, r);
    }

    @Override
    public final void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (impl.ite != null && state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity != null) {
                if (tileentity instanceof IInventory) {
                    InventoryHelper.dropContents(worldIn, pos, (IInventory) tileentity);
                    worldIn.updateNeighbourForOutputSignal(pos, this);
                }
                worldIn.removeBlockEntity(pos);
            }
        }
        for (IRep irep : impl.repList)
            irep.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public final BlockState rotate(BlockState state, Rotation rot) {
        if (impl.rotmir != null)
            return impl.rotmir.rotate(state, rot);
        return state;
    }

    protected void addImpls(BlockImplementor impl) {
    }

    @Override
    protected final void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        impl = TEMP;
        TEMP = null;
        addImpls(impl);
        for (IState is : impl.stateList)
            is.fillStateContainer(builder);
    }

}
