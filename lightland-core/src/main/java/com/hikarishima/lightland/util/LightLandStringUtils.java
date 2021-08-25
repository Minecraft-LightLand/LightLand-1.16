package com.hikarishima.lightland.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class LightLandStringUtils {
    @NotNull
    public static boolean[] contains(CharSequence source, CharSequence[] target) {
        final boolean[] result = new boolean[target.length];

        //int iSource, iSourceMa, iTargetArray, iTarget;
        //
        //for (iSource = 0; iSource < source.length(); iSource++) {
        //    targetArray:
        //    for (iTargetArray = 0; iTargetArray < target.length; iTargetArray++) {
        //        if (target[iTargetArray] == null) {
        //            continue;
        //        }
        //
        //        for (iTarget = 0, iSourceMa = 0; iTarget < target[iTargetArray].length(); iTarget++, iSourceMa++) {
        //            if (source.charAt(iSource + iSourceMa) != target[iTargetArray].charAt(iTarget)) {
        //                continue targetArray;
        //            }
        //        }
        //        result[iTargetArray] = true;
        //    }
        //}

        for (int i = 0; i < target.length; i++) {
            result[i] = StringUtils.contains(source, target[i]);
        }
        return result;
    }
}
