package test;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.chem.ReactionPool;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class ChemTest {

    public static void main(String[] args) throws Exception {
        File f = new File("./src/test/resources/lightland/recipes/config/_chemistry.json");
        JsonElement elem = new JsonParser().parse(new FileReader(f));
        ConfigRecipe r = RecipeRegistry.RSM_CONFIG.fromJson(new ResourceLocation(LightLand.MODID, "config_chemistry"), elem.getAsJsonObject());
        EquationPool pool = r.get("pool");
        Map<String, Double> map = Maps.newLinkedHashMap();
        //map.put("eff.speed_ii", 1);
        map.put("elem.earth", 2d);
        map.put("item.turtle_shell", 1d);
        ReactionPool react = pool.getPool(map);
        test(react.new Evaluator());
        ReactionPool.Evaluator ev = react.new Evaluator();
        long time = System.nanoTime();
        int step = test(ev);
        long stop = System.nanoTime();
        System.out.println("step: " + step);
        System.out.println("deviation: " + ev.deviation());
        ReactionPool.Result result = ev.toResult();
        result.map.forEach((k, v) -> System.out.println(k + ": " + v));
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
