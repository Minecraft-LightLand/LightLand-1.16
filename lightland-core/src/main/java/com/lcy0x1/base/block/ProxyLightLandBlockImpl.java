package com.lcy0x1.base.block;

import com.lcy0x1.base.block.mult.*;
import com.lcy0x1.base.block.one.BlockPowerBlockMethod;
import com.lcy0x1.base.block.one.LightBlockMethod;
import com.lcy0x1.base.block.one.MirrorRotateBlockMethod;
import com.lcy0x1.base.block.one.TitleEntityBlockMethod;
import com.lcy0x1.base.block.type.BlockMethod;
import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyInterceptor;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.proxy.container.DelegatedProxyContainer;
import com.lcy0x1.base.proxy.container.ListProxyContainer;
import com.lcy0x1.base.proxy.container.MutableProxyContainer;
import com.lcy0x1.base.proxy.container.ProxyContainer;
import com.lcy0x1.base.proxy.exception.NoProxyFoundException;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Log4j2
public class ProxyLightLandBlockImpl extends LightLandBlock implements Proxy<BlockMethod> {

    private static final ThreadLocal<BlockImplementor> TEMP = new ThreadLocal<>();

    private static final Enhancer ENHANCER = ProxyInterceptor.getEnhancer(ProxyLightLandBlockImpl.class);
    private static final Class<?>[] CONSTRUCTOR = {LightLandBlockProperties.class, BlockMethod[].class};

    public static LightLandBlock newBaseBlock(LightLandBlockProperties p, BlockMethod... impl) {
        return (LightLandBlock) ENHANCER.create(CONSTRUCTOR, new Object[]{p, impl});
    }

    private BlockImplementor impl;

    public ProxyLightLandBlockImpl(LightLandBlockProperties p, BlockMethod... impl) {
        super(handler(construct(p).addImpls(impl)));
        registerDefaultState(this.impl.execute(DefaultStateBlockMethod.class).reduce(defaultBlockState(),
                (state, def) -> def.getDefaultState(state), (a, b) -> a));
    }

    public static BlockImplementor construct(LightLandBlockProperties bb) {
        return new BlockImplementor(bb.getProps());
    }

    private static Properties handler(BlockImplementor bi) {
        if (TEMP.get() != null)
            throw new RuntimeException("concurrency error");
        TEMP.set(bi);
        return bi.props;
    }

    @Override
    @ForFirstProxy(value = BlockPowerBlockMethod.class, name = "isSignalSource")
    public boolean isSignalSource(BlockState bs) {
        return false;
    }

    @Override
    @ForFirstProxy(value = TitleEntityBlockMethod.class, name = "createTileEntity")
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        return impl.one(TitleEntityBlockMethod.class).map(e -> Optional.ofNullable(worldIn.getBlockEntity(pos))
                .map(Container::getRedstoneSignalFromBlockEntity).orElse(0)).orElse(0);
    }

    @Override
    @ForFirstProxy(value = LightBlockMethod.class, name = "getLightValue")
    public int getLightValue(BlockState bs, IBlockReader w, BlockPos pos) {
        return super.getLightValue(bs, w, pos);
    }

    @Override
    @ForEachProxy(value = PlacementBlockMethod.class, name = "getStateForPlacement", type = ForEachProxy.LoopType.AFTER)
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState();
    }

    @Override
    @ForFirstProxy(value = BlockPowerBlockMethod.class, name = "getSignal")
    public int getSignal(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
        return 0;
    }

    @Override
    @ForFirstProxy(value = TitleEntityBlockMethod.class, name = "hasTileEntity")
    public boolean hasTileEntity(BlockState state) {
        return false;
    }

    @Override
    @ForFirstProxy(value = MirrorRotateBlockMethod.class, name = "mirror")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }

    @Override
    @ForFirstProxy(value = OnClickBlockMethod.class, name = "onClick", cache = false)
    public ActionResultType use(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
        return ActionResultType.PASS;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (impl.one(TitleEntityBlockMethod.class).isPresent() && state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity != null) {
                if (tileentity instanceof IInventory) {
                    InventoryHelper.dropContents(worldIn, pos, (IInventory) tileentity);
                    worldIn.updateNeighbourForOutputSignal(pos, this);
                }
                worldIn.removeBlockEntity(pos);
            }
        }
        impl.execute(OnReplacedBlockMethod.class).forEach(e -> e.onReplaced(state, worldIn, pos, newState, isMoving));
    }

    @Override
    @ForFirstProxy(value = MirrorRotateBlockMethod.class, name = "rotate")
    public BlockState rotate(BlockState state, Rotation rot) {
        return state;
    }

    @Override
    @ForEachProxy(value = CreateBlockStateBlockMethod.class, name = "createBlockStateDefinition")
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block nei_block, BlockPos nei_pos, boolean moving) {
        impl.execute(NeighborUpdateBlockMethod.class).forEach(e -> e.neighborChanged(this, state, world, pos, nei_block, nei_pos, moving));
        super.neighborChanged(state, world, pos, nei_block, nei_pos, moving);
    }

    @Override
    @ForEachProxy(value = RandomTickBlockMethod.class, name = "randomTick")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Override
    @ForEachProxy(value = ScheduleTickBlockMethod.class, name = "tick") // skip test
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @ForEachProxy(value = AnimateTickBlockMethod.class, name = "animateTick")
    public void animateTick(BlockState state, World world, BlockPos pos, Random r) {
        //impl.execute(IAnimateTick.class).forEach(e -> e.animateTick(state, world, pos, r));
    }

    @NotNull
    @Override
    public ProxyContainer<? extends BlockMethod> getProxyContainer() {
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

    public static class BlockImplementor implements DelegatedProxyContainer<BlockMethod> {
        private final Properties props;
        @NotNull
        @Getter
        private final MutableProxyContainer<BlockMethod> proxy = new ListProxyContainer<>();

        public BlockImplementor(Properties p) {
            props = p;
        }

        public BlockImplementor addImpls(BlockMethod... impls) {
            proxy.addAllProxy(Arrays.asList(impls));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends BlockMethod> Stream<T> execute(Class<T> cls) {
            return StreamSupport.stream(proxy.spliterator(), false).filter(cls::isInstance).map(e -> (T) e);
        }

        public <T extends BlockMethod> Optional<T> one(Class<T> cls) {
            return execute(cls).findFirst();
        }
    }

}