package org.cc.kotlin.json

import org.cc.data.CCData
import java.io.File
import kotlin.text.indexOf as indexOf

open class JOBaseTokener {

    // lateinit：延遲初始化
    protected lateinit var data: CharArray
    protected var enc = "UTF-8"
    protected var curr = 0
    protected var line = 1
    protected var pos = 1
    protected var tab = 0
    protected var src: File? = null

    constructor(data: CharArray) {
        this.data = data
    }

    constructor(text: String?) {
        data = text!!.toCharArray()
    }

     constructor(f: File?, enc: String?) {
         try {
             this.data = CCData.loadClob(f, enc)
             src = f
         } catch (ex: Exception) {
             ex.printStackTrace()
         }
     }

    fun length(): Int {
        return data.size
    }

    fun charAt(index: Int): Char {
        return data[index]
    }

    fun subString(start: Int, end: Int): String? {
        return String(data, start, end - start)
    }

    fun m(c: Char): Boolean {
        return data[curr] == c
    }

    fun m(index: Int, text: String): Boolean {
        var idx = index
        val buf = text.toCharArray()
        for (c in buf) {
            if (idx >= data.size || c != data[idx++]) {
                return false
            }
        }
        return true
    }

    fun m(idx: Int, c: Char): Boolean {
        return curr + idx < data.size && c == data[curr + idx]
    }

    fun m(text: String?): Boolean {
        return m(curr, text!!)
    }

    fun mi(index: Int, text: String): Boolean {
        var idx = index
        val buf = text.toCharArray()
        for (c in buf) {
            if (idx >= data.size) {
                return false
            }
            val a = Character.toLowerCase(c)
            val b = Character.toLowerCase(data[idx++])
            if (a != b) {
                return false
            }
        }
        return true
    }

    fun has(idx: Int, text: String): Boolean {
        return text.indexOf(data[curr + idx]) >= 0
    }

    protected fun _next(): Char {
        curr++
        return if (data.size > curr) data[curr] else '\u0000'
    }



    operator fun next(): Char {
        var c = _next()
        pos++
        if (c.toInt() == 9) {
            tab++
        }
        if (c.toInt() == 10 || c.toInt() == 13) {
            line++
            pos = 0
            tab = 0
            if (c.toInt() == 13 && curr + 1 < data.size && data[curr + 1].toInt() == 10) {
                curr++
            }
            c = 10.toChar()
        }
        return c
    }

    protected fun move(offset: Int) {
        curr += offset
        if (curr < data.size) {
            val ch = data[curr]
            if (ch.toInt() == 10 || ch.toInt() == 13) {
                //   linux  , os x
                line++
                pos = 1
                tab = 0
                curr++
                if (ch.toInt() == 13 && curr + 1 < data.size && data[curr + 1].toInt() == 10) {
                    //  m$
                    curr++
                }
            }
        }
    }

    fun tk_string(quote: Char): String {
        val sb = StringBuilder()
        var ch = 0.toChar()
        while (next().also { ch = it }.toInt() != 0 && !m(quote)) {
            if (ch == '\\') {
                ch = next()
                when (ch) {
                    'b' -> sb.append('\b')
                    't' -> sb.append('\t')
                    'n' -> sb.append('\n')
                    'f' -> sb.append('\u000C')
                    'r' -> sb.append('\r')
                    'u' -> {
                        sb.append(subString(curr + 1, curr + 5)!!.toInt(16).toChar())
                        curr += 4
                        pos += 4
                    }
                    '"', '\'', '\\' -> sb.append(ch)
                }
            } else {
                sb.append(ch)
            }
        }
        //    m(quote)  check error
        next() // skip quote
        return sb.toString()
    }

    fun tk_text(pattern: String): String? {
        val start: Int = curr
        while (!m(pattern)) {
            next()
        }
        val text = subString(start, curr)
        if (m(pattern)) {
            curr += pattern.length
        }
        return text
    }

    fun tk_csp() {
        while (isWhiteSpace()) {
            next()
        }
    }

    fun isWhiteSpace(): Boolean {
        if(curr<data.size){

        } else {
            return false;
        }
        var c = data[curr];

        return curr < data.size && data[curr]
            .also { c = it }.toInt() == 9 || c.toInt() == 10 || c.toInt() == 13 || c.toInt() == 32
    }

    fun tk_m(str: String): Boolean {
        if (m(str)) {
            move(str.length)
            return true
        }
        return false
    }

    override fun toString(): String {
        return String.format("[line:%s,pos:%s,ps:%s]:%s", line, pos, curr, data[curr])
    }

}

