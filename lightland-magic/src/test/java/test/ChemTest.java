package test;

import com.google.common.collect.Maps;
import com.lcy0x1.core.chem.Equation;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.chem.ReactionPool;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ChemTest {
    @Test
    public void testReact() {
        final HashMap<String, Double> map = new HashMap<>();
        map.put("elem.earth", 2d);
        map.put("item.turtle_shell", 1d);

        //noinspection ArraysAsListWithZeroOrOneArgument
        final val pool = new ReactionPool(Arrays.asList(
            "elem.earth",
            "item.turtle_shell",
            "item.X",
            "eff.resistance_iii",
            "eff.slow_iv"
        ), Arrays.asList(
            new Equation(
                new String[]{
                    "elem.earth",
                    "item.turtle_shell"
                },
                new String[]{
                    "eff.resistance_iii",
                    "eff.slow_iv"
                },
                5,
                ""),
            new Equation(
                new String[]{
                    "eff.resistance_iii",
                    "item.turtle_shell"
                },
                new String[]{
                    "eff.resistance_iii",
                    "item.X"
                },
                3,
                "")
        ), map);
        for (int i = 0; i < 10000; i++) {
            testByReactionPool(pool);
        }
    }

    public static void testByEquationPool(EquationPool pool) {
        Map<String, Double> map = Maps.newLinkedHashMap();
        //map.put("eff.speed_ii", 1);
        map.put("elem.earth", 2d);
        map.put("item.turtle_shell", 1d);
        ReactionPool react = pool.getPool(map, "potion");
        testByReactionPool(react);
    }

    public static void testByReactionPool(ReactionPool react) {
        //test(react.newEvaluator());
        ReactionPool.Evaluator ev = react.newEvaluator();
        //long time = System.nanoTime();
        int step = test(ev);
        //long stop = System.nanoTime();
        //log.info("step: {}", step);
        //log.info("deviation: {}", ev.deviation());
        ReactionPool.Result result = ev.toResult();
        //result.getMap().forEach((k, v) -> System.out.println(k + ": " + v));
        //log.info("time: {}", (stop - time) / 1000 + " us");
    }

    private static int test(ReactionPool.Evaluator ev) {
        int step = 0;
        do {
            ev.step();
            step++;
        } while (ev.deviation() > 1e-6);
        return step;
    }

}
