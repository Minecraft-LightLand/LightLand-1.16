package cn.tursom

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import org.junit.Test

class AbstractBlockFunctionGenerator {
  @Test
  fun test() {
    println(Block::class.java.getDeclaredMethod("getHarvestLevel", BlockState::class.java))
    // getA(Block::getHarvestLevel)
  }

  fun getA(f: Function1<Int, BlockState>) {
    println(f)
  }
}