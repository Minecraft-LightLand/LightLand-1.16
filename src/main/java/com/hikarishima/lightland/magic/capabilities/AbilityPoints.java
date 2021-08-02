package com.hikarishima.lightland.magic.capabilities;

import com.hikarishima.lightland.magic.profession.Profession;
import com.lcy0x1.core.util.SerialClass;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SerialClass
public class AbilityPoints {

    private final MagicHandler parent;
    @SerialClass.SerialField
    public int general, body, magic, element, arcane;

    @SerialClass.SerialField
    public int health, strength, speed;

    @SerialClass.SerialField
    public Profession profession = null;

    AbilityPoints(MagicHandler parent) {
        this.parent = parent;
    }

    public boolean canLevelArcane() {
        return arcane > 0 || magic > 0 || general > 0;
    }

    public boolean canLevelMagic() {
        return magic > 0 || general > 0;
    }

    public boolean canLevelBody() {
        return body > 0 || general > 0;
    }

    public boolean canLevelElement() {
        return element > 0;
    }

    public boolean levelArcane() {
        if (arcane > 0) arcane--;
        else if (magic > 0) magic--;
        else if (general > 0) general--;
        else return false;
        return true;
    }

    public boolean levelMagic() {
        if (magic > 0) magic--;
        else if (general > 0) general--;
        else return false;
        return true;
    }

    public boolean levelBody() {
        if (body > 0) body--;
        else if (general > 0) general--;
        else return false;
        return true;
    }

    public void levelElement() {
        if (element > 0) element--;
    }

    public Profession getProfession() {
        return profession;
    }

    public boolean setProfession(Profession prof) {
        if (profession != null) {
            return false;
        }
        profession = prof;
        prof.init(parent);
        return true;
    }

    public enum LevelType {
        HEALTH((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            if (h.abilityPoints.levelBody())
                h.abilityPoints.health++;
        }),
        STRENGTH((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            if (h.abilityPoints.levelBody())
                h.abilityPoints.strength++;
        }),
        SPEED((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            if (h.abilityPoints.levelBody())
                h.abilityPoints.speed++;
        }),
        MANA((h) -> h.abilityPoints.canLevelMagic(), (h) -> {
            if (h.abilityPoints.levelMagic())
                h.magicAbility.magic_level++;
        }),
        SPELL((h) -> h.abilityPoints.canLevelMagic(), (h) -> {
            if (h.abilityPoints.levelMagic())
                h.magicAbility.spell_level++;

        });

        public final Predicate<MagicHandler> check;
        public final Consumer<MagicHandler> run;

        LevelType(Predicate<MagicHandler> check, Consumer<MagicHandler> run) {
            this.check = check;
            this.run = run;
        }

    }

}
