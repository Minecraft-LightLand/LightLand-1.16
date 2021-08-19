package com.lcy0x1.core.chem;

import com.google.common.collect.Maps;
import com.lcy0x1.core.util.SerialClass;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Getter
@Log4j2
public class ReactionPool {

    private static final double LARGE_LIMIT = 1e10, REVERSAL_CHECK = 1e-10, EDGE_CHECK = 1e-3, EPSILON = 1e-6;

    private final int equationSize, substanceSize;
    private final SubstanceContent[] substanceContents;
    private final EquationContent[] equationContents;
    private final Result EmptyResult;
    private final Evaluator EmptyEvaluator = new Evaluator() {
        @Override
        public void step() {
        }

        @Override
        public double deviation() {
            return 0;
        }

        @Override
        public Result toResult() {
            return EmptyResult;
        }

        @Override
        public Result complete(double eps, double max_time) {
            return EmptyResult;
        }

        @Override
        public boolean isComplete() {
            return false;
        }
    };

    public ReactionPool(Collection<String> substanceSet, Collection<Equation> equationSet, Map<String, Double> count) {
        equationSize = equationSet.size();
        substanceSize = substanceSet.size();
        substanceContents = new SubstanceContent[substanceSize];
        int i = 0;
        Map<String, SubstanceContent> imap = Maps.newLinkedHashMap();
        for (String id : substanceSet) {
            SubstanceContent content = new SubstanceContent(id, count.getOrDefault(id, 0d), equationSize);
            imap.put(id, content);
            substanceContents[i] = content;
            i++;
        }
        equationContents = new EquationContent[equationSize];
        i = 0;
        for (Equation e : equationSet) {
            equationContents[i] = new EquationContent(e, imap);
            for (SubstanceContent content : equationContents[i].in)
                content.coefs[i] = -1;
            for (SubstanceContent content : equationContents[i].r)
                content.coefs[i] = 1;
            i++;
        }
        EmptyResult = emptyResult();
    }

    public Evaluator newEvaluator() {
        if (equationSize == 0) {
            return EmptyEvaluator;
        } else {
            return new EvaluatorImpl();
        }
    }

    public interface Evaluator {
        void step();

        double deviation();

        Result toResult();

        Result complete(double eps, double max_time);

        boolean isComplete();
    }

    private Result emptyResult() {
        final Result result = new Result();
        for (SubstanceContent substanceContent : substanceContents) {
            result.map.put(substanceContent.id, .0);
        }
        return result;
    }

    @Log4j2
    private static class SubstanceContent {
        private final String id;
        private final double init;
        private final int[] coefs;

        private SubstanceContent(String id, double init, int substanceSize) {
            this.init = init;
            this.id = id;
            this.coefs = new int[substanceSize];
        }

        private double getValue(double[] val) {
            //noinspection ArrayHashCode
            //log.debug("{} get value {}{}", this, val.hashCode(), val);
            double value = init;
            for (int i = 0; i < coefs.length; i++)
                value += coefs[i] * val[i];
            return value;
        }

        private void clearEdge(double[] vec, double[] val) {
            double c = 0;
            double v = init;
            for (int i = 0; i < coefs.length; i++) {
                c += coefs[i] * val[i];
                v += coefs[i] * vec[i];
            }
            if (v < EDGE_CHECK && c > 0) {
                for (int i = 0; i < coefs.length; i++)
                    if (coefs[i] * val[i] > 0)
                        val[i] = 0;
            }
        }

        private double getMax(double[] vec, double[] val) {
            double c = 0;
            double v = init;
            for (int i = 0; i < coefs.length; i++) {
                c += coefs[i] * val[i];
                v += coefs[i] * vec[i];
            }
            if (c <= 0)
                return Double.MAX_VALUE;
            return v / c;
        }

    }

    private class EquationContent {

        private final SubstanceContent[] in, r;
        private final double k;

        private final double[] vrs;
        private final double[] vis;

        public EquationContent(Equation equation, Map<String, SubstanceContent> imap) {
            in = new SubstanceContent[equation.in.length];
            r = new SubstanceContent[equation.result.length];
            for (int i = 0; i < in.length; i++) {
                in[i] = imap.get(equation.in[i]);
            }
            for (int i = 0; i < r.length; i++) {
                r[i] = imap.get(equation.result[i]);
            }
            k = equation.k;

            vrs = new double[r.length];
            vis = new double[in.length];
        }

        private double getValue(double[] vector) {
            double p0 = k;
            double p1 = 1;
            for (SubstanceContent content : r)
                p0 *= content.getValue(vector);
            for (SubstanceContent content : in)
                p1 *= content.getValue(vector);
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
            for (int i = 0; i < equationSize; i++) {
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

    private class EvaluatorImpl implements Evaluator {

        private final double[] vector = new double[equationSize];
        private final double[] gradient = new double[equationSize];
        private final double[] diff = new double[equationSize];

        public boolean complete = false;

        public EvaluatorImpl() {
        }

        @Override
        public void step() {
            if (equationSize == 0)
                return;
            Arrays.fill(gradient, 0);
            for (int i = 0; i < equationSize; i++) {
                diff[i] = equationContents[i].getValue(vector);
                equationContents[i].addGradient(vector, gradient);
            }
            double len = 0;
            for (SubstanceContent substanceContent : substanceContents)
                substanceContent.clearEdge(vector, gradient);
            for (int i = 0; i < equationSize; i++) {
                len += gradient[i] * gradient[i];
            }
            len = Math.sqrt(len);
            for (int i = 0; i < equationSize; i++) {
                gradient[i] /= len;
            }
            double max = Double.MAX_VALUE;
            for (SubstanceContent substanceContent : substanceContents) {
                max = Math.min(max, substanceContent.getMax(vector, gradient));
            }
            descent(max);
        }

        @Override
        public double deviation() {
            for (int i = 0; i < equationSize; i++) {
                diff[i] = equationContents[i].getValue(vector);
            }
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

        @Override
        public Result toResult() {
            Result result = new Result();
            for (int i = 0; i < substanceSize; i++) {
                result.map.put(substanceContents[i].id, substanceContents[i].getValue(vector));
            }
            return result;
        }

        @Override
        public Result complete(double eps, double max_time) {
            if (equationSize == 0)
                return toResult();
            long time = System.nanoTime();
            while (true) {
                if (deviation() < eps * eps) {
                    complete = true;
                    break;
                }
                step();
                long t1 = System.nanoTime();
                if (t1 - time > max_time)
                    break;
            }
            return toResult();
        }

        @Override
        public boolean isComplete() {
            return complete;
        }
    }

    private class Descent {

        private final double[] init, grad;
        private final double[] vec = new double[equationSize];

        private Descent(double[] init, double[] grad) {
            this.init = init;
            this.grad = grad;
        }

        private double evaluate(double val) {
            for (int i = 0; i < equationSize; i++)
                vec[i] = init[i] - grad[i] * val;
            double ans = 0;
            for (EquationContent equationContent : equationContents) {
                double v = equationContent.getValue(vec);
                ans += v * v;
            }
            return ans;
        }

        private void set(double a) {
            for (int i = 0; i < equationSize; i++)
                init[i] -= grad[i] * a;
        }

    }

    @SerialClass
    @Getter
    public static class Result {
        @SerialClass.SerialField(generic = {String.class, Double.class})
        private final Map<String, Double> map = Maps.newLinkedHashMap();

        @Deprecated
        public Result() {
        }
    }

}
