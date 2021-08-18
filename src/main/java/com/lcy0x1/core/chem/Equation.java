package com.lcy0x1.core.chem;

import com.lcy0x1.core.util.SerialClass;

@SerialClass
public class Equation {

    @SerialClass.SerialField
    public String[] in, result;

    @SerialClass.SerialField
    public double k;

    @SerialClass.SerialField
    public String environment;

}
