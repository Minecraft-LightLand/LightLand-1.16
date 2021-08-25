package example;

import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

import java.util.Arrays;

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

    @Test
    public void test() {
        final ProxyExample proxyExample = newProxyTest();
        System.out.println(proxyExample.getA());
        System.out.println(Arrays.toString(proxyExample.getClass().getDeclaredFields()));
    }

    @Getter
    private final MutableProxy<ProxyMethod> proxy = new ListProxy<>();

    private int a;

    public ProxyExample() {
        proxy.addProxy((GetA) () -> {
            System.out.println("on proxy method");
            return 0;
        });
    }

    @ForEachProxy
    public int getA() {
        return a;
    }

    interface GetA extends ProxyMethod {
        int getA();
    }
}
