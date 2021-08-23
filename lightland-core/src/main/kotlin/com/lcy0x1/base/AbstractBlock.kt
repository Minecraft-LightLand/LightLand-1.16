package com.lcy0x1.base

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.BlockItemUseContext
import net.minecraft.state.StateContainer
import net.minecraft.state.properties.BlockStateProperties
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import java.util.function.Supplier
import kotlin.reflect.jvm.javaMethod

fun main() {
    println(Block::isSignalSource.javaMethod)
    println(AbstractBlock::isSignalSource.javaMethod)
    println(Block::isSignalSource.javaMethod == AbstractBlock::isSignalSource.javaMethod)
}

interface CreateBlockStateDefinition {
    companion object {

    }

    fun createBlockStateDefinition(p_206840_1_: StateContainer.Builder<Block, BlockState>)
}

@Suppress("unused")
class AbstractBlock(
    bimpl: BlockImplementor
) : Block(handler(bimpl)) {
    private var impl: BlockImplementor? = null

    constructor(
        p: BlockProp,
        vararg impl: IImpl?
    ) : this(construct(p).addImpls(impls = impl))

    override fun isSignalSource(bs: BlockState): Boolean {
        return impl?.power != null
    }

    override fun createTileEntity(state: BlockState, world: IBlockReader): TileEntity? {
        return impl?.ite?.createTileEntity(state, world)
    }

    override fun getAnalogOutputSignal(blockState: BlockState, worldIn: World, pos: BlockPos): Int {
        impl?.ite ?: return 0
        val te = worldIn.getBlockEntity(pos)
        return if (te == null) 0 else Container.getRedstoneSignalFromBlockEntity(te)
    }

    override fun getLightValue(bs: BlockState, w: IBlockReader, pos: BlockPos): Int {
        return if (impl!!.light == null) super.getLightValue(bs, w, pos) else impl!!.light!!.getLightValue(bs, w, pos)
    }

    override fun getStateForPlacement(context: BlockItemUseContext): BlockState? {
        return if (impl!!.face == null) defaultBlockState() else impl!!.face!!.getStateForPlacement(defaultBlockState(),
            context)
    }

    override fun getSignal(bs: BlockState, r: IBlockReader, pos: BlockPos, d: Direction): Int {
        return if (impl!!.power == null) 0 else impl!!.power!!.getSignal(bs, r, pos, d)
    }

    override fun hasTileEntity(state: BlockState): Boolean {
        return impl!!.ite != null
    }

    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return if (impl!!.rotmir != null) impl!!.rotmir!!.mirror(state, mirrorIn) else state
    }

    override fun use(
        bs: BlockState,
        w: World,
        pos: BlockPos,
        pl: PlayerEntity,
        h: Hand,
        r: BlockRayTraceResult,
    ): ActionResultType {
        return if (impl!!.click == null) ActionResultType.PASS else impl!!.click!!.onClick(bs, w, pos, pl, h, r)!!
    }

    override fun onRemove(state: BlockState, worldIn: World, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (impl!!.ite != null && state.block !== newState.block) {
            val tileEntity = worldIn.getBlockEntity(pos)
            if (tileEntity != null) {
                if (tileEntity is IInventory) {
                    InventoryHelper.dropContents(worldIn, pos, tileEntity)
                    worldIn.updateNeighbourForOutputSignal(pos, this)
                }
                worldIn.removeBlockEntity(pos)
            }
        }
        for (irep in impl!!.repList) irep.onReplaced(state, worldIn, pos, newState, isMoving)
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return if (impl!!.rotmir != null) impl!!.rotmir!!.rotate(state, rot) else state
    }

    protected fun addImpls(impl: BlockImplementor?) {}
    override fun createBlockStateDefinition(builder: StateContainer.Builder<Block, BlockState>) {
        impl = TEMP
        TEMP = null
        addImpls(impl)
        for (`is` in impl!!.stateList) `is`.fillStateContainer(builder)
    }

    interface IClick : IImpl {
        fun onClick(
            bs: BlockState?,
            w: World,
            pos: BlockPos,
            pl: PlayerEntity,
            h: Hand?,
            r: BlockRayTraceResult?,
        ): ActionResultType?
    }

    interface IImpl
    interface ILight : IImpl {
        fun getLightValue(bs: BlockState?, w: IBlockReader?, pos: BlockPos?): Int
    }

    interface IRep : IImpl {
        fun onReplaced(state: BlockState?, worldIn: World?, pos: BlockPos?, newState: BlockState?, isMoving: Boolean)
    }

    interface IState : IImpl {
        fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>)
    }

    interface STE : IImpl, Supplier<TileEntity> {
        override fun get(): TileEntity
    }

    internal interface IFace : IImpl {
        fun getStateForPlacement(def: BlockState, context: BlockItemUseContext): BlockState?
    }

    internal interface IPower : IImpl {
        fun getSignal(bs: BlockState, r: IBlockReader?, pos: BlockPos?, d: Direction?): Int
    }

    internal interface IRotMir : IImpl {
        fun mirror(state: BlockState, mirrorIn: Mirror): BlockState
        fun rotate(state: BlockState, rot: Rotation): BlockState
    }

    internal interface ITE : IImpl {
        fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity?
    }

    class BlockImplementor(val props: Properties) {
        val stateList: MutableList<IState> = ArrayList()
        val repList: MutableList<IRep> = ArrayList()
        internal val rotmir: IRotMir? = null
        internal val face: IFace? = null
        internal val ite: ITE? = null
        val click: IClick? = null
        val light: ILight? = null
        internal val power: IPower? = null

        fun addImpl(impl: IImpl): BlockImplementor {
            var implSnapshot = impl
            if (implSnapshot is IState) stateList.add(implSnapshot)
            if (implSnapshot is IRep) repList.add(implSnapshot)
            if (implSnapshot is STE) implSnapshot = TEPvd(implSnapshot)
            for (f in javaClass.declaredFields) if (IImpl::class.java.isAssignableFrom(f.type) && f.type.isAssignableFrom(implSnapshot.javaClass)) {
                f.isAccessible = true
                if (f[this] != null) throw RuntimeException("implementation conflict")
                f[this] = implSnapshot
            }
            return this
        }

        fun addImpls(vararg impls: IImpl?): BlockImplementor {
            for (impl in impls) impl?.let { addImpl(it) }
            return this
        }
    }

    class AllDireBlock internal constructor() : IFace, IState {
        override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
            builder.add(FACING)
        }

        override fun getStateForPlacement(def: BlockState, context: BlockItemUseContext): BlockState? {
            return def.setValue(FACING, context.clickedFace.opposite)
        }
    }

    class HorizontalBlock internal constructor() : IRotMir, IState, IFace {
        override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
            builder.add(HORIZONTAL_FACING)
        }

        override fun getStateForPlacement(def: BlockState, context: BlockItemUseContext): BlockState? {
            return def.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
        }

        override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
            return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)))
        }

        override fun rotate(state: BlockState, rot: Rotation): BlockState {
            return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)))
        }
    }

    class Power internal constructor() : IState, IPower {
        override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
            builder.add(BlockStateProperties.POWER)
        }

        override fun getSignal(bs: BlockState, r: IBlockReader?, pos: BlockPos?, d: Direction?): Int {
            return bs.getValue(BlockStateProperties.POWER)
        }
    }

    private class TEPvd(private val f: Supplier<out TileEntity>) : ITE, IClick {
        override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity {
            return f.get()
        }

        override fun onClick(
            bs: BlockState?,
            w: World,
            pos: BlockPos,
            pl: PlayerEntity,
            h: Hand?,
            r: BlockRayTraceResult?,
        ): ActionResultType {
            if (w.isClientSide()) return ActionResultType.SUCCESS
            val te = w.getBlockEntity(pos)
            if (te is INamedContainerProvider) pl.openMenu(te as INamedContainerProvider?)
            return ActionResultType.SUCCESS
        }
    }

    companion object {
        val POW = Power()
        val ALD = AllDireBlock()
        val HOR = HorizontalBlock()
        val FACING = BlockStateProperties.FACING
        val HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING
        private var TEMP: BlockImplementor? = null
        fun construct(bb: BlockProp): BlockImplementor {
            return BlockImplementor(bb.props)
        }

        private fun handler(bi: BlockImplementor): Properties {
            if (TEMP != null) throw RuntimeException("concurrency error")
            TEMP = bi
            return bi.props
        }
    }
}