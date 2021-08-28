package cn.tursom.forge

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.LogicalSidedProvider
import kotlin.coroutines.CoroutineContext

object ClientCoroutineDispatcher : CoroutineDispatcher() {
    private val minecraftServer: Minecraft = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT)

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        minecraftServer.execute(block)
    }
}