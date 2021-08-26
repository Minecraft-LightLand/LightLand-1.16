package com.lcy0x1.core.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

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

    @FunctionalInterface
    public interface ExcRun {

        void get() throws Throwable;

    }

    @FunctionalInterface
    public interface ExcSup<T> {

        T get() throws Throwable;

    }

}
