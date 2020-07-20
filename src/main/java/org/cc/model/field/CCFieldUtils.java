package org.cc.model.field;

import com.google.common.reflect.ClassPath;
import java.util.HashMap;
import java.util.Map;
import org.cc.CCMap;
import org.cc.IAProxyClass;
import org.cc.ICCMap;
import org.cc.util.CCJSON;
import org.cc.util.CCLogger;

public class CCFieldUtils {

    private static Map<String, Class> _cache;

    private static Object newInstance(String classId) {
        try {
            return Class.forName(classId).newInstance();
        } catch (Exception e) {
            CCLogger.error("Can't newInstance : " + classId);
            return null;
        }
    }

    private static Map<String, Class> cache() {
        if (_cache == null) {
            _cache = new HashMap<String, Class>(32);
            scanPackage(_cache, "org.cc.model.field");
        }
        return _cache;
    }

    private static void scanPackage(Map<String, Class> c, String string) {
        try {
            ClassLoader load = Thread.currentThread().getContextClassLoader();

            ClassPath classpath = ClassPath.from(load);
            classpath.getTopLevelClasses("org.cc.model.field").stream().map((classInfo) -> classInfo.load()).forEach((cls) -> {
                IAProxyClass a = (IAProxyClass) cls.getAnnotation(IAProxyClass.class);
                if (a != null) {
                    c.put(a.id(), cls);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static ICCField newField(String classId) throws Exception {
        Class cls = cache().get("field." + classId);
        cls = (cls == null) ? cache().get("field.obj") : cls;
        return (ICCField) cls.newInstance();
    }

    public static ICCField newInstance(ICCMap cm) throws Exception {
        ICCField fld = newField(cm.asString("dt"));
        if (fld != null) {
            fld.__init__(cm);
        }
        return fld;
    }

    public static ICCMap mix(ICCMap mFields, String line) {
        if (line.charAt(0) == '{') {
            return mix(mFields, CCJSON.loadString(line));
        } else {
            ICCMap ret = new CCMap();
            String[] items = line.split(":");
            ICCMap p = mFields.map(items[0]);
            if (p != null) {
                ret.putAll(p);
            }
            if (items.length > 1) {
                ret.put("alias", items[1]);
            }
            return ret;
        }
    }

    public static ICCMap mix(ICCMap mFields, Object o) {
        if (o instanceof ICCMap) {
            return mix(mFields, (ICCMap) o);
        } else if (o instanceof String) {
            return mix(mFields, (String) o);
        }
        return null;
    }

    public static ICCMap mix(ICCMap mFields, ICCMap m) {
        ICCMap ret = new CCMap();
        ICCMap p = mFields.map(m.asString("id"));
        if (p != null) {
            ret.putAll(p);
        }
        m.forEach((k, v) -> {
            ret.put(k, v);
        });
        return ret;
    }

}
