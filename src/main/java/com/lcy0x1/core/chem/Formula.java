package com.lcy0x1.core.chem;

import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class Formula {

    public Entry[] left, right;

    public double balance;

    public static class Entry {

        @SerialClass.SerialField
        public String id;

        @SerialClass.SerialField
        public int val;

        @SerialClass.SerialField
        public State state;

    }

    public enum State {
        SOLID, LIQUID, GAS
    }

}
