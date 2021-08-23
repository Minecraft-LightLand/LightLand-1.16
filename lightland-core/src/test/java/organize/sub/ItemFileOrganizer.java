package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class ItemFileOrganizer extends ResourceOrganizer {

    public String texture, model, IM, IM_B, IM_H;

    public ItemFileOrganizer() {
        super(Type.ASSETS, "items", "");
    }


    @Override
    public void organize(File f) throws Exception {
        texture = getTargetFolder() + "textures/item/";
        model = getTargetFolder() + "models/item/";
        IM = readFile(f.getPath() + "/-models/-template/-.json");
        IM_B = readFile(f.getPath() + "/-models/-template/-block.json");
        IM_H = readFile(f.getPath() + "/-models/-template/-handheld.json");
        process("", f);
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
        File ti = new File(texture + name + ".png");
        check(ti);
        Files.copy(f, ti);
        String template = getTemplate(name);
        write(model + name + ".json", template.replaceAll("\\^s", name));
    }

    private String getTemplate(String name) {
        if (name.endsWith("axe") || name.endsWith("sword") || name.endsWith("wand"))
            return IM_H;
        return IM;
    }


}
