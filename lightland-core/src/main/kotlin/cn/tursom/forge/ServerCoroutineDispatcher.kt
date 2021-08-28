package cn.tursom.forge

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.LogicalSidedProvider
import kotlin.coroutines.CoroutineContext

object ServerCoroutineDispatcher : CoroutineDispatcher() {
  private val minecraftServer: MinecraftServer = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER)

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    minecraftServer.execute(block)
  }
}

