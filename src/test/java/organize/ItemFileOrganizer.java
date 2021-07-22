package organize;

import com.google.common.io.Files;

import java.io.File;

public class ItemFileOrganizer extends ResourceOrganizer {

    public String texture, model, IM, IM_B;

    public ItemFileOrganizer() {
        super(Type.ASSETS, "items", "");
    }


    @Override
    public void organize(File f) throws Exception {
        texture = getTargetFolder() + "textures/item/";
        model = getTargetFolder() + "models/item/";
        IM = readFile(f.getPath() + "/-models/-template/-.json");
        IM_B = readFile(f.getPath() + "/-models/-template/-block.json");
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
        write(model + name + ".json", IM.replaceAll("\\^s", name));
    }


}
