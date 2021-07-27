package com.hikarishima.lightland.magic.capabilities;

import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

@SerialClass
public class MagicHandler {

    public static final Storage STORAGE = new Storage();

    @CapabilityInject(MagicHandler.class)
    public static Capability<MagicHandler> CAPABILITY = null;

    @SerialClass.SerialField
    public State state = State.PREINJECT;
    @SerialClass.SerialField
    public AbilityPoints abilityPoints = new AbilityPoints(this);
    @SerialClass.SerialField
    public MagicAbility magicAbility = new MagicAbility(this);
    @SerialClass.SerialField
    public MagicHolder magicHolder = new MagicHolder(this);
    public PlayerEntity player;
    public World world;

    public static void register() {
        CapabilityManager.INSTANCE.register(MagicHandler.class, STORAGE, MagicHandler::new);
    }

    public static MagicHandler get(PlayerEntity e) {
        return e.getCapability(CAPABILITY).resolve().get().check();
    }

    public void tick() {
        magicAbility.tick();
    }

    public void reset() {
        state = State.PREINJECT;
        abilityPoints = new AbilityPoints(this);
        magicAbility = new MagicAbility(this);
        magicHolder = new MagicHolder(this);
    }

    protected void init() {
        if (state == null) {
            reset();
        }
        if (state != State.ACTIVE) {
            state = State.ACTIVE;
        }
        magicHolder.product_manager = new NBTObj(magicHolder.products);
        magicAbility.arcane_manager = new NBTObj(magicAbility.arcane_type);
        magicHolder.checkUnlocks();
    }

    protected MagicHandler check() {
        if (state != State.ACTIVE)
            init();
        return this;
    }

    @SerialClass.OnInject
    public void onInject() {
        if (state == State.PREINJECT)
            state = State.PREINIT;
    }

    public enum State {
        PREINJECT, PREINIT, ACTIVE
    }

    public static class Storage implements Capability.IStorage<MagicHandler> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<MagicHandler> capability, MagicHandler obj, Direction direction) {
            return ExceptionHandler.get(() -> Automator.toTag(new CompoundNBT(), MagicHandler.class, obj, f -> true));
        }

        @Override
        public void readNBT(Capability<MagicHandler> capability, MagicHandler obj, Direction direction, INBT inbt) {
            ExceptionHandler.get(() -> Automator.fromTag((CompoundNBT) inbt, MagicHandler.class, obj, f -> true));
        }

    }


}
