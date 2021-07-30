package com.hikarishima.lightland.npc.token;

import com.hikarishima.lightland.npc.quest.LocationVisitStage;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.math.vector.Vector3d;

@SerialClass
public class LocationVisitToken extends QuestToken {

    public void visit(Vector3d position) {
        LocationVisitStage stage = progress.getStage();
        if (position.distanceToSqr(stage.x, stage.y, stage.z) < stage.r * stage.r) {
            progress.proceed();
        }
    }
}
