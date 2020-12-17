package org.cc.kotlin.json


import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class JONull : Cloneable {

    override fun clone(): Any {
        return this
    }

    override fun equals(o: Any?): Boolean {
        return o == null || o === this
    }

    /**
     * A Null object is equal to the null value and to itself.
     *
     * @return always returns 0.
     */
    override fun hashCode(): Int {
        return 0
    }

    /**
     * Get the "null" string value.
     *
     * @return The string "null".
     */
    override fun toString(): String {
        return "null"
    }
}


class JOMap : HashMap<String?, Any> {

    constructor()

    @Throws(JSONException::class)
    constructor (x: JSONTokener) {
        var c: Char
        var key: String?
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'")
        }
        while (true) {
            c = x.nextClean()
            key = when (c) {
                '\u0000' -> throw x.syntaxError("A JSONObject text must end with '}'")
                '}' -> return
                else -> {
                    x.back()
                    x.nextValue().toString()
                }
            }

            // The key is followed by ':'.
            c = x.nextClean()
            if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key")
            }

            // Use syntaxError(..) to include error location

            // Check if key exists
            if (this.get(key) != null) {
                // key already exists
                throw x.syntaxError("Duplicate key \"$key\"")
            }
            // Only add value if non-null
            val value = x.nextValue()
            this[key] = value
            when (x.nextClean()) {
                ';', ',' -> {
                    if (x.nextClean() == '}') {
                        return
                    }
                    x.back()
                }
                '}' -> return
                else -> throw x.syntaxError("Expected a ',' or '}'")
            }
        }
    }


    fun asInt(id: String): Int {
        return asInt(get(id), 0)
    }

    fun asInt(id: String, dv: Int): Int {
        return asInt(id, dv)
    }

    fun asLong(id: String): Long {
        return asLong(get(id), 0L)
    }

    fun asLong(id: String, dv: Long): Long {
        return asLong(get(id), dv)
    }

    fun asDouble(id: String): Double {
        return asDouble(get(id), 0.0)
    }

    fun asDouble(id: String, dv: Double): Double {
        return asDouble(get(id), dv)
    }

    fun asString(id: String): String {
        return asString(get(id))
    }

    fun asString(id: String, dv: String): String {
        return asString(get(id), dv)
    }

    fun asDate(id: String): Date? {
        return asDate(id, null)
    }

    fun asDate(id: String, dv: Date?): Date? {
        var d = asDate(get(id))
        return when (d) {
            null -> {
                dv
            }
            else -> {
                d
            }
        }
    }

}

class JOArray : ArrayList<Any> {

    constructor()

