package example;

import com.lcy0x1.base.proxy.ProxyInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

public class CGlibExample {


    @Data
    @AllArgsConstructor
    static public class TestClass {
        private int a;
        private boolean b;

        @Test
        public int getA() {
            return a;
        }
    }

    @Test
    public void test() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TestClass.class);
        enhancer.setCallback(ProxyInterceptor.INSTANCE);
        TestClass o = (TestClass) enhancer.create(
            new Class[]{int.class, boolean.class},
            new Object[]{0, false}
        );
        o.getA();
    }
}
