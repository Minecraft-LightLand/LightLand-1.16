package com.lcy0x1.core.chem;

import java.util.Collection;

public class Pool {

    public Collection<String> elements;
    public Collection<Formula> formulas;

    public Pool(Collection<String> elements, Collection<Formula> formulas) {
        this.elements = elements;
        this.formulas = formulas;
    }

}
