package cn.tursom.forge

import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.LogicalSidedProvider
import net.minecraftforge.fml.loading.FMLEnvironment
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

object GradleMainDispatcher : MainCoroutineDispatcher() {
    private val loaded = AtomicBoolean(false)

    private var oldDispatcher: MainCoroutineDispatcher? = null
    private val mainDispatcherLoader: Class<*> = Class.forName("kotlinx.coroutines.internal.MainDispatcherLoader")
    private val dispatcherField: Field = mainDispatcherLoader.getDeclaredField("dispatcher").also { dispatcher ->
        dispatcher.isAccessible = true
        val mf: Field = Field::class.java.getDeclaredField("modifiers")
        mf.isAccessible = true
        mf.setInt(dispatcher, dispatcher.modifiers and Modifier.FINAL.inv())
    }

    private val dispatcher by lazy {
        when (FMLEnvironment.dist!!) {
            Dist.CLIENT -> LogicalSidedProvider.INSTANCE.get<Minecraft>(LogicalSide.CLIENT)
            Dist.DEDICATED_SERVER -> LogicalSidedProvider.INSTANCE.get<MinecraftServer>(LogicalSide.SERVER)
        }.asCoroutineDispatcher()
    }

    fun init() {
        if (loaded.compareAndSet(false, true)) {
            oldDispatcher = dispatcherField.get(null) as MainCoroutineDispatcher?
            dispatcherField.set(null, this)
        }
    }

    fun resume() {
        if (loaded.compareAndSet(true, false) && oldDispatcher != null) {
            dispatcherField.set(null, oldDispatcher)
        }
    }

    override val immediate: MainCoroutineDispatcher get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}