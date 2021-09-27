package com.hikarishima.lightland.magic.registry.block;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegenOreBlock extends Block {

    private static final double UPDATE = 0.1;
    private static final double[] PROB = {0.3, 0.2, 0.11, 0.17, 0.17, 0.05};

    public enum State implements IStringSerializable {
        REGEN_0(-1, 0),
        REGEN_1(0, 0),
        REGEN_2(1, 0),
        REGEN_3(2, 0),
        REGEN_4(3, 0),
        REGEN_5(4, 0),
        REGEN_6(5, 0),
        REGEN_7(6, 0),
        COAL(2, PROB[0]),
        IRON(3, PROB[1] / (1 - PROB[0])),
        GOLD(4, PROB[2] / (1 - PROB[0] - PROB[1])),
        LAPIS(5, PROB[3] / (1 - PROB[0] - PROB[1] - PROB[2])),
        REDSTONE(6, PROB[4] / (1 - PROB[0] - PROB[1] - PROB[2] - PROB[3])),
        DIAMOND(7, 1);

        private final int stage;
        private final double chance;

        State(int stage, double chance) {
            this.stage = stage;
            this.chance = chance;
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

    public RegenOreBlock(Properties props) {
        super(props.randomTicks());
    }

    public void playerDestroy(World w, PlayerEntity pl, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(w, pl, pos, state, te, stack);
        w.setBlock(pos, defaultBlockState(), 3);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
        if (r.nextDouble() < UPDATE)
            world.setBlock(pos, state.setValue(ORE, state.getValue(ORE).next(r)), 3);
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

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ORE);
    }
}