    @Throws(JSONException::class)
    constructor(x: org.cc.kotlin.json.JSONTokener) {
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JOArray text must start with '['")
        }
        var nextChar = x.nextClean()
        if (nextChar.toInt() == 0) {
            // array is unclosed. No ']' found, instead EOF
            throw x.syntaxError("Expected a ',' or ']'")
        }
        if (nextChar != ']') {
            x.back()
            while (true) {
                if (x.nextClean() == ',') {
                    x.back()
                    this.add(JONull())
                } else {
                    x.back()
                    this.add(x.nextValue())
                }
                when (x.nextClean()) {
                    '\u0000' -> throw x.syntaxError("Expected a ',' or ']'")
                    ',' -> {
                        nextChar = x.nextClean()
                        if (nextChar.toInt() == 0) {
                            // array is unclosed. No ']' found, instead EOF
                            throw x.syntaxError("Expected a ',' or ']'")
                        }
                        if (nextChar == ']') {
                            return
                        }
                        x.back()
                    }
                    ']' -> return
                    else -> throw x.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    fun asInt(id: Int): Int {
        return asInt(get(id), 0)
    }

    fun asInt(id: Int, dv: Int): Int {
        return asInt(get(id), dv)
    }

    fun asLong(id: Int): Long {
        return asLong(get(id), 0L)
    }

    fun asLong(id: Int, dv: Long): Long {
        return asLong(get(id), dv)
    }

    fun asDouble(id: Int): Double {
        return asDouble(get(id), 0.0)
    }

    fun asDouble(id: Int, dv: Double): Double {
        return asDouble(get(id), dv)
    }

    fun asString(id: Int): String {
        return asString(get(id))
    }

    fun asString(id: Int, dv: String): String {
        return asString(get(id), dv)
    }

    fun asDate(id: Int): Date? {
        return asDate(id, null)
    }

    fun asDate(id: Int, dv: Date?): Date? {
        var d = asDate(get(id))
        return when (d) {
            null -> {
                dv
            }
            else -> {
                d
            }
        }
    }

}

fun strToValue(string: String): Any {
    if ("" == string) {
        return string
    }

    // check JSON key words true/false/null
    if ("true".equals(string, ignoreCase = true)) {
        return java.lang.Boolean.TRUE
    }
    if ("false".equals(string, ignoreCase = true)) {
        return java.lang.Boolean.FALSE
    }
    if ("null".equals(string, ignoreCase = true)) {
        return JONull()
    }

    /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */
    val initial = string[0]
    if (initial in '0'..'9' || initial == '-') {
        return strToNumber(string)!!
    } else {
        return string
    }
}

@Throws(NumberFormatException::class)
fun strToNumber(v: String): Number? {
    val initial = v[0]
    if (initial >= '0' && initial <= '9' || initial == '-') {
        // decimal representation
        if (isDecimalNotation(v)) {
            // Use a BigDecimal all the time so we keep the original
            // representation. BigDecimal doesn't support -0.0, ensure we
            // keep that by forcing a decimal.
            return try {
                val bd = BigDecimal(v)
                if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) {
                    java.lang.Double.valueOf(-0.0)
                } else bd
            } catch (retryAsDouble: NumberFormatException) {
                // this is to support "Hex Floats" like this: 0x1.0P-1074
                try {
                    val d = java.lang.Double.valueOf(v)
                    if (d.isNaN() || d.isInfinite()) {
                        throw NumberFormatException("val [$v] is not a valid number.")
                    }
                    d
                } catch (ignore: NumberFormatException) {
                    throw NumberFormatException("val [$v] is not a valid number.")
                }
            }
        }
        // block items like 00 01 etc. Java number parsers treat these as Octal.
        if (initial == '0' && v.length > 1) {
            val at1 = v[1]
            if (at1 >= '0' && at1 <= '9') {
                throw NumberFormatException("val [$v] is not a valid number.")
            }
        } else if (initial == '-' && v.length > 2) {
            val at1 = v[1]
            val at2 = v[2]
            if (at1 == '0' && at2 >= '0' && at2 <= '9') {
                throw NumberFormatException("val [$v] is not a valid number.")
            }
        }
        // integer representation.
        // This will narrow any values to the smallest reasonable Object representation
        // (Integer, Long, or BigInteger)

        // BigInteger down conversion: We use a similar bitLenth compare as
        // BigInteger#intValueExact uses. Increases GC, but objects hold
        // only what they need. i.e. Less runtime overhead if the value is
        // long lived.
        val bi = BigInteger(v)
        if (bi.bitLength() <= 31) {
            return Integer.valueOf(bi.toInt())
        }
        return if (bi.bitLength() <= 63) {
            java.lang.Long.valueOf(bi.toLong())
        } else bi
    }
    throw NumberFormatException("val [$v] is not a valid number.")
}


fun isDecimalNotation(v: String): Boolean {
    return v.indexOf('.') > -1 || v.indexOf('e') > -1 || v.indexOf('E') > -1 || "-0" == v
}

fun loadJO(text: String): JOMap {
    return if (text[0] == '[') {
        //JOTokener(text).jo_obj()
        JOMap(JSONTokener(text))
    } else JOMap(JSONTokener("{" + text + "}"))
}

fun loadJA(text: String): JOArray {
    return if (text[0] == '[') {
        //JOTokener(text).jo_list()
        JOArray(JSONTokener(text))
    } else JOArray(JSONTokener("[" + text + "]"))
}