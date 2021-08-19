package organize.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import organize.ResourceOrganizer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

public class LangFileOrganizer extends ResourceOrganizer {

    public LangFileOrganizer() {
        super(Type.ASSETS, "lang", "lang/");
    }

    @Override
    public void organize(File f) throws Exception {
        for (File fi : f.listFiles()) {
            String name = fi.getName();
            File target = new File(getTargetFolder() + name);
            check(target);
            JsonElement e = new JsonParser().parse(new FileReader(fi));
            JsonObject obj = new JsonObject();
            inject("", e.getAsJsonObject(), obj);
            FileWriter w = new FileWriter(target);
            w.write(obj.toString());
            w.close();
        }
    }

    private void inject(String path, JsonObject src, JsonObject dst) {
        for (Map.Entry<String, JsonElement> ent : src.entrySet()) {
            if (ent.getValue().isJsonObject()) {
                inject(path + ent.getKey() + ".", ent.getValue().getAsJsonObject(), dst);
            } else {
                dst.add(path + ent.getKey(), ent.getValue());
            }
        }
    }
}
