package org.cc.kotlin.json


fun asInt(o: Any?, dv: Int): Int {
    try {
        if (o is Number) {
            return o.toInt();
        } else if (o is String) {
            val str = o.trim { it <= ' ' }
            return if (str.length > 0) str.toInt() else dv
        }
    } catch (e: Exception) {
    }
    return dv
}

fun asLong(o: Any?, dv: Long): Long {
    try {
        if (o is Number) {
            return o.toLong();
        } else if (o is String) {
            val str = o.trim { it <= ' ' }
            return if (str.length > 0) str.toLong() else dv
        }
    } catch (e: Exception) {
    }
    return dv
}

fun asDouble(o: Any?, dv: Double): Double {
    try {
        if (o is Number) {
            return o.toDouble();
        } else if (o is String) {
            val str = o.trim { it <= ' ' }
            return if (str.length > 0) str.toDouble() else dv
        }
    } catch (e: Exception) {
    }
    return dv
}

fun asBoolean(o: Any?, dv: Boolean): Boolean {
    return if (o is Boolean) {
        o
    } else if (o is String) {
        java.lang.Boolean.parseBoolean(o.trim { it <= ' ' })
    } else {
        dv
    }
}

@JvmOverloads
fun asString(o: Any?, dv: String? = ""): String {
    return o?.toString()?.trim { it <= ' ' } ?: dv!!
}