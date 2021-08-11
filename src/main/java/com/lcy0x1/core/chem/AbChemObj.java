package com.lcy0x1.core.chem;

import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class AbChemObj {

    @SerialClass.SerialField
    public State state = State.LIQUID;

    public String id;

    public enum State {
        LIQUID, SOLID
    }

}
