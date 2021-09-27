package com.hikarishima.lightland.magic.registry.entity.golem;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlchemyGolemEntity extends GolemEntity implements IEntityAdditionalSpawnData {

    @SerialClass.SerialField(generic = String.class)
    public List<String> materials = new ArrayList<>();
    @SerialClass.SerialField
    public UUID owner;

    public AlchemyGolemEntity(EntityType<? extends GolemEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes(double hp, double speed, double kb, double atk, double def) {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, hp)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.KNOCKBACK_RESISTANCE, kb)
                .add(Attributes.ATTACK_DAMAGE, atk)
                .add(Attributes.ARMOR, def);
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(PlayerEntity player, List<String> list) {
        owner = player.getUUID();
        List<GolemMaterial> mats = new ArrayList<>();
        for (String str : list) {
            GolemMaterial mat = ConfigRecipe.getObject(level, MagicRecipeRegistry.GOLEM, str);
            if (mat != null)
                mats.add(mat);
        }
        GolemMaterial.onAddAttribute(this, mats);
        materials = list;
    }

    @Nullable
    public PlayerEntity getOwner() {
        return level.getPlayerByUUID(owner);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, PlayerEntity.class, AlchemyGolemEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class,
                16, false, false,
                (e) -> getOwner() != null && e instanceof MobEntity && ((MobEntity) e).getTarget() == getOwner()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class,
                16, false, false,
                (e) -> getOwner() != null && e.getLastHurtByMob() == getOwner()));
    }


    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    //--- BaseEntity ---

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("auto-serial"))
            return;
        ExceptionHandler.run(() -> Automator.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, f -> true));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.put("auto-serial", Automator.toTag(new CompoundNBT(), this));
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        Serializer.to(buffer, this);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void readSpawnData(PacketBuffer data) {
        Serializer.from(data, (Class) this.getClass(), this);
    }

}
