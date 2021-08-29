package organize.sub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import organize.ResourceOrganizer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LangFileOrganizer extends ResourceOrganizer {

    public LangFileOrganizer() {
        super(Type.ASSETS, "lang", "lang/");
    }

    @Override
    public void organize(File f) throws Exception {
        for (File fi : f.listFiles()) {
            if (!fi.isDirectory())
                continue;
            String name = fi.getName();
            File target = new File(getTargetFolder() + name + ".json");
            check(target);

            JsonElement root_json = new JsonParser().parse(new FileReader(fi.getPath() + "/main.json"));
            JsonObject dst_json = new JsonObject();
            for (File fj : fi.listFiles()) {
                if (fj.getName().startsWith("."))
                    continue;
                JsonObject part = new JsonParser().parse(new FileReader(fj)).getAsJsonObject();
                if (part.has("simple")) {
                    JsonObject block_list = part.get("simple").getAsJsonObject();
                    block_list.entrySet().forEach(ent0 -> {
                        JsonObject block = ent0.getValue().getAsJsonObject();
                        List<JsonObject> path = getPath(block, root_json);
                        block.entrySet().forEach(ent1 -> {
                            if (ent1.getKey().startsWith("-"))
                                return;
                            for (JsonObject po : path) {
                                po.add(ent1.getKey(), ent1.getValue());
                            }
                        });
                    });
                }
                if (part.has("cartesian")) {
                    JsonObject block_list = part.get("cartesian").getAsJsonObject();
                    block_list.entrySet().forEach(ent0 -> {
                        JsonObject block = ent0.getValue().getAsJsonObject();
                        List<JsonObject> path = getPath(block, root_json);
                        List<Pair<String, String>> map = new ArrayList<>();
                        for (JsonElement vector : block.get("list").getAsJsonArray()) {
                            if (map.isEmpty()) {
                                List<Pair<String, String>> finalMap = map;
                                vector.getAsJsonObject().entrySet().forEach(ent1 ->
                                        finalMap.add(Pair.of(ent1.getKey(), ent1.getValue().getAsString())));
                            } else {
                                map = map.stream().flatMap(ent1 -> vector.getAsJsonObject().entrySet().stream()
                                        .map(ent2 -> Pair.of(ent1.getFirst() + "_" + ent2.getKey(),
                                                ent1.getSecond() + ent2.getValue().getAsString())))
                                        .collect(Collectors.toList());
                            }
                        }
                        for (JsonObject po : path) {
                            for (Pair<String, String> pair : map) {
                                po.addProperty(pair.getFirst(), pair.getSecond());
                            }
                        }
                    });
                }
            }
            inject("", root_json.getAsJsonObject(), dst_json);
            FileWriter w = new FileWriter(target);
            w.write(dst_json.toString());
            w.close();
        }
    }

    private List<JsonObject> getPath(JsonObject jo, JsonElement e) {
        List<JsonObject> path = new ArrayList<>();
        for (JsonElement se : jo.get("-path").getAsJsonArray()) {
            String p = se.getAsString();
            JsonObject cur = e.getAsJsonObject();
            for (String str : p.split("\\.")) {
                cur = cur.get(str).getAsJsonObject();
            }
            path.add(cur);
        }
        return path;
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
