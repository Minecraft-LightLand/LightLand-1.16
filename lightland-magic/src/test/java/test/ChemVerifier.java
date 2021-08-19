package test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lcy0x1.core.chem.Equation;
import com.lcy0x1.core.chem.EquationPool;
import com.lcy0x1.core.util.Serializer;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;

public class ChemVerifier {

    public static void main(String[] args) throws Exception {
        File f = new File("./src/main/resources/data/lightland/recipes/config_chemistry.json");
        JsonElement elem = new JsonParser().parse(new FileReader(f));
        JsonObject obj = elem.getAsJsonObject().get("map").getAsJsonObject().get("pool").getAsJsonObject();
        obj.remove("_class");
        EquationPool pool = Serializer.from(obj, EquationPool.class, null);
        for (Equation e : pool.getEquations()) {
            for (String s : e.in) {
                if (!pool.getObjects().containsKey(s))
                    LogManager.getLogger().error(s + " not found");
            }
            for (String s : e.result) {
                if (!pool.getObjects().containsKey(s))
                    LogManager.getLogger().error(s + " not found");
            }
        }

    }

}


