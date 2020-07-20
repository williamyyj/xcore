package org.cc;

import java.util.Date;
import java.util.Map;
import org.cc.util.CCCast;
import org.cc.util.CCJSON;

/**
 *
 * @author william
 */
public interface ICCMap extends Map<String, Object> {

    default int asInt(String key) {
        return asInt(key, 0);
    }

    default int asInt(String key, int dv) {
        return CCCast._int(get(key), dv);
    }

    default long asLong(String key) {
        return asLong(key, 0L);
    }

    default long asLong(String key, Long dv) {
        return CCCast._long(get(key), dv);
    }

    default double asDouble(String key) {
        return asDouble(key, 0.0);
    }

    default double asDouble(String key, double dv) {
        return CCCast._double(get(key), dv);
    }

    default boolean asBool(String key) {
        return asBool(key, false);
    }

    default boolean asBool(String key, boolean dv) {
        return CCCast._bool(get(key), dv);
    }

    default Date asDate(String key) {
        return CCCast._date(get(key));
    }

    default String asString(String key) {
        return asString(key, "");
    }

    default String asString(String key, String dv) {
        return CCCast._string(get(key), dv);
    }

    default ICCMap map(String key) {
        Object o = get(key);
        return (o instanceof ICCMap) ? (ICCMap) o : null;
    }

    default ICCList list(String key) {
        Object o = get(key);
        return (o instanceof ICCList) ? (ICCList) o : null;
    }

    default String toString(int indent) {
        return CCJSON.toString(this, indent);
    }

    default <T> T as(Class<T> cls, String key) {
        return (T) get(key);
    }

}
