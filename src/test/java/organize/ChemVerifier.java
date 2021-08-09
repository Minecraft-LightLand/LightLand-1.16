package organize;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import com.lcy0x1.core.chem.Equation;
import com.lcy0x1.core.chem.EquationPool;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;

public class ChemVerifier {

    public static void main(String[] args) throws Exception {
        File f = new File("./src/test/resources/lightland/recipes/config/_chemistry.json");
        JsonElement elem = new JsonParser().parse(new FileReader(f));
        ConfigRecipe r = RecipeRegistry.RSM_CONFIG.fromJson(new ResourceLocation(LightLand.MODID, "config_chemistry"), elem.getAsJsonObject());
        EquationPool pool = r.get("pool");
        for (Equation e : pool.equations) {
            for (String s : e.in) {
                if (!pool.objects.containsKey(s))
                    LogManager.getLogger().error(s + " not found");
            }
            for (String s : e.result) {
                if (!pool.objects.containsKey(s))
                    LogManager.getLogger().error(s + " not found");
            }
        }

    }

}


