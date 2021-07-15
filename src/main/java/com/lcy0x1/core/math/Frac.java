package com.lcy0x1.core.math;

/**
 * represents a positive fraction
 *
 * @author arthur
 * @Date 2020-9-24
 */
public class Frac implements Comparable<Frac> {

    public static Frac mult(Frac f0, Frac f1) {
        long gcd0 = gcd(f0.num, f1.den);
        long gcd1 = gcd(f1.num, f0.den);
        long num = Math.multiplyExact(f0.num / gcd0, f1.num / gcd1);
        long den = Math.multiplyExact(f0.den / gcd1, f1.den / gcd0);
        return new Frac(num, den);
    }

    private static long gcd(long a, long b) {
        long max = Math.max(a, b);
        long min = Math.min(a, b);
        return min == 0 ? max : gcd(min, max % min);
    }

    public long num, den;

    public Frac(long num, long den) {
        this.num = num;
        this.den = den;
        validate();
    }

    public void add(Frac o) {
        long gcd = gcd(den, o.den);
        long v0 = Math.multiplyExact(num, o.den / gcd);
        long v1 = Math.multiplyExact(o.num, den / gcd);
        num = Math.addExact(v0, v1);
        den = Math.multiplyExact(den, o.den / gcd);
        validate();
    }

    @Override
    public int compareTo(Frac o) {
        if (equals(o))
            return 0;
        return Double.compare(getVal(), o.getVal());

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Frac) {
            Frac f = (Frac) o;
            return f.num == num && f.den == den;
        }
        return false;
    }

    public double getVal() {
        return num / den;
    }

    public void times(Frac base) {
        long gcd0 = gcd(num, base.den);
        long gcd1 = gcd(base.num, den);
        num = Math.multiplyExact(num / gcd0, base.num / gcd1);
        den = Math.multiplyExact(den / gcd1, base.den / gcd0);
        validate();
    }

    @Override
    public String toString() {
        return num + "/" + den;
    }

    private void validate() {
        long gcd = gcd(num, den);
        num /= gcd;
        den /= gcd;
    }

}