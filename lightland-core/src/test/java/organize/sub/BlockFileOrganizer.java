package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class BlockFileOrganizer extends ResourceOrganizer {


    private String texture, model, state, loot;
    private String BM, BS, BL, BS_H;

    public BlockFileOrganizer() {
        super(null, "blocks", null);
    }

    private void init(File f) {
        texture = getResourceFolder(true) + Type.ASSETS + "/" + MODID + "/textures/block/";
        model = getResourceFolder(true) + Type.ASSETS + "/" + MODID + "/models/block/";
        state = getResourceFolder(true) + Type.ASSETS + "/" + MODID + "/blockstates/";
        loot = getResourceFolder(true) + Type.DATA + "/" + MODID + "/loot_tables/blocks/";

        BS = readFile(f.getPath() + "/-blockstate/-.json");
        BS_H = readFile(f.getPath() + "/-blockstate/-horizontal.json");
        BM = readFile(f.getPath() + "/-model/-.json");
        BL = readFile(f.getPath() + "/-loot_tables/-.json");
    }

    @Override
    public void organize(File f) throws Exception {
        init(f);
        org(new File(f.getPath() + "/default"), true);
        org(new File(f.getPath() + "/no_drop"), false);

        File special = new File(f.getPath() + "/-special");
        if (special.exists()) {
            special(f);
        }
    }

    private void special(File f) throws Exception {
        File models = new File(f.getPath() + "/-special/model");
        File model_h = new File(f.getPath() + "/-special/model_horizontal");
        File textures = new File(f.getPath() + "/-special/textures");
        for (File fi : textures.listFiles()) {
            File ti = new File(texture + fi.getName());
            check(ti);
            Files.copy(fi, ti);
        }
        for (File fi : models.listFiles()) {
            File ti = new File(model + fi.getName());
            check(ti);
            Files.copy(fi, ti);
            String name = fi.getName().split("\\.")[0];
            write(state + name + ".json", BS.replaceAll("\\^s", name));
            write(loot + name + ".json", BL.replaceAll("\\^s", name));
            ((ItemFileOrganizer) MAP.get("items")).createBlock(name);
        }
        for (File fi : model_h.listFiles()) {
            File ti = new File(model + fi.getName());
            check(ti);
            Files.copy(fi, ti);
            String name = fi.getName().split("\\.")[0];
            write(state + name + ".json", BS_H.replaceAll("\\^s", name));
            write(loot + name + ".json", BL.replaceAll("\\^s", name));
            ((ItemFileOrganizer) MAP.get("items")).createBlock(name);
        }
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
            if (drop) {
                write(loot + name + ".json", BL.replaceAll("\\^s", name));
                ((ItemFileOrganizer) MAP.get("items")).createBlock(name);
            }
        }
    }


}
