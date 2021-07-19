package organize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class ResourceOrganizer {

    public enum Type {
        ASSETS("assets"), DATA("data");

        public final String side;

        Type(String side) {
            this.side = side;
        }
    }

    public static final Map<String, ResourceOrganizer> MAP = new HashMap<>();

    public static String MODID;

    public static void main(String[] args) throws Exception {
        delete(new File("./src/main/resources/assets/"));
        new LangFileOrganizer();
        new ItemFileOrganizer();
        File f = new File("./src/test/resources");
        for (File fi : f.listFiles()) {
            MODID = fi.getName();
            if (!fi.isDirectory())
                continue;
            for (File fj : fi.listFiles()) {
                if (!fj.isDirectory())
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

    public final Type type;
    public final String folder;
    public final String target;

    public ResourceOrganizer(Type type, String folder, String target) {
        this.type = type;
        this.folder = folder;
        this.target = target;
        MAP.put(folder, this);
    }

    public abstract void organize(File f) throws Exception;

    public final String getTargetFolder() {
        return "./src/main/resources/" + type.side + "/" + MODID + "/" + target;
    }

}
