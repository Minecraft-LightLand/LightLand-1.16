package example;

import com.lcy0x1.base.proxy.Proxy;
import com.lcy0x1.base.proxy.ProxyInterceptor;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import com.lcy0x1.base.proxy.annotation.ForFirstProxy;
import com.lcy0x1.base.proxy.container.ListProxyMethodContainer;
import com.lcy0x1.base.proxy.handler.ProxyMethod;
import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyExample implements Proxy<ProxyMethod> {
    public static ProxyExample newProxyTest() {
        return (ProxyExample) enhancer.create(construct, construct);
    }

    private static final Enhancer enhancer = ProxyInterceptor.getEnhancer(ProxyExample.class);

    private static final Class<?>[] construct = {};

    @Getter
    private final ListProxyMethodContainer<ProxyMethod> proxyContainer = new ListProxyMethodContainer<>();
    private final int a = 1;
    final AtomicInteger r = new AtomicInteger();
    private boolean testPerformance;

    public ProxyExample() {
        testPerformance = true;
        for (int i = 0; i < 20; i++) {
            proxyContainer.addProxy((GetA) () -> {
                if (!testPerformance) {
                    //System.out.println("on proxy get A");
                }
                return 0;
            });
            proxyContainer.addProxy((GetB) () -> {
                System.out.println("on proxy get B");
                return 3;
            });
        }
        proxyContainer.addProxy((SetA) a -> {
            System.out.println("on set a");
            r.set(a);
        });

        final AtomicInteger r = new AtomicInteger();
        int loopTime = 10;
        for (int i = 0; i < loopTime; i++) {
            proxyContainer.getProxyList().stream().filter(p -> p instanceof GetA).forEach(p -> {
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
        testPerformance = false;
    }

    //@Test
    //public void testMethodHandle() throws NoSuchMethodException, IllegalAccessException {
    //    final ProxyExample proxyExample = newProxyTest();
    //    final Method setA = proxyExample.getClass().getDeclaredMethod("setA", int.class);
    //    final MethodHandle methodHandle = MethodHandles.lookup().findVirtual(
    //        proxy.iterator().next().getClass(),
    //        "setA",
    //        MethodType.methodType(setA.getReturnType(), setA.getParameterTypes()));
    //    System.out.println(methodHandle);
    //}

    @Test
    public void test() {
        final ProxyExample proxyExample = newProxyTest();
        System.out.println(proxyExample.getA());
        System.out.println(proxyExample.getB());
        proxyExample.setA(100);
        System.out.println(r.get());
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
        testPerformance = true;
        final ProxyExample proxyExample = newProxyTest();
        long timeStart, timeEnd;
        int loopTime = 10000000;

        for (int i = 0; i < loopTime; i++) {
            for (ProxyMethod p : proxyContainer) {
                if (!(p instanceof GetA)) continue;
                ((GetA) p).getA();
            }
            r.set(getA());
        }

        timeStart = System.currentTimeMillis();
        for (int i = 0; i < loopTime; i++) {
            for (ProxyMethod p : proxyContainer) {
                if (!(p instanceof GetA)) continue;
                ((GetA) p).getA();
            }
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

    @ForEachProxy
    public void setA(int a) {
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

    public interface GetA extends ProxyMethod {
        int getA();
    }

    public interface SetA extends ProxyMethod {
        void setA(int a);
    }

    public interface GetB extends ProxyMethod {
        int getB();
    }

    public static class GetAImpl implements ProxyExample.GetA {
        @Override
        public int getA() {
            return 0;
        }
    }

}
