package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicProduct;
import com.hikarishima.lightland.magic.MagicProductType;
import com.hikarishima.lightland.recipe.IMagicRecipe;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.ExceptionHandler;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerialClass
public class MagicHandler {

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

    public static final Storage STORAGE = new Storage();

    @CapabilityInject(MagicHandler.class)
    public static Capability<MagicHandler> CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(MagicHandler.class, STORAGE, MagicHandler::new);
    }

    public enum State {
        PREINJECT, PREINIT, ACTIVE
    }

    @SerialClass.SerialField
    public State state = State.PREINJECT;

    @SerialClass.SerialField
    public CompoundNBT masteries = new CompoundNBT();

    @SerialClass.SerialField
    public CompoundNBT products = new CompoundNBT();

    public World world;

    private NBTObj manager;

    private final Map<MagicProductType<?, ?>, Map<ResourceLocation, MagicProduct<?, ?>>> product_cache = new HashMap<>();
    private final Map<ResourceLocation, IMagicRecipe<?>> recipe_cache = new HashMap<>();

    public int getElementalMastery(MagicElement elem) {
        return masteries.getInt(elem.getRegistryName().toString());
    }

    public void setElementalMastery(World w, MagicElement elem, int lv) {
        if (lv <= getElementalMastery(elem))
            return;
        masteries.putInt(elem.getRegistryName().toString(), lv);
        checkUnlocks();
    }

    public void init(World world) {
        this.world = world;
        if (state == State.PREINIT) {
            state = State.ACTIVE;
        }
        checkUnlocks();
    }

    private void checkUnlocks() {
        List<IMagicRecipe<?>> list = IMagicRecipe.getAll(world);
        for (IMagicRecipe<?> r : list) {
            recipe_cache.put(r.id, r);
        }
        for (IMagicRecipe<?> r : list) {
            if (isUnlocked(r))
                getProduct(r).setUnlock();
        }
    }

    private boolean isUnlocked(IMagicRecipe<?> r) {
        for (IMagicRecipe.ElementalMastery elem : r.elemental_mastery)
            if (getElementalMastery(elem.element) < elem.level)
                return false;
        for (ResourceLocation rl : r.predecessor) {
            MagicProduct<?, ?> prod = getProduct(recipe_cache.get(rl));
            if (prod != null && !prod.usable())
                return false;
        }
        return true;
    }

    public IMagicRecipe<?> getRecipe(ResourceLocation rl) {
        return recipe_cache.get(rl);
    }

    private MagicProduct<?, ?> getProduct(IMagicRecipe<?> r) {
        if (r == null)
            return null;
        MagicProductType<?, ?> type = r.product_type.getAsType();
        Map<ResourceLocation, MagicProduct<?, ?>> submap;
        if (!product_cache.containsKey(type))
            product_cache.put(type, submap = new HashMap<>());
        else submap = product_cache.get(type);
        if (submap.containsKey(r.product_id))
            return submap.get(r.product_id);
        NBTObj nbt = manager.getSub(type.getRegistryName().toString()).getSub(r.product_id.toString());
        MagicProduct<?, ?> ans = type.fac.get(this, nbt, r.product_id, r);
        submap.put(r.product_id, ans);
        return ans;
    }

    @SerialClass.OnInject
    public void onInject() {
        if (state == State.PREINJECT)
            state = State.PREINIT;
        manager = new NBTObj(products);
    }


}