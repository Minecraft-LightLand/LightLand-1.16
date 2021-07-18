package com.hikarishima.lightland.magic.capabilities;

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

    public void levelArcane() {
        if (arcane > 0) arcane--;
        else if (magic > 0) magic--;
        else if (general > 0) general--;
    }

    public void levelMagic() {
        if (magic > 0) magic--;
        else if (general > 0) general--;
    }

    public void levelBody() {
        if (body > 0) body--;
        else if (general > 0) general--;
    }

    public void levelElement() {
        if (element > 0) element--;
    }

    public enum LevelType {
        HEALTH((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            h.abilityPoints.levelBody();
            h.abilityPoints.health++;
        }),
        STRENGTH((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            h.abilityPoints.levelBody();
            h.abilityPoints.strength++;
        }),
        SPEED((h) -> h.abilityPoints.canLevelBody(), (h) -> {
            h.abilityPoints.levelBody();
            h.abilityPoints.speed++;
        }),
        MANA((h) -> h.abilityPoints.canLevelMagic(), (h) -> {
            h.abilityPoints.levelMagic();
            //TODO
        }),
        SPELL((h) -> h.abilityPoints.canLevelMagic(), (h) -> {
            h.abilityPoints.levelMagic();
            //TODO
        });

        public final Predicate<MagicHandler> check;
        public final Consumer<MagicHandler> run;

        LevelType(Predicate<MagicHandler> check, Consumer<MagicHandler> run) {
            this.check = check;
            this.run = run;
        }

    }

}
