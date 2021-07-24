package organize.sub;

import com.google.common.io.Files;
import organize.ResourceOrganizer;

import java.io.File;

public class AssetMisc extends ResourceOrganizer {

    public AssetMisc() {
        super(Type.ASSETS, "assets", "");
    }

    @Override
    public void organize(File f) throws Exception {
        for (File fi : f.listFiles())
            process(fi, getTargetFolder());
    }

    private void process(File f, String pre) throws Exception {
        if (f.getName().startsWith("."))
            return;
        if (f.isDirectory()) {
            for (File fi : f.listFiles())
                process(fi, pre + f.getName() + "/");
        } else {
            File t = new File(pre + f.getName());
            check(t);
            Files.copy(f, t);
        }
    }
}
