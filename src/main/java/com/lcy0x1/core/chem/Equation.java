package com.lcy0x1.core.chem;

import com.lcy0x1.core.util.SerialClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SerialClass
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Equation {

    @SerialClass.SerialField
    public String[] in, result;

    @SerialClass.SerialField
    public double k;

    @SerialClass.SerialField
    public String environment;

}
