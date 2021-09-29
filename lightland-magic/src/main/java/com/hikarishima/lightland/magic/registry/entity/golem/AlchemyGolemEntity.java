package com.hikarishima.lightland.magic.registry.entity.golem;

import com.hikarishima.lightland.magic.recipe.MagicRecipeRegistry;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.SerialClass;
import com.lcy0x1.core.util.Serializer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SerialClass
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlchemyGolemEntity extends GolemEntity implements IEntityAdditionalSpawnData {

    @SerialClass.SerialField(generic = String.class)
    public ArrayList<String> materials = new ArrayList<>();
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

    public GolemMaterial getMerged() {
        return new GolemMaterial(getMaterials().stream().map(e -> (GolemMaterial) ConfigRecipe.getObject(level, MagicRecipeRegistry.GOLEM, e)).collect(Collectors.toList()));
    }

    @Override
    public boolean canAttack(LivingEntity le) {
        if (isAlliedTo(le) || le.isAlliedTo(this)) {
            return false;
        }
        PlayerEntity owner = getOwner();
        if (owner != null) {
            if (le == owner || le.isAlliedTo(owner) || owner.isAlliedTo(le))
                return false;
            if (le instanceof AlchemyGolemEntity && ((AlchemyGolemEntity) le).getOwner() == owner)
                return false;
        }
        return super.canAttack(le);
    }

    @Nullable
    @Override
    public Team getTeam() {
        if (getOwner() != null) {
            return getOwner().getTeam();
        }
        return super.getTeam();
    }

    public void setMaterials(PlayerEntity player, ArrayList<String> list) {
        owner = player.getUUID();
        ArrayList<GolemMaterial> mats = new ArrayList<>();
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

    @Override
    public boolean hurt(DamageSource source, float amount) {
        GolemMaterial mat = getMerged();
        if (source.isFire()) {
            amount *= 1 - mat.fire_reduce;
            if (amount < 1e-3) {
                amount = 0;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount % 20 == 0) {
            this.setHealth((float) (this.getHealth() + getMerged().restore * 20));
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entity).getMobType());
            f1 += (float) EnchantmentHelper.getKnockbackBonus(this);
        }

        int i = EnchantmentHelper.getFireAspect(this);
        int tick = i * 4 + getMerged().fire_tick / 20;
        if (tick > 0) {
            entity.setSecondsOnFire(tick);
        }

        boolean flag = entity.hurt(getDamageSource(), f);
        if (flag) {
            if (f1 > 0.0F && entity instanceof LivingEntity) {
                ((LivingEntity) entity).knockback(f1 * 0.5F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
            }

            if (entity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity) entity;
                this.maybeDisableShield(playerentity, this.getMainHandItem(), playerentity.isUsingItem() ? playerentity.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
        }

        return flag;
    }

    protected DamageSource getDamageSource() {
        DamageSource source = DamageSource.mobAttack(this);
        GolemMaterial merged = getMerged();
        if (merged.bypass_armor > level.random.nextDouble()) {
            source = source.bypassArmor();
        }
        if (merged.bypass_magic > level.random.nextDouble()) {
            source = source.bypassMagic();
        }
        return source;
    }

    private void maybeDisableShield(PlayerEntity p_233655_1_, ItemStack p_233655_2_, ItemStack p_233655_3_) {
        if (!p_233655_2_.isEmpty() && !p_233655_3_.isEmpty() && p_233655_2_.getItem() instanceof AxeItem && p_233655_3_.getItem() == Items.SHIELD) {
            float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                p_233655_1_.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(p_233655_1_, (byte) 30);
            }
        }

    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, amount);
        LivingEntity le = source.getDirectEntity() instanceof LivingEntity ? (LivingEntity) source.getDirectEntity() : null;
        getMerged().onHit(this, le, source, amount);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, PlayerEntity.class, AlchemyGolemEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, MobEntity.class,
                16, false, false,
                (e) -> getOwner() != null && e instanceof MobEntity && ((MobEntity) e).getTarget() == getOwner()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class,
                16, false, false,
                (e) -> getOwner() != null && e.getLastHurtByMob() == getOwner()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MobEntity.class,
                16, false, false,
                (e) -> e instanceof MobEntity && ((MobEntity) e).getTarget() == this));
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
