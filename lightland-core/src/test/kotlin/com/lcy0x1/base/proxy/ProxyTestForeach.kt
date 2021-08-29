package com.lcy0x1.base.proxy

import cn.tursom.forge.Open
import com.lcy0x1.base.proxy.annotation.ForEachProxy
import com.lcy0x1.base.proxy.annotation.WithinProxyContext
import com.lcy0x1.base.proxy.container.ListProxyMethodContainer
import com.lcy0x1.base.proxy.container.ProxyMethodContainer
import com.lcy0x1.base.proxy.handler.ProxyMethod
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class ProxyTestForeach {
    companion object {
        const val proxyCount = 10
        const val log: Boolean = false
        private val proxiedCount = AtomicInteger()
        val enhance = ProxyInterceptor.getEnhancer(ProxyTestTarget::class.java)
        val proxyTestTarget = enhance.create() as ProxyTestTarget
    }

    @WithinProxyContext(pre = true)
    private interface TestInterface : ProxyMethod {
        val testProxy: Int
        val testBefore: Int
        val testBeforeWithReturn: Int
        val testAfter: Int
    }

    @Open
    class ProxyTestTarget private constructor(
        val random: Int = Random.nextInt(),
        private val proxy: ListProxyMethodContainer<ProxyMethod> = ListProxyMethodContainer(),
    ) : Proxy<ProxyMethod> {
        @get:ForEachProxy
        val testProxy: Int by ::random

        @get:ForEachProxy(type = ForEachProxy.LoopType.BEFORE)
        val testBefore: Int by ::random

        @get:ForEachProxy(type = ForEachProxy.LoopType.AFTER)
        val testAfter: Int by ::random

        @get:ForEachProxy(type = ForEachProxy.LoopType.BEFORE_WITH_RETURN)
        val testBeforeWithReturn: Int by ::random

        override fun getProxyContainer(): ProxyMethodContainer<out ProxyMethod> = proxy

        init {
            repeat(proxyCount) {
                proxy.addProxy(object : TestInterface {
                    override val testProxy: Int
                        get() = addPre()
                    override val testBefore: Int
                        get() = addPre()
                    override val testBeforeWithReturn: Int
                        get() = addPre()
                    override val testAfter: Int
                        get() = addPre()

                    fun addPre(): Int {
                        proxiedCount.incrementAndGet()
                        val pre = (ProxyContext.local()!![ProxyContext.pre] ?: Result.failed).result
                        if (log) println("pre: $pre")
                        return if (pre is Int) {
                            pre + 1
                        } else {
                            1
                        }
                    }
                })
            }
        }
    }

    private inline fun test(action: () -> Unit) {
        proxiedCount.set(0)
        action()
        assert(proxiedCount.get() == proxyCount)
    }

    @Test
    fun testProxy() = test {
        assert(proxyTestTarget.testProxy == proxyTestTarget.random)
    }

    @Test
    fun testBefore() = test {
        assert(proxyTestTarget.testBefore == proxyTestTarget.random)
    }

    @Test
    fun testBeforeWithReturn() = test {
        assert(proxyTestTarget.testBeforeWithReturn == proxyCount)
    }

    @Test
    fun testAfter() = test {
        assert(proxyTestTarget.testAfter == proxyTestTarget.random + proxyCount)
    }
}