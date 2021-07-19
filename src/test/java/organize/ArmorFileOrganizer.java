package organize;

import com.google.common.io.Files;

import java.io.File;

public class ArmorFileOrganizer extends ResourceOrganizer {

    public ArmorFileOrganizer() {
        super(Type.ASSETS, "armor", "textures/models/armor/");
    }

    @Override
    public void organize(File f) throws Exception {
        for (File fi : f.listFiles()) {
            File ti = new File(getTargetFolder() + fi.getName());
            check(ti);
            Files.copy(fi, ti);
        }
    }
}
