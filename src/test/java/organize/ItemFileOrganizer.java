package organize;

import com.google.common.io.Files;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

public class ItemFileOrganizer extends ResourceOrganizer {

    public ItemFileOrganizer() {
        super(Type.ASSETS, "items", "");
    }


    @Override
    public void organize(File f) throws Exception {
        String texture = getTargetFolder() + "textures/item/";
        String model = getTargetFolder() + "models/item/";
        String IM = readFile(f.getPath() + "/models/-template/-.json");
        String IM_B = readFile(f.getPath() + "/models/-template/-block.json");

        for (File fi : f.listFiles()) {
            if (fi.isDirectory())
                continue;
            String filename = fi.getName();
            String name = filename.split("\\.")[0];
            File ti = new File(texture + name + ".png");
            check(ti);
            Files.copy(fi, ti);
            write(model + name + ".json", IM.replaceAll("\\^s", name));
        }
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
