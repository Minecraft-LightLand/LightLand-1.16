package com.hikarishima.lightland.quest.token;

import com.hikarishima.lightland.config.StringSubstitution;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.quest.player.QuestToClient;
import com.hikarishima.lightland.quest.quest.MobKillStage;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@SerialClass
public class MobKillToken extends QuestToken {

    @SerialClass.SerialField
    public int count = 0;

    public void onKill(Entity entity) {
        MobKillStage stage = progress.getStage();
        if (stage.test(entity)) {
            onKill();
        }
    }

    public void onKill() {
        if (!progress.player.level.isClientSide()) {
            PacketHandler.toClient((ServerPlayerEntity) progress.player, QuestToClient.onKill(quest_id));
        }
        count++;
        MobKillStage stage = progress.getStage();
        if (count >= stage.count)
            progress.proceed();
    }

    @Override
    public ITextComponent getQuestProgressText() {
        MobKillStage stage = progress.getStage();
        String str = getDescription();
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(stage.entity);
        String name = StringSubstitution.toString(type.getDescription());
        Map<String, String> map = StringSubstitution.get("entity", name, "count", "" + count, "total", "" + stage.count);
        return new StringTextComponent(StringSubstitution.format(str, map));
    }
}
