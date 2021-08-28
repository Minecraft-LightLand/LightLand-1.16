package com.lcy0x1.base.proxy

import org.junit.Test
import kotlin.random.Random

class ProxyContextTest {
    companion object {
        val testKey = ProxyContext.Key<Int>()
    }

    val proxyContext = ProxyContext()

    inline fun test(action: () -> Unit) {
        proxyContext.clean()
        action()
    }

    @Test
    fun testClean() = test {
        proxyContext[testKey] = Random.nextInt()
        assert(proxyContext[testKey] != null)
        proxyContext.clean()
        assert(proxyContext[testKey] == null)
    }

    @Test
    fun testRemove() = test {
        proxyContext[testKey] = Random.nextInt()
        assert(proxyContext[testKey] != null)
        proxyContext.remove(testKey)
        assert(proxyContext[testKey] == null)
    }

    @Test
    fun testSetAndGet() = test {
        val random = Random.nextInt()
        proxyContext[testKey] = random
        assert(proxyContext[testKey] == random)
    }

    @Test
    fun testGetAndRemove() = test {
        val random = Random.nextInt()
        proxyContext[testKey] = random
        assert(proxyContext.getAndRemove(testKey) == random)
        assert(proxyContext[testKey] == null)
    }

    @Test
    fun testParent() = test {
        val random = Random.nextInt()
        proxyContext[testKey] = random
        val subContext = proxyContext.subContext

        // test get by parent
        assert(subContext[testKey] == random)

        // test set without parent
        subContext[testKey] = random + 1
        assert(subContext[testKey] == random + 1)
        assert(proxyContext[testKey] == random)

        // remove local data, re get by parent
        subContext.clean()
        assert(subContext[testKey] == random)
    }
}