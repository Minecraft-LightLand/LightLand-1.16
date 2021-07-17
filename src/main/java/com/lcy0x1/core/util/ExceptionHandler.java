package com.lcy0x1.core.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class ExceptionHandler {

    public static void run(ExcRun run) {
        try {
            run.get();
        } catch (Exception e) {
            LogManager.getLogger().throwing(Level.ERROR, e);
        }
    }

    public static <T> T get(ExcSup<T> sup) {
        try {
            return sup.get();
        } catch (Exception e) {
            LogManager.getLogger().throwing(Level.ERROR, e);
            return null;
        }
    }

    @FunctionalInterface
    public interface ExcRun {

        void get() throws Exception;

    }

    @FunctionalInterface
    public interface ExcSup<T> {

        T get() throws Exception;

    }

}
