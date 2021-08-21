package example;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public abstract class AbstractBlock extends Block {
    public static ThreadLocal<AbstractBlock> staticProxy;

    public AbstractBlock proxy;

    public AbstractBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.proxy = staticProxy.get();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        if (staticProxy.get() != null) {
            staticProxy.get().createBlockStateDefinition(p_206840_1_);
        } else {
            super.createBlockStateDefinition(p_206840_1_);
        }
    }

    public static class BlockExample extends AbstractBlock {
        public static BlockExample newBlockExample(AbstractBlock proxy, Properties p_i48440_1_) {
            AbstractBlock.staticProxy.set(proxy);
            return new BlockExample(p_i48440_1_);
        }

        protected BlockExample(Properties p_i48440_1_) {
            super(p_i48440_1_);
        }
    }
}
