package organize;

import com.google.common.io.Files;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

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
        System.out.println("file: " + name + " from " + f.getPath());
        File ti = new File(texture + name + ".png");
        check(ti);
        Files.copy(f, ti);
        write(model + name + ".json", IM.replaceAll("\\^s", name));
    }

    private String readFile(String path) {
        List<String> list = null;
        try {
            list = Files.readLines(new File(path), Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String str = "";
        for (String s : list)
            str += s + "\n";
        return str.replaceAll("\\^m", MODID);
    }

    private void write(String name, String cont) throws Exception {
        File f = new File(name);
        check(f);
        PrintStream ps = new PrintStream(f);
        ps.println(cont);
        ps.close();
    }


}
