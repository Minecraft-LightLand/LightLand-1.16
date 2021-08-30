package com.lcy0x1.base.proxy

import cn.tursom.forge.Open
import com.lcy0x1.base.proxy.annotation.ForFirstProxy
import com.lcy0x1.base.proxy.annotation.WithinProxyContext
import com.lcy0x1.base.proxy.container.ListProxyContainer
import com.lcy0x1.base.proxy.container.ProxyContainer
import com.lcy0x1.base.proxy.handler.ProxyMethod
import net.sf.cglib.proxy.MethodProxy
import org.junit.Test
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class ProxyTestFirst {
    companion object {
        const val proxyCount = 10
        const val log: Boolean = true
        private val proxiedCount = AtomicInteger()
        val enhance = ProxyInterceptor.getEnhancer(ProxyTestTarget::class.java)
        val proxyTestTarget = enhance.create() as ProxyTestTarget
    }

    @WithinProxyContext(pre = true, preSuper = true)
    private interface TestInterface : ProxyMethod {
        val testProxy: Int
        val testName: Int
        val testNameProxy: Int
        val testCache: Int
    }

    @Open
    class ProxyTestTarget : Proxy<ProxyMethod>, TestInterface {
        val random = Random.nextInt()
        private val proxy = ListProxyContainer<ProxyMethod>()

        @get:ForFirstProxy
        override val testProxy: Int by ::random

        @get:ForFirstProxy(name = "getTestNameProxy")
        override val testName: Int by ::random
        override val testNameProxy: Int by ::random

        @get:ForFirstProxy(cache = false)
        override val testCache: Int by ::random

        @get:ForFirstProxy(must = true)
        val testMust: Int by ::random

        override fun getProxyContainer(): ProxyContainer<out ProxyMethod> = proxy

        init {
            repeat(proxyCount) {
                proxy.addProxy(object : TestInterface {
                    override val testProxy: Int get() = addPre("testProxy")
                    override val testName: Int get() = addPre("testName")
                    override val testNameProxy: Int get() = addPre("testNameProxy")

                    @get:WithinProxyContext(pre = true, preSuper = false)
                    override val testCache: Int
                        get() = addPre("testCache")

                    fun addPre(testName: String): Int {
                        proxiedCount.incrementAndGet()
                        val local = ProxyContext.local()!!
                        val pre = local.getResult(ProxyContext.objectPre)
                        if (log) println("$testName pre: $pre")
                        return if (pre is Int) {
                            pre + 1
                        } else {
                            1
                        }
                    }

                    override fun onProxy(obj: Proxy<*>, method: Method, args: Array<out Any>, proxy: MethodProxy, context: ProxyContext): Result<*>? {
                        val onProxy = Result.snapshot(super.onProxy(obj, method, args, proxy, context))
                        if (onProxy.isSuccess && method.name == "getTestCache") {
                            if (onProxy.result != 5) {
                                context[ProxyContext.continueFirstProxyMethod] = true
                            }
                        }
                        return onProxy
                    }
                })
            }
        }
    }

    private inline fun test(action: () -> Unit) {
        proxiedCount.set(0)
        action()
        println("proxiedCount: ${proxiedCount.get()}")
    }

    @Test
    fun testName() = test {
        println("testName: ${proxyTestTarget.testName}")
        assert(proxyTestTarget.testName == proxyTestTarget.random + 1)
    }

    @Test
    fun testCache() = test {
        println("testCache: ${proxyTestTarget.testCache}")
        assert(proxyTestTarget.testCache == 5)
    }

    @Test
    fun testMust() = test {
        try {
            proxyTestTarget.testMust
        } catch (e: Throwable) {
            return@test
        }
        throw RuntimeException("testMust failed")
    }
}