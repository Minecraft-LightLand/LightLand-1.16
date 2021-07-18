package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.item.arcane.internal.ArcaneType;
import com.lcy0x1.core.util.NBTObj;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

@SerialClass
public class MagicAbility {

    @SerialClass.SerialField
    public CompoundNBT arcane_type = new CompoundNBT();
    public NBTObj arcane_manager;
    @SerialClass.SerialField
    protected int magic_mana, spell_load, arcane_mana;
    @SerialClass.SerialField
    protected int magic_level, spell_level;
    private final MagicHandler parent;

    MagicAbility(MagicHandler parent) {
        this.parent = parent;
    }

    public void giveArcaneMana(int mana) {
        arcane_mana = MathHelper.clamp(arcane_mana + mana, 0, getMaxArcaneMana());
    }

    public int getArcaneMana() {
        return arcane_mana;
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

    public int getMaxArcaneMana() {
        return arcane_type.getAllKeys().size() * 10;
    }

    public boolean isArcaneTypeUnlocked(ArcaneType type) {
        return arcane_manager.getSub(type.getID()).tag.getInt("level") > 0;
    }

    public void unlockArcaneType(ArcaneType type) {
        if (!isArcaneTypeUnlocked(type)) {
            arcane_manager.getSub(type.getID()).tag.putInt("level", 1);
            parent.abilityPoints.levelArcane();
        }
    }

}
