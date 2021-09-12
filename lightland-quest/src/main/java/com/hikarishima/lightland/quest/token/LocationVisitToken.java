package com.hikarishima.lightland.quest.token;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.quest.quest.LocationVisitStage;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

@SerialClass
public class LocationVisitToken extends QuestToken {

    public void visit(Vector3d position) {
        LocationVisitStage stage = progress.getStage();
        if (position.distanceToSqr(stage.x, stage.y, stage.z) < stage.r * stage.r) {
            progress.proceed();
        }
    }

    @Override
    public ITextComponent getQuestProgressText() {
        LocationVisitStage stage = progress.getStage();
        double dist = Math.sqrt(progress.player.getEyePosition(1f).distanceToSqr(stage.x, stage.y, stage.z) - stage.r * stage.r);
        String str = getDescription();
        Map<String, String> map = StringSubstitution.get("distance", Math.round(dist) + " m");
        str = StringSubstitution.format(str, map);
        return new StringTextComponent(str);
    }
}
