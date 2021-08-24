package example;

import com.lcy0x1.base.proxy.*;
import com.lcy0x1.base.proxy.annotation.ForEachProxy;
import lombok.Getter;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

public class ProxyExample implements ProxyContainer<ProxyMethod> {

    private static final Enhancer enhancer = new Enhancer();
    private static final Class<?>[] construct = {};

    static {
        enhancer.setSuperclass(ProxyExample.class);
        enhancer.setCallback(new ProxyInterceptor());
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

    public static ProxyExample newProxyTest() {
        return (ProxyExample) enhancer.create(construct, construct);
    }

    @ForEachProxy
    public int getA() {
        return a;
    }

    @Test
    public void test() {
        System.out.println(newProxyTest().getA());
    }

    interface GetA extends ProxyMethod {
        int getA();
    }
}
