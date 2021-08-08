package test;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.chem.ReactionPool;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.nbt.CompoundNBT;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class ChemTest {

    public static void main(String[] args) throws Exception {
        File file = new File("./src/test/java/test/_test.json");
        JsonElement je = new JsonParser().parse(new FileReader(file));
        EquationPool pool = Serializer.from(je, EquationPool.class, null);
        Map<String, Integer> map = Maps.newLinkedHashMap();
        //map.put("eff.speed_ii", 1);
        map.put("elem.air", 1);
        map.put("elem.quint", 1);
        map.put("item.sugar", 1);
        ReactionPool react = pool.getPool(map);
        test(react.new Evaluator());
        ReactionPool.Evaluator ev = react.new Evaluator();
        long time = System.nanoTime();
        int step = test(ev);
        long stop = System.nanoTime();
        System.out.println("step: " + step);
        System.out.println("deviation: " + ev.deviation());
        System.out.println(Automator.toTag(new CompoundNBT(), ev));
        System.out.print("time: " + (stop - time) / 1000 + " us");
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
