package org.cc.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author william
 */
public class JOPath {

    public static Object path(JSONObject jo, String jp) {
        String[] path = jp.split(":");
        return path(jo, path);
    }

    public static JSONObject asJSON(JSONObject jo, String jp) {
        Object ret = path(jo, jp);
        if (ret instanceof JSONObject) {
            return (JSONObject) ret;
        }
        return null;
    }

    public static JSONArray asArray(JSONObject jo, String jp) {
        Object ret = path(jo, jp);
        if (ret instanceof JSONArray) {
            return (JSONArray) ret;
        } else if (ret instanceof JSONObject) {
            JSONArray arr = new JSONArray();
            arr.add(ret);
            return arr;
        }
        return null;
    }

    public static int asInt(JSONObject jo, String jp) {
        Object ret = path(jo, jp);
        return (ret instanceof Number) ? ((Number) ret).intValue() : 0;
    }

    private static Object opt(Object m, String k) {
        if (m instanceof JSONObject) {
            return ((JSONObject) m).opt(k);
        } else if (m instanceof JSONArray) {
            return ((JSONArray) m).get(Integer.parseInt(k.trim()));
        }
        return null;
    }

    private static Object path(JSONObject jo, String[] path) {
        Object p = jo;
        for (String key : path) {
            p = opt(p, key);
            if (p == null) {
                break;
            }
        }
        return p;
    }

    public static void set(JSONObject target, String path, Object o) {
        String[] items = path.split(":");
        set(target, items, 0, o);
    }

    private static void set(JSONObject parent, String[] items, int level, Object o) {
        if (level >= (items.length - 1)) {
            parent.put(items[level], o);
        } else {
            String key = items[level];
            JSONObject p = null;
            if (parent.has(key)) {
                p = parent.optJSONObject(key);
            } else {
                p = new JSONObject();
                parent.put(key, p);
            }
            set(p, items, level + 1, o);
        }
    }

    public static void setJA(JSONObject jo, String path, Object o) {
        Object item = path(jo, path);
        if (item instanceof JSONArray) {
            ((JSONArray) item).put(o);
        } else if (item == null) {
            JSONArray ja = new JSONArray();
            set(jo, path, ja);
            ja.put(o);
        }
    }

    public static void inc(JSONObject jo, String path, int iv) {
        Object item = path(jo, path);
        if (item instanceof Number) {
            set(jo, path, ((Number) item).intValue() + iv);
        } else if (item == null) {
            set(jo, path, iv);
        }
    }

    public static void appendSerial(JSONObject jo, String jp, Object o) {
        Object item = path(jo, jp);
        if (item != null) {
            set(jo, jp, item.toString() + "," + o.toString());
        } else {
            set(jo, jp, o.toString());
        }
    }

    public static List<String> toList(String line) {
        return Arrays.asList(line.split(","));
    }

    public static List<String> toList(JSONArray arr) {
        List<String> ret = new ArrayList<String>();
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                ret.add(arr.optString(i));
            }
        }
        return ret;
    }

}
