package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.spell.internal.AbstractSpell;
import com.hikarishima.lightland.registry.item.magic.MagicScroll;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;

@SerialClass
public class MagicAbility {

    public static final int ACTIVATION = 600;

    private final MagicHandler parent;
    @SerialClass.SerialField
    public CompoundNBT arcane_type = new CompoundNBT();
    @SerialClass.SerialField
    public ListNBT spell_activation = new ListNBT();
    @SerialClass.SerialField
    public int magic_level, spell_level;
    @SerialClass.SerialField
    protected int magic_mana, spell_load;

    MagicAbility(MagicHandler parent) {
        this.parent = parent;
    }

    public void giveMana(int mana) {
        magic_mana = MathHelper.clamp(magic_mana + mana, 0, getMaxMana());
    }

    public void addSpellLoad(int load) {
        spell_load += load;
    }

    public void tick() {
        magic_mana = MathHelper.clamp(magic_mana + getManaRestoration(), 0, getMaxMana());
        spell_load = Math.max(spell_load - getSpellReduction(), 0);
        for (int i = 0; i < getMaxSpellSlot(); i++) {
            ItemStack stack = parent.player.inventory.getItem(i);
            if (spell_activation.size() == i)
                spell_activation.add(new CompoundNBT());
            CompoundNBT tag = spell_activation.getCompound(i);
            tickSpell(stack, tag);
        }
    }

    private void tickSpell(ItemStack stack, CompoundNBT tag) {
        if (stack.getItem() instanceof MagicScroll) {
            String tag_spell = tag.getString("spell");
            AbstractSpell spell = MagicScroll.getSpell(stack);
            if (spell != null) {
                if (tag_spell.equals(spell.getID())) {
                    int tick = tag.getInt("time");
                    tag.putInt("time", tick + 1);
                } else {
                    tag.putString("spell", spell.getID());
                    tag.putInt("time", 0);
                }
                return;
            }
        }
        tag.putString("spell", "");
        tag.putInt("time", 0);
    }

    public int getMaxMana() {
        return magic_level * 100;
    }

    public int getManaRestoration() {
        return magic_level;
    }

    public int getMaxSpellSlot() {
        return spell_level;
    }

    public int getMaxSpellEndurance() {
        return spell_level * 100;
    }

    public int getSpellReduction() {
        return spell_level;
    }

    public boolean isArcaneTypeUnlocked(ArcaneType type) {
        return new NBTObj(arcane_type).getSub(type.getID()).tag.getInt("level") > 0;
    }

    public void unlockArcaneType(ArcaneType type) {
        if (!isArcaneTypeUnlocked(type)) {
            new NBTObj(arcane_type).getSub(type.getID()).tag.putInt("level", 1);
            parent.abilityPoints.levelArcane();
        }
    }

    public double getSpellActivation(int id) {
        if (id < 0 || id >= getMaxSpellSlot())
            return 1;
        int time = spell_activation.getCompound(id).getInt("time");
        if (time >= ACTIVATION)
            return 0;
        return 1.0 * (ACTIVATION - time) / ACTIVATION;
    }

    public int getMana() {
        return magic_mana;
    }

}
