package test;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.chem.ReactionPool;
import lombok.extern.log4j.Log4j2;
import net.minecraft.util.ResourceLocation;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

@Log4j2
public class ChemTest {
    @Test
    public void testReact() throws Exception {
        File f = new File(ChemTest.class.getClassLoader()
            //.getResource("lightland/recipes/config/_chemistry.json").getFile());
            .getResource("data/lightland/recipes/config_chemistry.json").getFile());
        JsonElement elem = new JsonParser().parse(new FileReader(f));
        ConfigRecipe r = RecipeRegistry.RSM_CONFIG.fromJson(new ResourceLocation(LightLand.MODID, "config_chemistry"), elem.getAsJsonObject());
        testByEquationPool(r.get("pool"));
    }

    public static void testByEquationPool(EquationPool pool) {
        Map<String, Double> map = Maps.newLinkedHashMap();
        //map.put("eff.speed_ii", 1);
        map.put("elem.earth", 2d);
        map.put("item.turtle_shell", 1d);
        ReactionPool react = pool.getPool(map, "potion");
        test(react.newEvaluator());
        ReactionPool.Evaluator ev = react.newEvaluator();
        long time = System.nanoTime();
        int step = test(ev);
        long stop = System.nanoTime();
        log.info("step: {}", step);
        log.info("deviation: {}", ev.deviation());
        ReactionPool.Result result = ev.toResult();
        result.map.forEach((k, v) -> System.out.println(k + ": " + v));
        log.info("time: {}", (stop - time) / 1000 + " us");
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
