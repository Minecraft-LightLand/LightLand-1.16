package com.hikarishima.lightland.magic.recipe;

import com.hikarishima.lightland.magic.MagicElement;
import com.lcy0x1.core.util.SerialClass;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.TreeMap;

@SerialClass
public class DefMagicRecipe extends IMagicRecipe<DefMagicRecipe> {

    @SerialClass.SerialField(generic = {String.class, MagicElement.class})
    public TreeMap<String, MagicElement> elements = new TreeMap<>();
    @SerialClass.SerialField
    public String[] flows;

    public DefMagicRecipe(ResourceLocation id) {
        super(id, MagicRecipeRegistry.RSM_DEF);
    }

    private static boolean flowRegex(char[] chars, String s0, String s1, boolean[][] bools, boolean bidirect) {
        int[] i0 = new int[s0.length()];
        int[] i1 = new int[s1.length()];
        for (int i = 0; i < s0.length(); i++) {
            i0[i] = -1;
            for (int c = 0; c < chars.length; c++) {
                if (chars[c] == s0.charAt(i)) {
                    i0[i] = c;
                    break;
                }
            }
            if (i0[i] == -1)
                return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            i1[i] = -1;
            for (int c = 0; c < chars.length; c++) {
                if (chars[c] == s1.charAt(i)) {
                    i1[i] = c;
                    break;
                }
            }
            if (i1[i] == -1)
                return false;
        }
        for (int k : i0)
            for (int i : i1) {
                if (k == i)
                    return false;
                if (bools[k][i])
                    return false;
                bools[k][i] = true;
                if (bidirect) {
                    if (bools[i][k])
                        return false;
                    bools[i][k] = true;
                }
            }
        return true;
    }

    private static boolean flowRound(char[] chars, String str, boolean[][] bools) {
        int[] i0 = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            i0[i] = -1;
            for (int c = 0; c < chars.length; c++) {
                if (chars[c] == str.charAt(i)) {
                    i0[i] = c;
                    break;
                }
            }
            if (i0[i] == -1)
                return false;
        }
        for (int i : i0)
            for (int j : i0) {
                if (i == j)
                    continue;
                bools[i][j] = true;
            }
        return true;
    }

    @SerialClass.OnInject
    public void onInject() {
        int n = elements.size();
        MagicElement[] elems = new MagicElement[n];
        char[] chars = new char[n];
        int i = 0;
        for (Map.Entry<String, MagicElement> ent : elements.entrySet()) {
            elems[i] = ent.getValue();
            if (ent.getKey().length() != 1)
                LogManager.getLogger().error("key length not 1 in " + this.id);
            chars[i] = ent.getKey().charAt(0);
            i++;
        }
        boolean[][] bools = new boolean[6][6];
        for (String flow : flows) {
            if (flow.contains("<->")) {
                String[] strs = flow.split("<->");
                if (strs.length != 2 || !flowRegex(chars, strs[0], strs[1], bools, true))
                    LogManager.getLogger().error("illegal side expression" + flow + " in " + this.id);
            } else if (flow.contains("->")) {
                String[] strs = flow.split("->");
                if (strs.length != 2 || !flowRegex(chars, strs[0], strs[1], bools, false))
                    LogManager.getLogger().error("illegal side expression " + flow + " in " + this.id);
            } else if (flow.endsWith("|")) {
                if (!flowRound(chars, flow.substring(0, flow.length() - 1), bools))
                    LogManager.getLogger().error("illegal round expression " + flow + " in " + this.id);
            } else LogManager.getLogger().error("illegal connector " + flow + " in " + this.id);
        }
        register(elems, bools);
    }

}
