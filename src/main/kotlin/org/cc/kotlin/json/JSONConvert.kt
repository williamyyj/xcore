package org.cc.kotlin.json


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

var sfmt = "yyyyMMdd"
var lfmt = "yyyyMMddHHmmss"
var afmt = "yyyyMMddHHmmssSSS"
var cstfmt = "EEE MMM dd HH:mm:ss zzz yyyy"

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

fun asString(o: Any?, dv: String? = ""): String {
    return o?.toString()?.trim { it <= ' ' } ?: dv!!
}


fun asDate(o: Any?): Date? {
    if (o is Date) {
        return o
    } else if (o is String) {
        return to_date(o)
    }
    return null
}

fun to_date(text: String): Date? {
    if (text.contains("CST ")) {
        return to_cst(text)
    }
    val str = text.replace("[^0-9\\.]+".toRegex(), "")
    val len = str.length
    when (len) {
        8 -> return to_date(sfmt, str)
        14 -> return to_date(lfmt, str)
    }
    return null
}

fun to_date(fmt: String, text: String): Date? {
    val sdf = SimpleDateFormat(fmt)
    try {
        return sdf.parse(text)
    } catch (ex: ParseException) {
        //CCLogger.warn("Can't cast $fmt,$text")
    }
    return null
}

fun to_cst(text: String?): Date? {
    val sdf = SimpleDateFormat(cstfmt, Locale.US)
    try {
        return sdf.parse(text)
    } catch (ex: ParseException) {
        ex.printStackTrace()
    }
    return null
}