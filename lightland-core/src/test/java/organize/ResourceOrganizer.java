package organize;

import com.google.common.io.Files;
import organize.sub.*;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ResourceOrganizer {

    public static final Map<String, ResourceOrganizer> MAP = new HashMap<>();
    public static String MODID;
    public final Type type;
    public final String folder;
    public final String target;

    public ResourceOrganizer(Type type, String folder, String target) {
        this.type = type;
        this.folder = folder;
        this.target = target;
        MAP.put(folder, this);
    }

    public static void main(String[] args) throws Exception {
        new LangFileOrganizer();
        new ItemFileOrganizer();
        new BlockFileOrganizer();
        new ArmorFileOrganizer();
        new RecipeFileOrganizer();
        new AssetMisc();
        File f = new File("./lightland-core/src/test/resources");
        for (File fi : f.listFiles()) {
            MODID = fi.getName();
            delete(new File("./" + MODID + "/src/main/resources/assets/"));
            delete(new File("./" + MODID + "/src/main/resources/data/"));
            if (!fi.isDirectory())
                continue;
            for (File fj : fi.listFiles()) {
                if (!fj.isDirectory())
                    continue;
                if (fj.getName().equals("gui"))
                    continue;
                ResourceOrganizer obj = MAP.get(fj.getName());
                obj.organize(fj);
            }
        }
    }

    public static void delete(File f) throws Exception {
        if (f.exists()) {
            if (f.isDirectory())
                for (File fi : f.listFiles())
                    delete(fi);
            f.delete();
        }
    }

    public static void check(File f) throws Exception {
        if (f.exists()) {
            f.delete();
        }
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        f.createNewFile();
    }

    public abstract void organize(File f) throws Exception;

    public final String getTargetFolder() {
        return getResourceFolder(true) + type + "/" + MODID + "/" + target;
    }

    public final String getResourceFolder(boolean main) {
        return (main ? "./" + MODID + "/src/main/resources/" : "./lightland/src/test/resources/");
    }

    protected String readFile(String path) {
        List<String> list = null;
        try {
            list = Files.readLines(new File(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        String str = "";
        for (String s : list)
            str += s + "\n";
        return str.replaceAll("\\^m", MODID);
    }

    protected void write(String name, String cont) throws Exception {
        File f = new File(name);
        check(f);
        PrintStream ps = new PrintStream(f, "UTF-8");
        ps.println(cont);
        ps.close();
    }

    public enum Type {
        ASSETS("assets"), DATA("data");

        public final String side;

        Type(String side) {
            this.side = side;
        }

        public String toString() {
            return side;
        }
    }

}
