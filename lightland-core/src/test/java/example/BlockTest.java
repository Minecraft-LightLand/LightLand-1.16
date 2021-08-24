package example;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BlockTest {

    public BlockTest(){

    }

    @Test
    public void run() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        int ans = list.stream().map(e -> {
            System.out.println(e);
            return e;
        }).filter(e -> e > 5).findFirst().orElse(0);
        System.out.println("final = " + ans);
    }

}
