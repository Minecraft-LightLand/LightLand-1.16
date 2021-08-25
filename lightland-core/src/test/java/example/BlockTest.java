package example;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BlockTest {

    public BlockTest() {

    }

    @Test
    public void run() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        String ans = list.stream().reduce("-", (s, i) -> s + i, (a, b) -> null);
        System.out.println("final = " + ans);
    }

}
