package org.cc.util;

import org.cc.CCList;
import org.cc.CCMap;
import org.cc.ICCMap;
import org.cc.ICCList;

/**
 *
 * @author william
 */
public class CCPath {
    
     public static Object path(ICCMap jo, String jopath) {
        String[] path = jopath.split(":");
        return path(jo, path);
    }

    public static ICCMap map(ICCMap jo, String jopath) {
        Object ret = path(jo, jopath);
        if (ret instanceof ICCMap) {
            return (ICCMap) ret;
        }
        return null;
    }

    public static ICCList list(ICCMap jo, String jopath) {
        Object ret = path(jo, jopath);
        if (ret instanceof ICCList) {
            return (ICCList) ret;
        } else if (ret instanceof ICCMap) {
            ICCList arr = new CCList();
            arr.add(ret);
            return arr;
        }
        return null;
    }

    private static Object opt(Object m, String k) {
        if (m instanceof ICCMap) {
            return ((ICCMap) m).get(k);
        } else if (m instanceof ICCList) {
            return ((ICCList) m).get(Integer.parseInt(k.trim()));
        }
        return null;
    }

    private static Object path(ICCMap jo, String[] path) {
        Object p = jo;
        for (String key : path) {
            p = opt(p, key);
            if (p == null) {
                break;
            }
        }
        return p;
    }

    public static void set(ICCMap target, String path, Object o) {
        String[] items = path.split(":");
        set(target, items, 0, o);
    }

    private static void set(ICCMap parent, String[] items, int level, Object o) {
        if (level >= (items.length - 1)) {
            parent.put(items[level], o);
        } else {
            String key = items[level];
            ICCMap p = null;
            if (parent.containsKey(key)) {
                p = parent.map(key);
            } else {
                p = new CCMap();
                parent.put(key, p);
            }
            set(p, items, level + 1, o);
        }
    }

    public static void setJA(ICCMap jo, String path, Object o) {
        Object item = path(jo, path);
        if (item instanceof ICCList) {
            ((ICCList) item).add(o);
        } else if (item == null) {
            ICCList ja = new CCList();
            set(jo, path, ja);
            ja.add(o);
        }
    }
}
