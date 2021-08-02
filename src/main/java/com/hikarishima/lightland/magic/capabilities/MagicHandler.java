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
import java.util.function.Consumer;

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

    public void reset(Reset reset) {
        reset.cons.accept(this);
    }

    protected void init() {
        if (state == null) {
            reset(Reset.FOR_INJECT);
        }
        if (state != State.ACTIVE) {
            state = State.ACTIVE;
        }
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

    public enum Reset {
        ABILITY((h) -> {
            h.magicAbility = new MagicAbility(h);
            h.abilityPoints = new AbilityPoints(h);
        }), HOLDER((h)->{
            h.magicHolder = new MagicHolder(h);
            h.magicHolder.checkUnlocks();
        }),ALL((h) -> {
            ABILITY.cons.accept(h);
            HOLDER.cons.accept(h);
        }), FOR_INJECT((h) -> {
            h.state = State.PREINJECT;
            h.magicAbility = new MagicAbility(h);
            h.abilityPoints = new AbilityPoints(h);
            h.magicHolder = new MagicHolder(h);
        });

        final Consumer<MagicHandler> cons;

        Reset(Consumer<MagicHandler> cons) {
            this.cons = cons;
        }
    }

    public static class Storage implements Capability.IStorage<MagicHandler> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<MagicHandler> capability, MagicHandler obj, Direction direction) {
            return Automator.toTag(new CompoundNBT(), obj);
        }

        @Override
        public void readNBT(Capability<MagicHandler> capability, MagicHandler obj, Direction direction, INBT inbt) {
            ExceptionHandler.get(() -> Automator.fromTag((CompoundNBT) inbt, MagicHandler.class, obj, f -> true));
        }

    }

}
