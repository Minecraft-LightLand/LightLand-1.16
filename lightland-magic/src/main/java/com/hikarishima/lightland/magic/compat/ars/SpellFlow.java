package com.hikarishima.lightland.magic.compat.ars;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.lcy0x1.core.util.SerialClass;

import java.util.*;
import java.util.stream.Collectors;

@SerialClass
public class SpellFlow {

    @SerialClass
    public static class Node {

        class Builder {

            Set<Node> parent = new HashSet<>();

            Set<Thread> threads = new HashSet<>();

            void resolve(SpellFlow.Builder builder) {
                for (Thread t : threads) {
                    t.list.add(Node.this);
                }
                List<Node> next = getList();
                if (next.size() == 0) {
                    builder.complete.addAll(threads);
                    return;
                }
                for (Node n : next) {
                    Builder b = builder.map.get(n);
                    for (Thread t : threads) {
                        b.threads.add(t.copy());
                    }
                    b.parent.remove(Node.this);
                    builder.update(n);
                }
            }

        }

        @SerialClass.SerialField
        public AbstractSpellPart glyph;

        List<Node> getList() {
            return new ArrayList<>();//TODO
        }

    }

    public static class Thread {

        final List<Node> list = new ArrayList<>();

        Thread copy() {
            Thread copy = new Thread();
            copy.list.addAll(list);
            return copy;
        }

        Spell toSpell() {
            //TODO validate
            Spell.Builder b = new Spell.Builder();
            for (Node n : list) {
                if (n.glyph != null) {
                    b.add(n.glyph);
                }
            }
            return b.build();
        }

    }

    public class Builder {

        final Map<Node, Node.Builder> map = new HashMap<>();
        final Set<Thread> complete = new HashSet<>();
        final Set<Node> other = new HashSet<>();

        Set<Node> zero = new HashSet<>();

        public List<Spell> getSpells() {
            for (Node n : list) {
                map.put(n, n.new Builder());
            }
            for (Node n : list) {
                for (Node m : n.getList()) {
                    map.get(m).parent.add(n);
                }
            }
            for (Node n : list) {
                if (map.get(n).parent.size() == 0) {
                    zero.add(n);
                } else {
                    other.add(n);
                }
            }
            while (other.size() + zero.size() > 0) {
                Set<Node> temp = zero;
                if (temp.size() == 0) {
                    break;
                }
                zero = new HashSet<>();
                for (Node n : temp) {
                    map.get(n).resolve(this);
                }
            }
            return complete.stream().map(Thread::toSpell).collect(Collectors.toList());
        }

        void update(Node n) {
            Node.Builder b = map.get(n);
            if (b.parent.size() == 0) {
                other.remove(n);
                zero.add(n);
            }
        }

    }

    @SerialClass.SerialField(generic = Node.class)
    public List<Node> list = new ArrayList<>();

}
