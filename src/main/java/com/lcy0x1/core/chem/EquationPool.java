package com.lcy0x1.core.chem;

import com.google.common.collect.Maps;
import com.lcy0x1.core.util.SerialClass;

import java.util.*;

@SerialClass
public class EquationPool {

    @SerialClass.SerialField(generic = {String.class, AbChemObj.class})
    public LinkedHashMap<String, AbChemObj> objects = Maps.newLinkedHashMap();

    @SerialClass.SerialField
    public Equation[] equations;

    private final Map<String, Set<Equation>> map = Maps.newLinkedHashMap();

    @SerialClass.OnInject
    public void onInject() {
        for (Equation eq : equations) {
            for (String s : eq.in)
                add(s, eq);
            for (String s : eq.result)
                add(s, eq);
        }
    }

    private void add(String obj, Equation eq) {
        Set<Equation> set;
        if (!map.containsKey(obj))
            map.put(obj, set = new LinkedHashSet<>());
        else set = map.get(obj);
        set.add(eq);
    }

    public ReactionPool getPool(Map<String, Integer> objs) {
        Queue<String> queue = new ArrayDeque<>(objs.keySet());
        Set<String> objset = new LinkedHashSet<>(objs.keySet());
        Set<Equation> eqset = new LinkedHashSet<>();
        while (queue.size() > 0) {
            String str = queue.poll();
            if (!map.containsKey(str))
                continue;
            for (Equation e : map.get(str)) {
                if (eqset.contains(e))
                    continue;
                List<String> lr = Arrays.asList(e.result);
                List<String> li = Arrays.asList(e.in);
                boolean re;
                if ((re = objset.containsAll(lr)) || objset.containsAll(li)) {
                    eqset.add(e);
                    List<String> temp = re ? li : lr;
                    for (String s : temp)
                        if (!objset.contains(s)) {
                            queue.add(s);
                            objset.add(s);
                        }
                }
            }
        }
        return new ReactionPool(objset, eqset, objs);
    }

}
