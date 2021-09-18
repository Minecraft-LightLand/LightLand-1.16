package com.hikarishima.lightland.magic.compat.ars;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.Serializer;
import net.minecraft.nbt.StringNBT;

public class ArsInit {

    public static void init() {
        new Serializer.StringClassHandler<>(AbstractSpellPart.class, (a) -> ArsNouveauAPI.getInstance().getSpell_map().get(a), (b) -> b.name);
        new Automator.ClassHandler<>(AbstractSpellPart.class, (a) -> ArsNouveauAPI.getInstance().getSpell_map().get(a.getAsString()), (b) -> StringNBT.valueOf(b.name));
    }

}
