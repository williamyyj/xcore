package org.cc.kotlin.json

import org.cc.util.CCJsonParser
import org.cc.util.CCLogger
import java.io.File

class JOTokener : JOBaseTokener{

    val idPattern = " :()[]{}\\\"'"
    val valuePattern = ",)]}>"
    val wordPattern = " ,)]}>"
    val opPattern = " ,)]}$"
    val tk_lstr_start = "$\"" // $".....""..."


    constructor(f: File?, enc: String?) : super(f, enc) {}
    constructor(text: String?) : super(text) {}


    protected fun tk_m(c: Char): Boolean {
        if (data.size > curr && data[curr] == c) {
            move(1)
            return true
        }
        return false
    }

    @Throws(Exception::class)
    fun jo_obj(): JOMap {
        if (!tk_m('{')) {
            throw error("Parser  exception '{' ")
        }
        val jo: JOMap = JOMap()
        while (true) {
            tk_csp()
            if (tk_m('}')) {
                return jo
            }
            val key: String = jo_next(idPattern).toString()
            tk_csp()
            if (!tk_m(':')) {
                throw error("Parser  expected ':' ")
            }
            tk_csp()
            jo_next(valuePattern)?.let { jo.put(key, it) }
            tk_csp()
            if (tk_m('}')) {
                return jo
            }
            if (!tk_m(',')) {
                throw error("Parser  expected ( ',' | '}' ) ")
            }
        }
    }


    @Throws(Exception::class)
     fun jo_list(): JOArray {
        if (!tk_m('[')) {
            throw error("ICCList expected '['")
        }
        val ja: JOArray = JOArray()
        while (true) {
            tk_csp()
            if (tk_m(']')) {
                return ja
            }
            jo_next(valuePattern)?.let { ja.add(it) }
            tk_csp()
            if (tk_m(']')) {
                return ja
            }
            if (!tk_m(',')) {
                throw error("ICCList expected  (','|']') ")
            }
        }
    }

    @Throws(Exception::class)
    fun jo_next(pattern: String): Any? {
        if (m('\'') || m('"')) {
            return jo_string(data[curr])
        } else if (m('{')) {
            return jo_obj()
        } else if (m('[')) {
            return jo_list()
        }
        return jo_value(jo_word(pattern))
    }

    fun jo_word(pattern: String): String {
        val start: Int = curr
        // 合理換行是區原素
        while (data[curr].toInt() >= 32 && ! has(0, pattern) ) {
            next()
        }
        return subString(start, curr)!!.trim { it <= ' ' }
    }

    fun jo_value(s: String): Any? {
        if (s == "") {
            return s
        }
        if (s.equals("true", ignoreCase = true)) {
            return true
        }
        if (s.equals("false", ignoreCase = true)) {
            return false
        }
        if (s.equals("null", ignoreCase = true)) {
            return null
        }
        val b = s[0]
        if ( b in '0'..'9' || b == '.' || b == '-' || b == '+') {
            if (b == '0' && s.length > 2 && (s[1] == 'x' || s[1] == 'X')
            ) {
                try {
                    return s.substring(2).toInt(16)
                } catch (ignore: Exception) {
                }
            }
            try {
                return if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1
                ) {
                    s.toDouble()
                } else {
                    val myLong: Long = 5; //val myLong: Long = s
                    if (myLong == myLong.toInt().toLong()) {
                        myLong.toInt()
                    } else {
                        myLong
                    }
                }
            } catch (ignore: Exception) {
                //ignore.printStackTrace();
            }
        }
        return s
    }

    fun jo_string(quote: Char): String {
        return tk_string(quote)
    }

    fun error(error: String?): Throwable {
        val fmt = "Error (%s)%s in line:%s pos:%s "
        val message = String.format(fmt, src, error, line, pos)
        CCLogger.debug(message)
        return Exception(message)
    }
}