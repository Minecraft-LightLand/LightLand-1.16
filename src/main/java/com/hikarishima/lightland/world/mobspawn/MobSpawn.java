package com.hikarishima.lightland.world.mobspawn;

import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.MobSpawnInfo;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class MobSpawn {

    public static final List<MobSpawn> LIST = new ArrayList<>();
    @SerialClass.SerialField
    public String id, name;
    @SerialClass.SerialField
    public int origin_x, origin_y;
    @SerialClass.SerialField
    public double center_density, density_scale, safe_threshold, difficulty_cap, base_difficulty, difficulty_scale;
    @SerialClass.SerialField
    public Entry[] mobs;

    public MobSpawn() {
    }

    public static void init() {
        IMobLevel.readFile(MobSpawn.class, LIST, "spawn_rules.json");
        IMobLevel.readFile(EquipLevel.EquipItem.class, EquipLevel.ITEMS, "item_cost.json");
        IMobLevel.readFile(EquipLevel.Enchant.class, EquipLevel.ENCHANTS, "enchant_cost.json");
        IMobLevel.readFile(BuffLevel.Buff.class, BuffLevel.LIST, "buff_cost.json");
        IMobLevel.readFile(PotionLevel.PotionEntry.class, PotionLevel.LIST, "potion_cost.json");
    }

    private static MobSpawn getSpawner(IWorld world, int x, int y) {
        MobSpawn winner = null;
        double max_density = 0;
        for (MobSpawn spawn : MobSpawn.LIST) {
            double density = spawn.getDensity(x, y);
            if (density > max_density) {
                winner = spawn;
                max_density = density;
            }
        }
        return winner;
    }

    public static void fillSpawnList(IWorld world, List<MobSpawnInfo.Spawners> list, BlockPos pos) {
        MobSpawn winner = getSpawner(world, pos.getX(), pos.getY());
        if (winner != null) {
            double difficulty = winner.getDifficulty(pos.getX(), pos.getY());
            if (difficulty > 0)
                for (MobSpawn.Entry entry : winner.getMobs())
                    if (entry.type != null)
                        list.add(entry.spawner);
        }
    }

    public static void addAllSpawns(List<MobSpawnInfo.Spawners> list) {
        for (MobSpawn rule : LIST)
            for (MobSpawn.Entry entry : rule.getMobs())
                list.add(entry.spawner);
    }

    public static boolean modifySpawnedEntity(IWorld world, LivingEntity ent) {
        int x = (int) ent.getX();
        int y = (int) ent.getY();
        MobSpawn spawner = getSpawner(world, x, y);
        if (spawner == null)
            return true;
        double difficulty = spawner.getDifficulty(x, y);
        return IMobLevel.apply(world, ent, difficulty);
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDensity(int x, int y) {
        int dx = origin_x - x;
        int dy = origin_y - y;
        return center_density - density_scale * Math.sqrt(dx * dx + dy * dy);
    }

    public double getDifficulty(int x, int y) {
        int dx = origin_x - x;
        int dy = origin_y - y;
        double dis = Math.sqrt(dx * dx + dy * dy);
        if (dis < safe_threshold)
            return 0;
        double diff = base_difficulty + (dis - safe_threshold) * difficulty_scale;
        if (diff > difficulty_cap)
            return difficulty_cap;
        return diff;
    }

    public Entry[] getMobs() {
        return mobs;
    }

    @SerialClass
    public static class Entry {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int weight, min, max;

        public EntityType<?> type;

        public MobSpawnInfo.Spawners spawner;

        @SerialClass.OnInject
        public void onInject() {
            type = EntityType.byString(id).orElse(null);
            if (type == null)
                LogManager.getLogger().warn("entity type [" + id + "] not present");
            else {
                spawner = new MobSpawnInfo.Spawners(type, weight, min, max);
            }
        }

    }
}
