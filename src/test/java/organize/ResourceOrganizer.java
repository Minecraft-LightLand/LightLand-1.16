package organize;

import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
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
        new LangFileOrganizer();
        File f = new File("./src/test/resources");
        for (File fi : f.listFiles()) {
            MODID = fi.getName();
            for (File fj : fi.listFiles()) {
                ResourceOrganizer obj = MAP.get(fj.getName());
                obj.organize(fj);
            }
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
