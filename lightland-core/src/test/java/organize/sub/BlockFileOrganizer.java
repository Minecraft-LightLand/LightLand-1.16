package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class BlockFileOrganizer extends ResourceOrganizer {


    private String texture, model, state, loot;
    private String BM, BS, BL;

    public BlockFileOrganizer() {
        super(null, "blocks", null);
    }

    private void init(File f) {
        texture = "./src/main/resources/" + Type.ASSETS + "/" + MODID + "/textures/block/";
        model = "./src/main/resources/" + Type.ASSETS + "/" + MODID + "/models/block/";
        state = "./src/main/resources/" + Type.ASSETS + "/" + MODID + "/blockstates/";
        loot = "./src/main/resources/" + Type.DATA + "/" + MODID + "/loot_tables/blocks/";

        BS = readFile(f.getPath() + "/-blockstate/-.json");
        BM = readFile(f.getPath() + "/-model/-.json");
        BL = readFile(f.getPath() + "/-loot_tables/-.json");
    }

    @Override
    public void organize(File f) throws Exception {
        init(f);
        org(new File(f.getPath() + "/default"), true);
        org(new File(f.getPath() + "/no_drop"), false);
    }

    private void org(File f, boolean drop) throws Exception {
        for (File fi : f.listFiles()) {
            String filename = fi.getName();
            if (filename.startsWith("-") || filename.startsWith("."))
                continue;
            String name = filename.split("\\.")[0];
            File ti = new File(texture + name + ".png");
            check(ti);
            Files.copy(fi, ti);
            write(state + name + ".json", BS.replaceAll("\\^s", name));
            write(model + name + ".json", BM.replaceAll("\\^s", name));
            if (drop) write(loot + name + ".json", BL.replaceAll("\\^s", name));
        }
    }
}
