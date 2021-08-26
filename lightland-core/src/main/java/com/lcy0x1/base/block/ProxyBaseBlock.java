package com.lcy0x1.base.block;

import com.lcy0x1.base.block.impl.TEPvd;
import com.lcy0x1.base.block.mult.*;
import com.lcy0x1.base.block.one.*;
import com.lcy0x1.base.block.type.IImpl;
import com.lcy0x1.base.block.type.STE;
import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sf.cglib.proxy.Enhancer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Log4j2
public class ProxyBaseBlock extends BaseBlock implements Proxy<IImpl> {

    private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

    private static final Enhancer ENHANCER = new Enhancer();
    private static final Class<?>[] CONSTRUCTOR = {BlockProp.class, IImpl[].class};

    static {
        ENHANCER.setSuperclass(ProxyBaseBlock.class);
        ENHANCER.setCallback(new ProxyInterceptor());
    }

    public static BaseBlock newBaseBlock(BlockProp p, IImpl... impl) {
        return (BaseBlock) ENHANCER.create(CONSTRUCTOR, new Object[]{p, impl});
    }

    private BlockImplementor impl;

    public ProxyBaseBlock(BlockProp p, IImpl... impl) {
        super(handler(construct(p).addImpls(impl)));
        registerDefaultState(this.impl.execute(IDefaultState.class).reduce(defaultBlockState(),
                (state, def) -> def.getDefaultState(state), (a, b) -> a));
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
    public boolean isSignalSource(BlockState bs) {
        return impl.one(IPower.class).isPresent();
    }

    @Override
    @ForFirstProxy(value = ITE.class, name = "createTileEntity")
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        return impl.one(ITE.class).map(e -> Optional.ofNullable(worldIn.getBlockEntity(pos))
                .map(Container::getRedstoneSignalFromBlockEntity).orElse(0)).orElse(0);
    }

    @Override
    @ForFirstProxy(value = ILight.class, name = "getLightValue")
    public int getLightValue(BlockState bs, IBlockReader w, BlockPos pos) {
        return super.getLightValue(bs, w, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return impl.execute(IPlacement.class).reduce(defaultBlockState(),
                (state, impl) -> impl.getStateForPlacement(state, context), (a, b) -> a);
    }

    @Override
    @ForFirstProxy(value = IPower.class, name = "getSignal")
    public int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
        return 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return impl.one(ITE.class).isPresent();
    }

    @Override
    @ForFirstProxy(value = IRotMir.class, name = "mirror")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }

    @Override
    @ForFirstProxy(value = IClick.class, name = "onClick")
    public ActionResultType use(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
        //log.warn("this class: {}, stack trace: ", getClass(), new Throwable());
        return ActionResultType.PASS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
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
    @ForFirstProxy(value = IRotMir.class, name = "rotate")
    public BlockState rotate(BlockState state, Rotation rot) {
        return state;
    }

    @Override
    @ForEachProxy(value = IState.class, name = "createBlockStateDefinition")
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
        impl.execute(INeighborUpdate.class).forEach(e -> e.neighborChanged(this, state, world, pos, nei_block, nei_pos, moving));
        super.neighborChanged(state, world, pos, nei_block, nei_pos, moving);
    }

    @Override
    @ForEachProxy(value = IRandomTick.class, name = "randomTick")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Override
    @ForEachProxy(value = IScheduleTick.class, name = "tick")
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random r) {
        impl.execute(IAnimateTick.class).forEach(e -> e.animateTick(state, world, pos, r));
    }

    @NotNull
    @Override
    public ProxyMethodContainer<? extends IImpl> getProxy() {
        if (impl == null) {
            final BlockImplementor blockImplementor = TEMP.get();
            if (blockImplementor != null) {
                impl = blockImplementor;
                TEMP.remove();
            } else {
                throw new NoProxyFoundException();
            }
        }
        return impl;
    }

    public static class BlockImplementor implements DelegatedProxyMethodContainer<IImpl> {
        @Getter
        private long lastModify = 0;
        private final Properties props;
        @NotNull
        @Getter
        private final MutableProxyMethodContainer<IImpl> proxy = new ListProxyMethodContainer<>();

        public BlockImplementor(Properties p) {
            props = p;
        }

        public BlockImplementor addImpls(IImpl... impls) {
            lastModify = System.currentTimeMillis();
            for (IImpl impl : impls)
                if (impl instanceof STE)
                    proxy.addProxy(new TEPvd((STE) impl));
                else if (impl != null)
                    proxy.addProxy(impl);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends IImpl> Stream<T> execute(Class<T> cls) {
            return StreamSupport.stream(proxy.spliterator(), false).filter(cls::isInstance).map(e -> (T) e);
        }

        public <T extends IImpl> Optional<T> one(Class<T> cls) {
            return execute(cls).findFirst();
        }
    }

}