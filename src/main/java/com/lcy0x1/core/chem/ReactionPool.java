package com.lcy0x1.core.chem;

import com.google.common.collect.Maps;
import com.lcy0x1.core.util.SerialClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ReactionPool {

    private static final double LARGE_LIMIT = 1e10, REVERSAL_CHECK = 1e-10, EDGE_CHECK = 1e-3, EPSILON = 1e-6;

    public final int n, m;
    public final Obj[] objs;
    public final Eq[] eqs;

    ReactionPool(Collection<String> objset, Collection<Equation> eqset, Map<String, Integer> count) {
        n = eqset.size();
        m = objset.size();
        objs = new Obj[m];
        int i = 0;
        Map<String, Obj> imap = Maps.newLinkedHashMap();
        for (String str : objset) {
            Obj o = new Obj(str, count.getOrDefault(str, 0), n);
            imap.put(str, o);
            objs[i] = o;
            i++;
        }
        eqs = new Eq[n];
        i = 0;
        for (Equation e : eqset) {
            eqs[i] = new Eq(e, imap);
            for (Obj o : eqs[i].in)
                o.coefs[i] = -1;
            for (Obj o : eqs[i].r)
                o.coefs[i] = 1;
            i++;
        }
    }

    public class Obj {

        private final String obj;
        private final int init;
        private final int[] coefs;

        private Obj(String obj, int init, int n) {
            this.init = init;
            this.obj = obj;
            this.coefs = new int[n];
        }

        private double getValue(double[] val) {
            double value = init;
            for (int i = 0; i < n; i++)
                value += coefs[i] * val[i];
            return value;
        }

        private void clearEdge(double[] vec, double[] val) {
            double c = 0;
            double v = init;
            for (int i = 0; i < n; i++) {
                c += coefs[i] * val[i];
                v += coefs[i] * vec[i];
            }
            if (v < EDGE_CHECK && c > 0) {
                for (int i = 0; i < n; i++)
                    if (coefs[i] * val[i] > 0)
                        val[i] = 0;
            }
        }

        private double getMax(double[] vec, double[] val) {
            double c = 0;
            double v = init;
            for (int i = 0; i < n; i++) {
                c += coefs[i] * val[i];
                v += coefs[i] * vec[i];
            }
            if (c <= 0)
                return Double.MAX_VALUE;
            return v / c;
        }

    }

    public class Eq {

        private final Obj[] in, r;
        private final double k;

        private final double[] vrs;
        private final double[] vis;

        public Eq(Equation equation, Map<String, Obj> imap) {
            in = new Obj[equation.in.length];
            r = new Obj[equation.result.length];
            for (int i = 0; i < in.length; i++)
                in[i] = imap.get(equation.in[i]);
            for (int i = 0; i < r.length; i++)
                r[i] = imap.get(equation.result[i]);
            k = equation.k;

            vrs = new double[r.length];
            vis = new double[in.length];
        }

        private double getValue(double[] vector) {
            double p0 = k;
            double p1 = 1;
            for (Obj o : r)
                p0 *= o.getValue(vector);
            for (Obj o : in)
                p1 *= o.getValue(vector);
            return p0 - p1;
        }

        private void addGradient(double[] vector, double[] grad) {
            double v0 = getValue(vector);
            double pr = 1;
            double pi = 1;
            int zr = 0;
            int zi = 0;
            for (int i = 0; i < r.length; i++) {
                vrs[i] = r[i].getValue(vector);
                if (vrs[i] != 0)
                    pr *= vrs[i];
                else zr++;
            }
            for (int i = 0; i < in.length; i++) {
                vis[i] = in[i].getValue(vector);
                if (vis[i] != 0)
                    pi *= vis[i];
                else zi++;
            }
            for (int i = 0; i < n; i++) {
                double sum = 0;
                if (zr <= 1)
                    for (int j = 0; j < r.length; j++) {
                        if (zr == 0)
                            sum += k * pr / vrs[j] * r[j].coefs[i];
                        else if (zr == 1 && vrs[j] == 0)
                            sum += k * pr * r[j].coefs[i];
                    }
                if (zi <= 1)
                    for (int j = 0; j < in.length; j++) {
                        if (zi == 0)
                            sum -= pi / vis[j] * in[j].coefs[i];
                        else if (zi == 1 && vis[j] == 0)
                            sum -= pi * in[j].coefs[i];
                    }
                grad[i] += v0 * sum;
            }
        }

    }

    public class Evaluator {

        private final double[] vector = new double[n];
        private final double[] gradient = new double[n];
        private final double[] diff = new double[n];

        public Evaluator() {

        }

        public void step() {
            Arrays.fill(gradient, 0);
            for (int i = 0; i < n; i++) {
                diff[i] = eqs[i].getValue(vector);
                eqs[i].addGradient(vector, gradient);
            }
            double len = 0;
            for (Obj o : objs)
                o.clearEdge(vector, gradient);
            for (int i = 0; i < n; i++) {
                len += gradient[i] * gradient[i];
            }
            len = Math.sqrt(len);
            for (int i = 0; i < n; i++) {
                gradient[i] /= len;
            }
            double max = Double.MAX_VALUE;
            for (Obj o : objs) {
                max = Math.min(max, o.getMax(vector, gradient));
            }
            descent(max);
        }

        public double deviation() {
            double ans = 0;
            for (double d : diff)
                ans += d * d;
            return ans;
        }

        private void descent(double max) {
            Descent descent = new Descent(vector, gradient);
            double a0 = 0;
            double a1 = 0.1 * max;
            double a2 = 0.2 * max;
            double v0 = descent.evaluate(a0);
            double v1 = descent.evaluate(a1);
            double v2 = descent.evaluate(a2);
            if (v0 > v1 && v1 > v2) {
                // go beyond
                a0 = a1;
                a1 = a2;
                a2 = a2 * 2;
                v0 = v1;
                v1 = v2;
                v2 = descent.evaluate(a2);
                while (v2 < v1) {
                    a0 = a1;
                    a1 = a2;
                    a2 *= 2;
                    v0 = v1;
                    v1 = v2;
                    v2 = descent.evaluate(a2);
                    if (a2 > LARGE_LIMIT)
                        throw new RuntimeException("infinite descent");
                }
                // now v0 > v1 < v2
            }
            while (v1 > v0) {
                a2 = a1;
                v2 = v1;
                a1 = (a0 + a1) / 2;
                v1 = descent.evaluate(a1);
                if (Math.abs(a1 - a0) < REVERSAL_CHECK)
                    throw new RuntimeException("wrong direction");
            }
            // v0 > v1 < v2
            double b0, b1, vb0, vb1;
            while (Math.abs(a2 - a0) > ReactionPool.EPSILON) {
                b0 = (a0 + a1) / 2;
                b1 = (a1 + a2) / 2;
                vb0 = descent.evaluate(b0);
                vb1 = descent.evaluate(b1);
                if (vb0 < v1) {
                    // min at left
                    a2 = a1;
                    a1 = b0;
                    v1 = vb0;
                } else if (vb1 < v1) {
                    // min at right
                    a0 = a1;
                    a1 = b1;
                    v1 = vb1;
                } else {
                    // min at middle
                    a0 = b0;
                    a2 = b1;
                }
            }
            descent.set((a2 + a0) / 2);
        }

        public Result toResult() {
            Result result = new Result();
            for (int i = 0; i < m; i++) {
                result.map.put(objs[i].obj, objs[i].getValue(vector));
            }
            return result;
        }

    }

    private class Descent {

        private final double[] init, grad;
        private final double[] vec = new double[n];

        private Descent(double[] init, double[] grad) {
            this.init = init;
            this.grad = grad;
        }

        private double evaluate(double val) {
            for (int i = 0; i < n; i++)
                vec[i] = init[i] - grad[i] * val;
            double ans = 0;
            for (Eq eq : eqs) {
                double v = eq.getValue(vec);
                ans += v * v;
            }
            return ans;
        }

        private void set(double a) {
            for (int i = 0; i < n; i++)
                init[i] -= grad[i] * a;
        }

    }

    @SerialClass
    public static class Result {

        @SerialClass.SerialField(generic = {String.class, Double.class})
        public final Map<String, Double> map = Maps.newLinkedHashMap();

        private Result() {

        }

    }

}
