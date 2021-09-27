package com.hikarishima.lightland.magic.registry.block;

import com.google.common.collect.Lists;
import com.hikarishima.lightland.magic.registry.ParticleRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegenOreBlock extends Block {

    private static final double UPDATE = 0.1;
    private static final double[] PROB = {0.3, 0.2, 0.11, 0.17, 0.17, 0.05};

    public enum State implements IStringSerializable {
        REGEN_0(-1, 0, Blocks.STONE::defaultBlockState),
        REGEN_1(0, 0, Blocks.STONE::defaultBlockState),
        REGEN_2(1, 0, Blocks.STONE::defaultBlockState),
        REGEN_3(2, 0, Blocks.STONE::defaultBlockState),
        REGEN_4(3, 0, Blocks.STONE::defaultBlockState),
        REGEN_5(4, 0, Blocks.STONE::defaultBlockState),
        REGEN_6(5, 0, Blocks.STONE::defaultBlockState),
        REGEN_7(6, 0, Blocks.STONE::defaultBlockState),
        COAL(2, PROB[0], Blocks.COAL_ORE::defaultBlockState),
        IRON(3, PROB[1] / (1 - PROB[0]), Blocks.IRON_ORE::defaultBlockState),
        GOLD(4, PROB[2] / (1 - PROB[0] - PROB[1]), Blocks.GOLD_ORE::defaultBlockState),
        LAPIS(5, PROB[3] / (1 - PROB[0] - PROB[1] - PROB[2]), Blocks.LAPIS_ORE::defaultBlockState),
        REDSTONE(6, PROB[4] / (1 - PROB[0] - PROB[1] - PROB[2] - PROB[3]), Blocks.REDSTONE_ORE::defaultBlockState),
        DIAMOND(7, 1, Blocks.DIAMOND_ORE::defaultBlockState);

        private final int stage;
        private final double chance;
        public final Supplier<BlockState> fake;

        State(int stage, double chance, Supplier<BlockState> fake) {
            this.stage = stage;
            this.chance = chance;
            this.fake = fake;
        }

        public State next(Random r) {
            if (ordinal() >= 8) {
                return this;
            }
            double rand = r.nextDouble();
            State def = State.values()[Math.min(7, ordinal() + 1)];
            State ore = ordinal() < COAL.stage ? null : State.values()[ordinal() + 6];
            if (ore == null) {
                return def;
            }
            return rand < ore.chance ? ore : def;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public static class OreProperty extends EnumProperty<State> {

        protected OreProperty() {
            super("ore", State.class, Lists.newArrayList(State.values()));
        }
    }

    public static final Property<State> ORE = new OreProperty();

    public RegenOreBlock() {
        super(AbstractBlock.Properties.copy(Blocks.STONE).randomTicks());
    }

    public void playerDestroy(World w, PlayerEntity pl, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(w, pl, pos, state.getValue(ORE).fake.get(), te, stack);
        w.setBlock(pos, defaultBlockState(), 3);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
        if (r.nextDouble() < UPDATE)
            world.setBlock(pos, state.setValue(ORE, state.getValue(ORE).next(r)), 3);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        return super.getExplosionResistance(state.getValue(ORE).fake.get(), world, pos, explosion);
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        return super.getDestroyProgress(state.getValue(ORE).fake.get(), player, world, pos);
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        State st = state.getValue(ORE);
        if (st == State.COAL) return 1;
        if (st == State.IRON) return 2;
        if (st == State.LAPIS) return 2;
        if (st == State.GOLD) return 3;
        if (st == State.REDSTONE) return 3;
        if (st == State.DIAMOND) return 3;
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        IParticleData particle = ParticleRegistry.PICKAXE.get(getHarvestLevel(state)).get();
        double d0 = pos.getX() + 0.5D;
        double d1 = pos.getY();
        double d2 = pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.1D) {
            world.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        }
        double d4 = random.nextDouble() * 0.6D - 0.3D;
        double d6 = random.nextDouble() * 6.0D / 16.0D;
        world.addParticle(particle, d0 + d4, d1 + d6, d2 + d4, 0.0D, 0.0D, 0.0D);

    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ORE);
    }

}
