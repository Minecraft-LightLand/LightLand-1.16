package test;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hikarishima.lightland.LightLand;
import com.hikarishima.lightland.quest.dialog.Dialog;
import com.hikarishima.lightland.quest.dialog.DialogSelector;
import com.hikarishima.lightland.quest.option.DialogComplete;
import com.hikarishima.lightland.quest.option.IOptionComponent;
import com.hikarishima.lightland.quest.option.Option;
import com.hikarishima.lightland.quest.option.QuestStart;
import com.hikarishima.lightland.quest.quest.DialogStage;
import com.hikarishima.lightland.quest.quest.IQuestStage;
import com.hikarishima.lightland.quest.quest.MobKillStage;
import com.hikarishima.lightland.quest.quest.QuestScene;
import com.hikarishima.lightland.quest.trigger.QuestTrigger;
import com.hikarishima.lightland.quest.trigger.QuestUnlockTrigger;
import com.hikarishima.lightland.quest.trigger.SetNPCDialogTrigger;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.hikarishima.lightland.recipe.RecipeRegistry;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class DialogVerifier {

    public static void main(String[] args) throws Exception {
        File f = new File("./src/main/resources/data/lightland/recipes/config_dialog.json");
        JsonElement elem = new JsonParser().parse(new FileReader(f));
        ConfigRecipe r = RecipeRegistry.RSM_CONFIG.get().fromJson(new ResourceLocation(LightLand.MODID, "config_dialog"), elem.getAsJsonObject());
        Map<String, Dialog> dialog_id = getID(r.map, Dialog.class);
        Map<String, DialogSelector> selector_id = getID(r.map, DialogSelector.class);
        Map<String, QuestScene> quest_id = getID(r.map, QuestScene.class);
        System.out.println("verifier start");
        for (Map.Entry<String, Dialog> ent : dialog_id.entrySet()) {
            Dialog dialog = ent.getValue();
            if (dialog.next == null || dialog.next.length == 0) {
                System.out.println("dialog " + ent.getKey() + " does not have next");
            } else {
                for (int i = 0; i < dialog.next.length; i++) {
                    Option o = dialog.next[i];
                    if (o == null) {
                        System.out.println("dialog " + ent.getKey() + " option " + i + " is null");
                    } else {
                        if (o.next != null && o.next.length() > 0 && !dialog_id.containsKey(o.next)) {
                            System.out.println("dialog " + ent.getKey() + " option " + i + " has invalid next id " + o.next);
                        }
                        if (o.components != null && o.components.length > 0) {
                            for (int j = 0; j < o.components.length; j++) {
                                IOptionComponent c = o.components[j];
                                if (c == null) {
                                    System.out.println("dialog " + ent.getKey() + " option " + i + " has invalid component at " + j);
                                } else {
                                    if (c instanceof QuestStart) {
                                        if (!quest_id.containsKey(((QuestStart) c).quest_id)) {
                                            System.out.println("dialog " + ent.getKey() + " option " + i + " component " + j + " has invalid quest id " + ((QuestStart) c).quest_id);
                                        }
                                    }
                                    if (c instanceof DialogComplete) {
                                        if (!quest_id.containsKey(((DialogComplete) c).quest_id)) {
                                            System.out.println("dialog " + ent.getKey() + " option " + i + " component " + j + " has invalid quest id " + ((DialogComplete) c).quest_id);
                                        }
                                        QuestScene scene = quest_id.get(((DialogComplete) c).quest_id);
                                        boolean check = false;
                                        for (IQuestStage stage : scene.stage_list) {
                                            if (stage instanceof DialogStage) {
                                                if (((DialogStage) stage).stage_id.equals(((DialogComplete) c).stage_id)) {
                                                    check = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!check) {
                                            System.out.println("dialog " + ent.getKey() + " option " + i + " component " + j + " has invalid stage id " + ((DialogComplete) c).stage_id);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, DialogSelector> ent : selector_id.entrySet()) {
            DialogSelector selector = ent.getValue();
            if (selector.dialog_list == null || selector.dialog_list.length == 0) {
                System.out.println("dialog selector " + ent.getKey() + " does not have dialog_list");
            } else
                for (int i = 0; i < selector.dialog_list.length; i++) {
                    DialogSelector.DialogEntry entry = selector.dialog_list[i];
                    if (entry == null) {
                        System.out.println("dialog selector " + ent.getKey() + " dialog_list[" + i + "] is null");
                    } else {
                        if (entry.weight <= 0) {
                            System.out.println("dialog selector " + ent.getKey() + " dialog_list[" + i + "] should have positive weight, not " + entry.weight);
                        }
                        if (!dialog_id.containsKey(entry.id)) {
                            System.out.println("dialog selector " + ent.getKey() + " dialog_list[" + i + "] has invalid dialog id " + entry.id);
                        }
                    }
                }
            if (selector.next_selector == null || selector.next_selector.length == 0) {
                System.out.println("dialog selector " + ent.getKey() + " does not have next_selector");
            } else
                for (int i = 0; i < selector.next_selector.length; i++) {
                    DialogSelector.NextSelector entry = selector.next_selector[i];
                    if (entry == null) {
                        System.out.println("dialog selector " + ent.getKey() + " next_selector[" + i + "] is null");
                    } else {
                        if (entry.weight <= 0) {
                            System.out.println("dialog selector " + ent.getKey() + " next_selector[" + i + "]  should have positive weight, not " + entry.weight);
                        }
                        if (entry.id != null && entry.id.length() > 0 && !selector_id.containsKey(entry.id)) {
                            System.out.println("dialog selector " + ent.getKey() + " next_selector[" + i + "] has invalid selector id " + entry.id);
                        }
                    }
                }
        }
        for (Map.Entry<String, QuestScene> ent : quest_id.entrySet()) {
            QuestScene quest = ent.getValue();
            if (quest.stage_list == null || quest.stage_list.length == 0) {
                System.out.println("quest " + ent.getKey() + " does not have a stag_list");
            } else {
                Set<String> npcs = new HashSet<>();
                Set<String> stage_ids = new HashSet<>();
                for (int i = 0; i < quest.stage_list.length; i++) {
                    IQuestStage stage = quest.stage_list[i];
                    if (stage == null) {
                        System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] is null");
                    } else {
                        if (stage.title == null || stage.title.length() == 0) {
                            System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] does not have title");
                        }
                        if (stage.description == null || stage.description.length() == 0) {
                            System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] does not have description");
                        }
                        BiConsumer<QuestTrigger[], String> cons = (ts, name) -> {
                            if (ts != null && ts.length > 0) {
                                for (int j = 0; j < ts.length; j++) {
                                    QuestTrigger trigger = ts[j];
                                    if (trigger == null) {
                                        System.out.println(name + "[" + j + "] is null");
                                    } else {
                                        if (trigger instanceof SetNPCDialogTrigger) {
                                            if (((SetNPCDialogTrigger) trigger).selector.length() > 0) {
                                                if (Arrays.stream(quest.npc_lock).noneMatch(((SetNPCDialogTrigger) trigger).npc::equals)) {
                                                    System.out.println(name + "[" + j + "] uses unlogged NPC");
                                                } else {
                                                    npcs.add(((SetNPCDialogTrigger) trigger).npc);
                                                }
                                                if (!selector_id.containsKey(((SetNPCDialogTrigger) trigger).selector)) {
                                                    System.out.println(name + "[" + j + "] has invalid selector id " + ((SetNPCDialogTrigger) trigger).selector);
                                                }
                                            } else {
                                                npcs.remove(((SetNPCDialogTrigger) trigger).npc);
                                            }
                                        } else if (trigger instanceof QuestUnlockTrigger) {
                                            if (!quest_id.containsKey(((QuestUnlockTrigger) trigger).quest_id)) {
                                                System.out.println(name + "[" + j + "] has invalid quest id " + ((QuestUnlockTrigger) trigger).quest_id);
                                            }
                                        }
                                    }
                                }
                            }
                        };
                        cons.accept(stage.start_triggers, "quest " + ent.getKey() + " stage_list[" + i + "] start_triggers");
                        cons.accept(stage.end_triggers, "quest " + ent.getKey() + " stage_list[" + i + "] end_triggers");
                        if (stage instanceof DialogStage) {
                            DialogStage dialogStage = (DialogStage) stage;
                            if (dialogStage.stage_id == null || dialogStage.stage_id.length() == 0) {
                                System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] does not have stage_id");
                            } else {
                                if (stage_ids.contains(dialogStage.stage_id)) {
                                    System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] has repeated stage_id");
                                } else {
                                    stage_ids.add(dialogStage.stage_id);
                                }
                            }
                        } else if (stage instanceof MobKillStage) {
                            MobKillStage mob = (MobKillStage) stage;
                            if (mob.count <= 0) {
                                System.out.println("quest " + ent.getKey() + " stage_list[" + i + "] does not have positive count");
                            }
                        }
                    }
                }
                if (npcs.size() > 0) {
                    System.out.println("quest " + ent.getKey() + " has unclosed NPC " + npcs);
                }
            }
        }
        System.out.println("verifier complete");
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> getID(Map<String, ?> map, Class<T> cls) {
        return (Map<String, T>) Maps.filterEntries(map, e -> cls.isInstance(e.getValue()));
    }

}
