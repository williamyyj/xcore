package org.cc.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author william
 */
public class CCFunc {

    private static ConcurrentHashMap<String, Object> cache;

    private static ConcurrentHashMap<String, Object> cache() {
        if (cache == null) {
            cache = new ConcurrentHashMap<String, Object>();
        }
        return cache;
    }

    private static final String prefix = "org.cc.fun";

    private static String classId(String id) {
        if (id != null && !id.startsWith(prefix)) {
            return prefix + "." + id;
        } else {
            return id;
        }
    }

    private static Object load(String classId) {
        try {
            Object fun = cache().get(classId);
            if (fun == null) {
                Class<?> cls = Class.forName(classId);
                CCLogger.debug("Load function  " + classId);
                fun = cls.newInstance();
                cache().put(classId, fun);
            }
            return fun;
        } catch (Exception e) {
            CCLogger.error("Can't find classId : " + e.getMessage());
            return null;
        }
    }

    public static Object apply(String id, Object p) {
        String classId = classId(id);
        Function fun = (Function) load(classId);
        if (fun != null) {
            return fun.apply(p);
        }
        return null;
    }

    public static Object apply2(String id, Object p, Object u) {
        String classId = classId(id);
        BiFunction fun = (BiFunction) load(classId);
        if (fun != null) {
            return fun.apply(p, u);
        }
        return null;
    }

    public static void accept(String id, Object p) {
        String classId = classId(id);
        Consumer fun = (Consumer) load(classId);
        if (fun != null) {
            fun.accept(p);
        }
    }

    public static void accept2(String id, Object p, Object u) {
        String classId = classId(id);
        BiConsumer fun = (BiConsumer) load(classId);
        if (fun != null) {
            fun.accept(p,u);
        }
    }

}
