package cn.tursom

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import org.junit.Test
import java.io.File

class AbstractBlockFunctionGenerator {
  @Test
  fun test() {
    File("").inputStream().bufferedReader()
    println(Block::class.java.getDeclaredMethod("getHarvestLevel", BlockState::class.java))
    // getA(Block::getHarvestLevel)
  }

  fun getA(f: Function1<Int, BlockState>) {
    println(f)
  }
}