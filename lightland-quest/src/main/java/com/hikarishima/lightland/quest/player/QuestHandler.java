package com.hikarishima.lightland.quest.player;


import com.google.common.collect.Maps;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.quest.dialog.DialogHolder;
import com.hikarishima.lightland.quest.dialog.DialogSelector;
import com.hikarishima.lightland.quest.quest.QuestScene;
import com.hikarishima.lightland.quest.token.QuestToken;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SerialClass
public class QuestHandler {

    public static final Storage STORAGE = new Storage();

    @CapabilityInject(QuestHandler.class)
    public static Capability<QuestHandler> CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(QuestHandler.class, STORAGE, QuestHandler::new);
    }

    public static QuestHandler get(PlayerEntity player) {
        return (QuestHandler) player.getCapability(CAPABILITY).cast().resolve().get();
    }

    private static CompoundNBT revive_cache;

    @OnlyIn(Dist.CLIENT)
    public static void cacheSet(CompoundNBT tag, boolean force) {
        ClientPlayerEntity pl = Proxy.getClientPlayer();
        if (!force && pl != null && pl.getCapability(CAPABILITY).cast().resolve().isPresent())
            ExceptionHandler.run(() -> Automator.fromTag(tag, QuestHandler.class, get(pl), f -> true));
        else revive_cache = tag;
    }

    @OnlyIn(Dist.CLIENT)
    public static CompoundNBT getCache(PlayerEntity pl) {
        CompoundNBT tag = revive_cache;
        revive_cache = null;
        if (tag == null)
            tag = Automator.toTag(new CompoundNBT(), get(pl));
        return tag;
    }

    public World world;
    public PlayerEntity player;

    @SerialClass.SerialField(generic = {String.class, QuestToken.class})
    private final Map<String, QuestToken> tokens = Maps.newLinkedHashMap();

    @SerialClass.SerialField(generic = {String.class, PlayerProgress.class})
    private final Map<String, PlayerProgress> progress = Maps.newLinkedHashMap();

    @SerialClass.SerialField(generic = {String.class, String.class})
    private final Map<String, String> quest_dialog = Maps.newLinkedHashMap();

    public void init() {

    }

    public void startQuest(String key) {
        if (!progress.containsKey(key)) {
            PlayerProgress prog = QuestScene.generate(player, this, key);
            progress.put(key, prog);
            prog.init();
        }
    }

    public PlayerProgress getProgress(String key) {
        startQuest(key);
        PlayerProgress ans = progress.get(key);
        ans.player = player;
        ans.handler = this;
        if (ans.scene == null)
            ans.scene = QuestScene.get(player.level, key);
        return ans;
    }

    public void giveToken(String key, QuestToken token) {
        if (token == null) {
            tokens.remove(key);
            return;
        }
        QuestToken old = tokens.put(key, token);
        if (old != null)
            LogManager.getLogger().error("old token " + old + " is not removed properly before setting " + token);
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestToken> T getToken(String key) {
        QuestToken token = tokens.get(key);
        token.progress = getProgress(key);
        return (T) token;
    }

    public DialogHolder getDialog(String name) {
        String dialog = quest_dialog.get(name);
        DialogSelector sel = DialogSelector.getDialogSelector(player.level, dialog);
        if (sel == null)
            return null;
        return new DialogHolder(sel, player.level);
    }

    @SuppressWarnings("unchecked")
    public <T extends QuestToken> Collection<T> getTokens(Class<T> cls) {
        Set<T> ans = new HashSet<>();
        for (String str : tokens.keySet()) {
            QuestToken token = getToken(str);
            if (cls.isInstance(token))
                ans.add((T) token);
        }
        return ans;
    }

    public void reset(String id) {
        if (id == null || id.length() == 0) {
            quest_dialog.clear();
            progress.clear();
            tokens.clear();
        } else {
            if (progress.containsKey(id)) {
                PlayerProgress prog = getProgress(id);
                tokens.remove(id);
                for (String npc : prog.scene.npc_lock)
                    quest_dialog.remove(npc);
                progress.remove(id);
            }
        }
    }

    public void setDialog(PlayerProgress progress, String npc, String selector) {
        quest_dialog.put(npc, selector);//TODO NPC conflict
    }

    public static class Storage implements Capability.IStorage<QuestHandler> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<QuestHandler> capability, QuestHandler obj, Direction direction) {
            return Automator.toTag(new CompoundNBT(), obj);
        }

        @Override
        public void readNBT(Capability<QuestHandler> capability, QuestHandler obj, Direction direction, INBT inbt) {
            ExceptionHandler.get(() -> Automator.fromTag((CompoundNBT) inbt, QuestHandler.class, obj, f -> true));
        }

    }

}
