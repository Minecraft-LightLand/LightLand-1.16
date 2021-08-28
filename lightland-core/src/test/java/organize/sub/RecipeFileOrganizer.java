package organize.sub;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lcy0x1.core.util.Serializer;
import organize.ResourceOrganizer;
import organize.json.JsonPart;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeFileOrganizer extends ResourceOrganizer {

    public RecipeFileOrganizer() {
        super(Type.DATA, "recipes", "recipes/");
    }

    private Map<String, List<JsonPart>> parts;

    @Override
    public void organize(File f) throws Exception {
        parts = Maps.newLinkedHashMap();
        addParts(new File(f.getPath() + "/-parts/"));
        generate(new File(f.getPath() + "/-template"));
        process("", f);
    }

    private void generate(File file) throws Exception {
        if (!file.exists())
            return;
        File info = new File(file.getPath() + "/-info.json");
        if (!info.exists())
            return;
        JsonElement elem = new JsonParser().parse(new FileReader(info));
        for (Map.Entry<String, JsonElement> layer_0 : elem.getAsJsonObject().entrySet()) {
            String temp = readFile(file.getPath() + "/" + layer_0.getKey() + ".json");
            for (Map.Entry<String, JsonElement> layer_1 : layer_0.getValue().getAsJsonObject().entrySet()) {
                String name = layer_1.getKey();
                File dst = new File(getTargetFolder() + name + ".json");
                check(dst);
                String ans = temp;
                for (Map.Entry<String, JsonElement> layer_2 : layer_1.getValue().getAsJsonObject().entrySet()) {
                    ans = ans.replaceAll("\\^" + layer_2.getKey(), layer_2.getValue().getAsString());
                }
                PrintStream ps = new PrintStream(dst);
                ps.println(ans);
                ps.close();
            }
        }
    }

    private void addParts(File f) throws Exception {
        if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
                String file = fi.getName();
                if (file.startsWith("-") || file.startsWith("."))
                    continue;
                addParts(fi);
            }
            return;
        }
        JsonElement je = new JsonParser().parse(new FileReader(f));
        JsonPart part = Serializer.from(je, JsonPart.class, null);
        List<JsonPart> list = parts.getOrDefault(part.file, new ArrayList<>());
        list.add(part);
        parts.put(part.file, list);
    }

    private void process(String prefix, File f) throws Exception {
        String filename = f.getName();
        if (filename.startsWith("-") || filename.startsWith("."))
            return;
        filename = f.isDirectory() ? filename : filename.split("\\.")[0];
        String name = filename.startsWith("_") ? prefix + filename : filename.endsWith("_") ? filename + prefix : filename;
        if (f.isDirectory()) {
            for (File fi : f.listFiles()) {
                String file = fi.getName();
                if (file.startsWith("-") || file.startsWith("."))
                    continue;
                process(name, fi);
            }
            return;
        }
        String fs = getTargetFolder() + name;
        File ti = new File(fs + ".json");
        check(ti);
        if (parts.containsKey(name)) {
            JsonElement je = new JsonParser().parse(new FileReader(f));
            parts.get(name).forEach(e -> e.add(je));
            PrintStream ps = new PrintStream(ti);
            ps.println(je.toString());
            ps.close();
        } else
            Files.copy(f, ti);
    }


}
