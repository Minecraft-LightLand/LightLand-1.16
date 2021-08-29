package com.lcy0x1.base.proxy;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
public class MethodAccessGroup {
    private final List<MethodAccess> group;

    @Data
    public static class MethodAccessIndex {
        @NotNull
        private final MethodAccess methodAccess;
        @NotNull
        private final int index;

        public Object invoke(Object object, Object... args) {
            return methodAccess.invoke(object, index, args);
        }
    }

    @Nullable
    public MethodAccessIndex getIndex(String methodName, Class<?>... paramTypes) {
        final Iterator<MethodAccess> methodAccessIterator = group.iterator();
        while (methodAccessIterator.hasNext()) {
            final MethodAccess methodAccess = methodAccessIterator.next();
            if (methodAccess == null) {
                methodAccessIterator.remove();
                continue;
            }
            try {
                final int index = methodAccess.getIndex(methodName, paramTypes);
                return new MethodAccessIndex(methodAccess, index);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
