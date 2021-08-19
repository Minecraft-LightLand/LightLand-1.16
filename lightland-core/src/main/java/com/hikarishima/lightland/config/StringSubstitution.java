package com.hikarishima.lightland.config;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Map;

public class StringSubstitution {

    public static Map<String, String> get(String... strs) {
        Map<String, String> ans = new HashMap<>();
        for (int i = 0; i < strs.length - 1; i += 2) {
            ans.put(strs[i], strs[i + 1]);
        }
        return ans;
    }

    public static String toString(ITextComponent text) {
        if (text instanceof TranslationTextComponent)
            return LanguageMap.getInstance().getOrDefault(((TranslationTextComponent) text).getKey());
        return text.getContents();
    }

    public static String format(String str, Map<String, String> map) {
        for (String key : map.keySet()) {
            str = str.replaceAll("\\{" + key + "\\}", map.get(key));
        }
        return str;
    }

}
