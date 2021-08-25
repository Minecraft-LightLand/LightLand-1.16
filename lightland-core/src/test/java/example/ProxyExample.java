package example;

import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyExample implements ProxyContainer<ProxyMethod> {
    public static ProxyExample newProxyTest() {
        return (ProxyExample) enhancer.create(construct, construct);
    }

    private static final Enhancer enhancer = new Enhancer();

    private static final Class<?>[] construct = {};

    static {
        enhancer.setSuperclass(ProxyExample.class);
        enhancer.setCallback(new ProxyInterceptor());
    }

    @Getter
    private final ListProxy<ProxyMethod> proxy = new ListProxy<>();
    private final int a = 1;

    public ProxyExample() {
        for (int i = 0; i < 100; i++) {
            proxy.addProxy((GetA) () -> {
                //System.out.println("on proxy");
                return 0;
            });
            proxy.addProxy((GetB) () -> {
                System.out.println("on proxy");
                return 3;
            });
        }

        final AtomicInteger r = new AtomicInteger();
        int loopTime = 10;
        for (int i = 0; i < loopTime; i++) {
            proxy.getProxyList().stream().filter(p -> p instanceof GetA).forEach(p -> {
                r.set(((GetA) p).getA());
            });
            r.set(getA());
        }

        try {
            final Method onProxy = getClass().getMethod("getA");
            onProxy.setAccessible(true);
            for (int i = 0; i < loopTime; i++) {
                onProxy.invoke(this);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
        }
    }

    @Test
    public void test() {
        Proxy.of("");
        final ProxyExample proxyExample = newProxyTest();
        System.out.println(proxyExample.getA());
        System.out.println(proxyExample.getB());
        System.out.println(Arrays.toString(proxyExample.getClass().getDeclaredFields()));
    }

    @Test
    public void testForFirst() {
        final ProxyExample proxyExample = newProxyTest();
        for (int i = 0; i < 10; i++) {
            System.out.println(proxyExample.getB());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(proxyExample.getC());
        }
    }

    @Test
    public void performanceTest() {
        final ProxyExample proxyExample = newProxyTest();
        long timeStart, timeEnd;
        int loopTime = 1000000;
        final AtomicInteger r = new AtomicInteger();

        for (int i = 0; i < loopTime; i++) {
            proxy.getProxyList().stream().filter(p -> p instanceof GetA).forEach(p -> {
                r.set(((GetA) p).getA());
            });
            r.set(getA());
        }

        timeStart = System.currentTimeMillis();
        for (int i = 0; i < loopTime; i++) {
            proxy.getProxyList().stream().filter(p -> p instanceof GetA)
                    .map(p -> (GetA) p)
                    .forEach(p -> {
                        r.set(p.getA());
                    });
            r.set(getA());
        }
        timeEnd = System.currentTimeMillis();
        System.out.println(timeEnd - timeStart);


        for (int i = 0; i < loopTime; i++) {
            r.set(proxyExample.getA());
        }

        timeStart = System.currentTimeMillis();
        for (int i = 0; i < loopTime; i++) {
            r.set(proxyExample.getA());
        }
        timeEnd = System.currentTimeMillis();
        System.out.println(timeEnd - timeStart);
        System.out.println(r);
    }

    @ForEachProxy
    public int getA() {
        return a;
    }

    @ForFirstProxy
    public int getB() {
        System.out.println("not on proxy");
        return 2;
    }

    @ForFirstProxy
    public int getC() {
        System.out.println("not on proxy");
        return 2;
    }

    interface GetA extends ProxyMethod {
        int getA();
    }

    interface GetB extends ProxyMethod {
        int getB();
    }

    public static class GetAImpl implements ProxyExample.GetA {
        @Override
        public int getA() {
            return 0;
        }
    }

}
