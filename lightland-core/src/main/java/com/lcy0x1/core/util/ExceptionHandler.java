package com.lcy0x1.core.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;

public class ExceptionHandler {

    public static void run(ExcRun run) {
        try {
            run.get();
        } catch (Throwable e) {
            LogManager.getLogger().throwing(Level.ERROR, e);
        }
    }

    public static <T> T get(ExcSup<T> sup) {
        try {
            return sup.get();
        } catch (Throwable e) {
            LogManager.getLogger().throwing(Level.ERROR, e);
            return null;
        }
    }

    public static <T> T ignore(ExcSup<T> sup) {
        try {
            return sup.get();
        } catch (Throwable e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> target, String name, Class<?>... cls) {
        Method m;
        Class<?>[] t = new Class[]{target};
        while ((m = ignore(() -> t[0].getDeclaredMethod(name, cls))) == null)
            t[0] = t[0].getSuperclass();
        m.setAccessible(true);
        return m;
    }

    @FunctionalInterface
    public interface ExcRun {

        void get() throws Throwable;

    }

    @FunctionalInterface
    public interface ExcSup<T> {

        T get() throws Throwable;

    }

}
