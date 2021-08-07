package com.lcy0x1.core.chem;

import com.lcy0x1.core.util.SerialClass;

import java.util.*;

@SerialClass
public class FormulaPool {

    @SerialClass.SerialField
    public Formula[] list;

    private Map<String, List<Formula>> left_map, right_map;

    @SerialClass.OnInject
    public void onInject() {
        for (Formula f : list) {
            for (Formula.Entry e : f.left) {
                List<Formula> l;
                if (left_map.containsKey(e.id))
                    l = left_map.get(e.id);
                else left_map.put(e.id, l = new ArrayList<>());
                l.add(f);
            }
            for (Formula.Entry e : f.right) {
                List<Formula> l;
                if (right_map.containsKey(e.id))
                    l = right_map.get(e.id);
                else right_map.put(e.id, l = new ArrayList<>());
                l.add(f);
            }
        }
    }

    public Pool getPool(List<String> ings) {
        Queue<String> queue = new ArrayDeque<>(ings);
        Set<String> elements = new TreeSet<>(ings);
        Set<Formula> formulas = new HashSet<>();
        while(queue.size()>0){
            String e = queue.poll();
            List<Formula> potential = left_map.get(e);
            for (Formula f : potential){
                if (formulas.contains(f))
                    continue;
                boolean all = true;
                for (Formula.Entry ent : f.left)
                    if (!elements.contains(ent.id)){
                        all = false;
                        break;
                    }
                if (all){
                    formulas.add(f);
                    for (Formula.Entry ent : f.right){
                        if (!elements.contains(ent.id)){
                            elements.add(ent.id);
                            queue.add(ent.id);
                        }
                    }
                }
            }
        }
        return new Pool(elements, formulas);
    }

}
