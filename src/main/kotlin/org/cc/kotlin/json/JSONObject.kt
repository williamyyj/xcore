package org.cc.kotlin.json

import org.json.*
import org.json.JSONTokener
import java.io.Closeable
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
/**
 * A JSONObject is an unordered collection of name/value pairs. Its external
 * form is a string wrapped in curly braces with colons between the names and
 * values, and commas between the values and names. The internal form is an
 * object having `get` and `opt` methods for accessing
 * the values by name, and `put` methods for adding or replacing
 * values by name. The values can be any of these types: `Boolean`,
 * `JSONArray`, `JSONObject`, `Number`,
 * `String`, or the `JSONObject.NULL` object. A
 * JSONObject constructor can be used to convert an external form JSON text
 * into an internal form whose values can be retrieved with the
 * `get` and `opt` methods, or to convert values into a
 * JSON text using the `put` and `toString` methods. A
 * `get` method returns a value if one can be found, and throws an
 * exception if one cannot be found. An `opt` method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 *
 *
 * The generic `get()` and `opt()` methods return an
 * object, which you can cast or query for type. There are also typed
 * `get` and `opt` methods that do type checking and type
 * coercion for you. The opt methods differ from the get methods in that they
 * do not throw. Instead, they return a specified value, such as null.
 *
 *
 * The `put` methods add or replace values in an object. For
 * example,
 *
 * <pre>
 * myString = new JSONObject()
 * .put(&quot;JSON&quot;, &quot;Hello, World!&quot;).toString();
</pre> *
 *
 * produces the string `{"JSON": "Hello, World"}`.
 *
 *
 * The texts produced by the `toString` methods strictly conform to
 * the JSON syntax rules. The constructors are more forgiving in the texts they
 * will accept:
 *
 *  * An extra `,`&nbsp;<small>(comma)</small> may appear just
 * before the closing brace.
 *  * Strings may be quoted with `'`&nbsp;<small>(single
 * quote)</small>.
 *  * Strings do not need to be quoted at all if they do not begin with a
 * quote or single quote, and if they do not contain leading or trailing
 * spaces, and if they do not contain any of these characters:
 * `{ } [ ] / \ : , #` and if they do not look like numbers and
 * if they are not the reserved words `true`, `false`,
 * or `null`.
 *
 *
 * @author JSON.org
 * @version 2016-08-15
 */
class JSONObject {
    /**
     * JSONObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
    private class Null : Cloneable {
        /**
         * There is only intended to be a single instance of the NULL object,
         * so the clone method returns itself.
         *
         * @return NULL.
         */
        override fun clone(): Any {
            return this
        }

        /**
         * A Null object is equal to the null value and to itself.
         *
         * @param object
         * An object to test for nullness.
         * @return true if the object parameter is the JSONObject.NULL object or
         * null.
         */
        override fun equals(`object`: Any?): Boolean {
            return `object` == null || `object` === this
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

    /**
     * The map where the JSONObject's properties are kept.
     */
    private val map: MutableMap<String?, Any?>

    /**
     * Construct an empty JSONObject.
     */
    constructor() {
        // HashMap is used on purpose to ensure that elements are unordered by 
        // the specification.
        // JSON tends to be a portable transfer format to allows the container 
        // implementations to rearrange their items for a faster element 
        // retrieval based on associative access.
        // Therefore, an implementation mustn't rely on the order of the item.
        map = HashMap()
    }

    /**
     * Construct a JSONObject from a subset of another JSONObject. An array of
     * strings is used to identify the keys that should be copied. Missing keys
     * are ignored.
     *
     * @param jo
     * A JSONObject.
     * @param names
     * An array of strings.
     */
    constructor(jo: JSONObject, vararg names: String?) : this(names.size) {
        var i = 0
        while (i < names.size) {
            try {
                putOnce(names[i], jo.opt(names[i]))
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    /**
     * Construct a JSONObject from a JSONTokener.
     *
     * @param x
     * A JSONTokener object containing the source string.
     * @throws JSONException
     * If there is a syntax error in the source string or a
     * duplicated key.
     */
    constructor(x: JSONTokener) : this() {
        var c: Char
        var key: String
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
            if (key != null) {
                // Check if key exists
                if (opt(key) != null) {
                    // key already exists
                    throw x.syntaxError("Duplicate key \"$key\"")
                }
                // Only add value if non-null
                val value = x.nextValue()
                if (value != null) {
                    this.put(key, value)
                }
            }
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

    /**
     * Construct a JSONObject from a Map.
     *
     * @param m
     * A map object that can be used to initialize the contents of
     * the JSONObject.
     * @throws JSONException
     * If a value in the map is non-finite number.
     * @throws NullPointerException
     * If a key in the map is `null`
     */
    constructor(m: Map<*, *>?) {
        if (m == null) {
            map = HashMap()
        } else {
            map = HashMap(m.size)
            for ((key, value) in m) {
                if (key == null) {
                    throw NullPointerException("Null key.")
                }
                if (value != null) {
                    map[key.toString()] = wrap(value)
                }
            }
        }
    }

    /**
     * Construct a JSONObject from an Object using bean getters. It reflects on
     * all of the public methods of the object. For each of the methods with no
     * parameters and a name starting with `"get"` or
     * `"is"` followed by an uppercase letter, the method is invoked,
     * and a key and the value returned from the getter method are put into the
     * new JSONObject.
     *
     *
     * The key is formed by removing the `"get"` or `"is"`
     * prefix. If the second remaining character is not upper case, then the
     * first character is converted to lower case.
     *
     *
     * Methods that are `static`, return `void`,
     * have parameters, or are "bridge" methods, are ignored.
     *
     *
     * For example, if an object has a method named `"getName"`, and
     * if the result of calling `object.getName()` is
     * `"Larry Fine"`, then the JSONObject will contain
     * `"name": "Larry Fine"`.
     *
     *
     * The [JSONPropertyName] annotation can be used on a bean getter to
     * override key name used in the JSONObject. For example, using the object
     * above with the `getName` method, if we annotated it with:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * public String getName() { return this.name; }
    </pre> *
     * The resulting JSON object would contain `"FullName": "Larry Fine"`
     *
     *
     * Similarly, the [JSONPropertyName] annotation can be used on non-
     * `get` and `is` methods. We can also override key
     * name used in the JSONObject as seen below even though the field would normally
     * be ignored:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * public String fullName() { return this.name; }
    </pre> *
     * The resulting JSON object would contain `"FullName": "Larry Fine"`
     *
     *
     * The [JSONPropertyIgnore] annotation can be used to force the bean property
     * to not be serialized into JSON. If both [JSONPropertyIgnore] and
     * [JSONPropertyName] are defined on the same method, a depth comparison is
     * performed and the one closest to the concrete class being serialized is used.
     * If both annotations are at the same level, then the [JSONPropertyIgnore]
     * annotation takes precedent and the field is not serialized.
     * For example, the following declaration would prevent the `getName`
     * method from being serialized:
     * <pre>
     * &#64;JSONPropertyName("FullName")
     * &#64;JSONPropertyIgnore
     * public String getName() { return this.name; }
    </pre> *
     *
     *
     *
     * @param bean
     * An object that has getter methods that should be used to make
     * a JSONObject.
     */
    constructor(bean: Any) : this() {
        populateMap(bean)
    }

    /**
     * Construct a JSONObject from an Object, using reflection to find the
     * public members. The resulting JSONObject's keys will be the strings from
     * the names array, and the values will be the field values associated with
     * those keys in the object. If a key is not found or not visible, then it
     * will not be copied into the new JSONObject.
     *
     * @param object
     * An object that has fields that should be used to make a
     * JSONObject.
     * @param names
     * An array of strings, the names of the fields to be obtained
     * from the object.
     */
    constructor(`object`: Any, vararg names: String?) : this(names.size) {
        val c: Class<*> = `object`.javaClass
        var i = 0
        while (i < names.size) {
            val name = names[i]
            try {
                putOpt(name, c.getField(name)[`object`])
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    /**
     * Construct a JSONObject from a source JSON text string. This is the most
     * commonly used JSONObject constructor.
     *
     * @param source
     * A string beginning with `{`&nbsp;<small>(left
     * brace)</small> and ending with `}`
     * &nbsp;<small>(right brace)</small>.
     * @exception JSONException
     * If there is a syntax error in the source string or a
     * duplicated key.
     */
    constructor(source: String?) : this(JSONTokener(source)) {}

    /**
     * Construct a JSONObject from a ResourceBundle.
     *
     * @param baseName
     * The ResourceBundle base name.
     * @param locale
     * The Locale to load the ResourceBundle for.
     * @throws JSONException
     * If any JSONExceptions are detected.
     */
    constructor(baseName: String?, locale: Locale?) : this() {
        val bundle = ResourceBundle.getBundle(
            baseName, locale,
            Thread.currentThread().contextClassLoader
        )

// Iterate through the keys in the bundle.
        val keys = bundle.keys
        while (keys.hasMoreElements()) {
            val key: Any? = keys.nextElement()
            if (key != null) {

// Go through the path, ensuring that there is a nested JSONObject for each
// segment except the last. Add the value using the last segment's name into
// the deepest nested JSONObject.
                val path = (key as String).split("\\.").toTypedArray()
                val last = path.size - 1
                var target = this
                var i = 0
                while (i < last) {
                    val segment = path[i]
                    var nextTarget = target.optJSONObject(segment)
                    if (nextTarget == null) {
                        nextTarget = JSONObject()
                        target.put(segment, nextTarget)
                    }
                    target = nextTarget
                    i += 1
                }
                target.put(path[last], bundle.getString(key as String?))
            }
        }
    }

    /**
     * Constructor to specify an initial capacity of the internal map. Useful for library
     * internal calls where we know, or at least can best guess, how big this JSONObject
     * will be.
     *
     * @param initialCapacity initial capacity of the internal map.
     */
    protected constructor(initialCapacity: Int) {
        map = HashMap(initialCapacity)
    }

    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a JSONArray
     * is stored under the key to hold all of the accumulated values. If there
     * is already a JSONArray, then the new value is appended to it. In
     * contrast, the put method replaces the previous value.
     *
     * If only one value is accumulated that is not a JSONArray, then the result
     * will be the same as using put. But if multiple values are accumulated,
     * then the result will be like append.
     *
     * @param key
     * A key string.
     * @param value
     * An object to be accumulated under the key.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun accumulate(key: String?, value: Any?): JSONObject {
        testValidity(value)
        val `object` = opt(key)
        if (`object` == null) {
            this.put(
                key,
                if (value is JSONArray) JSONArray().put(value) else value
            )
        } else if (`object` is JSONArray) {
            `object`.put(value)
        } else {
            this.put(key, JSONArray().put(`object`).put(value))
        }
        return this
    }

    /**
     * Append values to the array under a key. If the key does not exist in the
     * JSONObject, then the key is put in the JSONObject with its value being a
     * JSONArray containing the value parameter. If the key was already
     * associated with a JSONArray, then the value parameter is appended to it.
     *
     * @param key
     * A key string.
     * @param value
     * An object to be accumulated under the key.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number or if the current value associated with
     * the key is not a JSONArray.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun append(key: String?, value: Any?): JSONObject {
        testValidity(value)
        val `object` = opt(key)
        if (`object` == null) {
            this.put(key, JSONArray().put(value))
        } else if (`object` is JSONArray) {
            this.put(key, `object`.put(value))
        } else {
            throw wrongValueFormatException(key, "JSONArray", null, null)
        }
        return this
    }

    /**
     * Get the value object associated with a key.
     *
     * @param key
     * A key string.
     * @return The object associated with the key.
     * @throws JSONException
     * if the key is not found.
     */
    @Throws(JSONException::class)
    operator fun get(key: String?): Any {
        if (key == null) {
            throw JSONException("Null key.")
        }
        return opt(key) ?: throw JSONException("JSONObject[" + quote(key) + "] not found.")
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     * Enum Type
     * @param clazz
     * The type of enum to retrieve.
     * @param key
     * A key string.
     * @return The enum value associated with the key
     * @throws JSONException
     * if the key is not found or if the value cannot be converted
     * to an enum.
    </E> */
    @Throws(JSONException::class)
    fun <E : Enum<E>?> getEnum(clazz: Class<E>, key: String?): E {
        return optEnum(clazz, key)
            ?: // JSONException should really take a throwable argument.
            // If it did, I would re-implement this with the Enum.valueOf
            // method and place any thrown exception in the JSONException
            throw wrongValueFormatException(
                key,
                "enum of type " + quote(clazz.simpleName),
                null
            )
    }

    /**
     * Get the boolean value associated with a key.
     *
     * @param key
     * A key string.
     * @return The truth.
     * @throws JSONException
     * if the value is not a Boolean or the String "true" or
     * "false".
     */
    @Throws(JSONException::class)
    fun getBoolean(key: String?): Boolean {
        val `object` = this[key]
        if (`object` == java.lang.Boolean.FALSE || `object` is String && `object`
                .equals("false", ignoreCase = true)
        ) {
            return false
        } else if (`object` == java.lang.Boolean.TRUE || `object` is String && `object`
                .equals("true", ignoreCase = true)
        ) {
            return true
        }
        throw wrongValueFormatException(key, "Boolean", null)
    }

    /**
     * Get the BigInteger value associated with a key.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JSONException
     * if the key is not found or if the value cannot
     * be converted to BigInteger.
     */
    @Throws(JSONException::class)
    fun getBigInteger(key: String?): BigInteger {
        val `object` = this[key]
        val ret = objectToBigInteger(`object`, null)
        if (ret != null) {
            return ret
        }
        throw wrongValueFormatException(key, "BigInteger", `object`, null)
    }

    /**
     * Get the BigDecimal value associated with a key. If the value is float or
     * double, the the [BigDecimal.BigDecimal] constructor will
     * be used. See notes on the constructor for conversion issues that may
     * arise.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JSONException
     * if the key is not found or if the value
     * cannot be converted to BigDecimal.
     */
    @Throws(JSONException::class)
    fun getBigDecimal(key: String?): BigDecimal {
        val `object` = this[key]
        val ret = objectToBigDecimal(`object`, null)
        if (ret != null) {
            return ret
        }
        throw wrongValueFormatException(key, "BigDecimal", `object`, null)
    }

    /**
     * Get the double value associated with a key.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JSONException
     * if the key is not found or if the value is not a Number
     * object and cannot be converted to a number.
     */
    @Throws(JSONException::class)
    fun getDouble(key: String?): Double {
        val `object` = this[key]
        return if (`object` is Number) {
            `object`.doubleValue()
        } else try {
            `object`.toString().toDouble()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "double", e)
        }
    }

    /**
     * Get the float value associated with a key.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JSONException
     * if the key is not found or if the value is not a Number
     * object and cannot be converted to a number.
     */
    @Throws(JSONException::class)
    fun getFloat(key: String?): Float {
        val `object` = this[key]
        return if (`object` is Number) {
            `object`.floatValue()
        } else try {
            `object`.toString().toFloat()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "float", e)
        }
    }

    /**
     * Get the Number value associated with a key.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JSONException
     * if the key is not found or if the value is not a Number
     * object and cannot be converted to a number.
     */
    @Throws(JSONException::class)
    fun getNumber(key: String?): Number {
        val `object` = this[key]
        return try {
            if (`object` is Number) {
                `object`
            } else stringToNumber(`object`.toString())
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "number", e)
        }
    }

    /**
     * Get the int value associated with a key.
     *
     * @param key
     * A key string.
     * @return The integer value.
     * @throws JSONException
     * if the key is not found or if the value cannot be converted
     * to an integer.
     */
    @Throws(JSONException::class)
    fun getInt(key: String?): Int {
        val `object` = this[key]
        return if (`object` is Number) {
            `object`.intValue()
        } else try {
            `object`.toString().toInt()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "int", e)
        }
    }

    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key
     * A key string.
     * @return A JSONArray which is the value.
     * @throws JSONException
     * if the key is not found or if the value is not a JSONArray.
     */
    @Throws(JSONException::class)
    fun getJSONArray(key: String?): JSONArray {
        val `object` = this[key]
        if (`object` is JSONArray) {
            return `object`
        }
        throw wrongValueFormatException(key, "JSONArray", null)
    }

    /**
     * Get the JSONObject value associated with a key.
     *
     * @param key
     * A key string.
     * @return A JSONObject which is the value.
     * @throws JSONException
     * if the key is not found or if the value is not a JSONObject.
     */
    @Throws(JSONException::class)
    fun getJSONObject(key: String?): JSONObject {
        val `object` = this[key]
        if (`object` is JSONObject) {
            return `object`
        }
        throw wrongValueFormatException(key, "JSONObject", null)
    }

    /**
     * Get the long value associated with a key.
     *
     * @param key
     * A key string.
     * @return The long value.
     * @throws JSONException
     * if the key is not found or if the value cannot be converted
     * to a long.
     */
    @Throws(JSONException::class)
    fun getLong(key: String?): Long {
        val `object` = this[key]
        return if (`object` is Number) {
            `object`.longValue()
        } else try {
            `object`.toString().toLong()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "long", e)
        }
    }

    /**
     * Get the string associated with a key.
     *
     * @param key
     * A key string.
     * @return A string which is the value.
     * @throws JSONException
     * if there is no string value for the key.
     */
    @Throws(JSONException::class)
    fun getString(key: String?): String {
        val `object` = this[key]
        if (`object` is String) {
            return `object`
        }
        throw wrongValueFormatException(key, "string", null)
    }

    /**
     * Determine if the JSONObject contains a specific key.
     *
     * @param key
     * A key string.
     * @return true if the key exists in the JSONObject.
     */
    fun has(key: String?): Boolean {
        return map.containsKey(key)
    }

    /**
     * Increment a property of a JSONObject. If there is no such property,
     * create one with a value of 1 (Integer). If there is such a property, and if it is
     * an Integer, Long, Double, Float, BigInteger, or BigDecimal then add one to it.
     * No overflow bounds checking is performed, so callers should initialize the key
     * prior to this call with an appropriate type that can handle the maximum expected
     * value.
     *
     * @param key
     * A key string.
     * @return this.
     * @throws JSONException
     * If there is already a property with this name that is not an
     * Integer, Long, Double, or Float.
     */
    @Throws(JSONException::class)
    fun increment(key: String?): JSONObject {
        val value = opt(key)
        if (value == null) {
            this.put(key, 1)
        } else if (value is Int) {
            this.put(key, value.toInt() + 1)
        } else if (value is Long) {
            this.put(key, value.toLong() + 1L)
        } else if (value is BigInteger) {
            this.put(key, value.add(BigInteger.ONE))
        } else if (value is Float) {
            this.put(key, value.toFloat() + 1.0f)
        } else if (value is Double) {
            this.put(key, value.toDouble() + 1.0)
        } else if (value is BigDecimal) {
            this.put(key, value.add(BigDecimal.ONE))
        } else {
            throw JSONException("Unable to increment [" + quote(key) + "].")
        }
        return this
    }

    /**
     * Determine if the value associated with the key is `null` or if there is no
     * value.
     *
     * @param key
     * A key string.
     * @return true if there is no value associated with the key or if the value
     * is the JSONObject.NULL object.
     */
    fun isNull(key: String?): Boolean {
        return NULL == opt(key)
    }

    /**
     * Get an enumeration of the keys of the JSONObject. Modifying this key Set will also
     * modify the JSONObject. Use with caution.
     *
     * @see Set.iterator
     * @return An iterator of the keys.
     */
    fun keys(): Iterator<String?> {
        return keySet().iterator()
    }

    /**
     * Get a set of keys of the JSONObject. Modifying this key Set will also modify the
     * JSONObject. Use with caution.
     *
     * @see Map.keySet
     * @return A keySet.
     */
    fun keySet(): Set<String?> {
        return map.keys
    }

    /**
     * Get a set of entries of the JSONObject. These are raw values and may not
     * match what is returned by the JSONObject get* and opt* functions. Modifying
     * the returned EntrySet or the Entry objects contained therein will modify the
     * backing JSONObject. This does not return a clone or a read-only view.
     *
     * Use with caution.
     *
     * @see Map.entrySet
     * @return An Entry Set
     */
    protected fun entrySet(): Set<Map.Entry<String?, Any?>> {
        return map.entries
    }

    /**
     * Get the number of keys stored in the JSONObject.
     *
     * @return The number of keys in the JSONObject.
     */
    fun length(): Int {
        return map.size
    }

    /**
     * Check if JSONObject is empty.
     *
     * @return true if JSONObject is empty, otherwise false.
     */
    val isEmpty: Boolean
        get() = map.isEmpty()

    /**
     * Produce a JSONArray containing the names of the elements of this
     * JSONObject.
     *
     * @return A JSONArray containing the key strings, or null if the JSONObject
     * is empty.
     */
    fun names(): JSONArray? {
        return if (map.isEmpty()) {
            null
        } else JSONArray(map.keys)
    }

    /**
     * Get an optional value associated with a key.
     *
     * @param key
     * A key string.
     * @return An object which is the value, or null if there is no value.
     */
    fun opt(key: String?): Any? {
        return if (key == null) null else map[key]
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     * Enum Type
     * @param clazz
     * The type of enum to retrieve.
     * @param key
     * A key string.
     * @return The enum value associated with the key or null if not found
    </E> */
    fun <E : Enum<E>?> optEnum(clazz: Class<E>, key: String?): E {
        return this.optEnum(clazz, key, null)
    }

    /**
     * Get the enum value associated with a key.
     *
     * @param <E>
     * Enum Type
     * @param clazz
     * The type of enum to retrieve.
     * @param key
     * A key string.
     * @param defaultValue
     * The default in case the value is not found
     * @return The enum value associated with the key or defaultValue
     * if the value is not found or cannot be assigned to `clazz`
    </E> */
    fun <E : Enum<E>?> optEnum(clazz: Class<E>, key: String?, defaultValue: E?): E? {
        return try {
            val `val` = opt(key)
            if (NULL == `val`) {
                return defaultValue
            }
            if (clazz.isAssignableFrom(`val`.javaClass)) {
                // we just checked it!
                return `val` as E?
            }
            java.lang.Enum.valueOf(clazz, `val`.toString())
        } catch (e: IllegalArgumentException) {
            defaultValue
        } catch (e: NullPointerException) {
            defaultValue
        }
    }
    /**
     * Get an optional boolean associated with a key. It returns the
     * defaultValue if there is no such key, or if it is not a Boolean or the
     * String "true" or "false" (case insensitive).
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return The truth.
     */
    /**
     * Get an optional boolean associated with a key. It returns false if there
     * is no such key, or if the value is not Boolean.TRUE or the String "true".
     *
     * @param key
     * A key string.
     * @return The truth.
     */
    @JvmOverloads
    fun optBoolean(key: String?, defaultValue: Boolean = false): Boolean {
        val `val` = opt(key)
        if (NULL == `val`) {
            return defaultValue
        }
        return if (`val` is Boolean) {
            `val`.toBoolean()
        } else try {
            // we'll use the get anyway because it does string conversion.
            getBoolean(key)
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * Get an optional BigDecimal associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number. If the value
     * is float or double, then the [BigDecimal.BigDecimal]
     * constructor will be used. See notes on the constructor for conversion
     * issues that may arise.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    fun optBigDecimal(key: String?, defaultValue: BigDecimal?): BigDecimal? {
        val `val` = opt(key)
        return objectToBigDecimal(`val`, defaultValue)
    }

    /**
     * Get an optional BigInteger associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    fun optBigInteger(key: String?, defaultValue: BigInteger?): BigInteger? {
        val `val` = opt(key)
        return objectToBigInteger(`val`, defaultValue)
    }
    /**
     * Get an optional double associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    /**
     * Get an optional double associated with a key, or NaN if there is no such
     * key or if its value is not a number. If the value is a string, an attempt
     * will be made to evaluate it as a number.
     *
     * @param key
     * A string which is the key.
     * @return An object which is the value.
     */
    @JvmOverloads
    fun optDouble(key: String?, defaultValue: Double = Double.NaN): Double {
        val `val` = optNumber(key) ?: return defaultValue
        // if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
        // return defaultValue;
        // }
        return `val`.doubleValue()
    }
    /**
     * Get the optional double value associated with an index. The defaultValue
     * is returned if there is no value for the index, or if the value is not a
     * number and cannot be converted to a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default value.
     * @return The value.
     */
    /**
     * Get the optional double value associated with an index. NaN is returned
     * if there is no value for the index, or if the value is not a number and
     * cannot be converted to a number.
     *
     * @param key
     * A key string.
     * @return The value.
     */
    @JvmOverloads
    fun optFloat(key: String?, defaultValue: Float = Float.NaN): Float {
        val `val` = optNumber(key) ?: return defaultValue
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return defaultValue;
        // }
        return `val`.floatValue()
    }
    /**
     * Get an optional int value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    /**
     * Get an optional int value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @return An object which is the value.
     */
    @JvmOverloads
    fun optInt(key: String?, defaultValue: Int = 0): Int {
        val `val` = optNumber(key, null) ?: return defaultValue
        return `val`.intValue()
    }

    /**
     * Get an optional JSONArray associated with a key. It returns null if there
     * is no such key, or if its value is not a JSONArray.
     *
     * @param key
     * A key string.
     * @return A JSONArray which is the value.
     */
    fun optJSONArray(key: String?): JSONArray? {
        val o = opt(key)
        return if (o is JSONArray) o else null
    }

    /**
     * Get an optional JSONObject associated with a key. It returns null if
     * there is no such key, or if its value is not a JSONObject.
     *
     * @param key
     * A key string.
     * @return A JSONObject which is the value.
     */
    fun optJSONObject(key: String?): JSONObject? {
        val `object` = opt(key)
        return if (`object` is JSONObject) `object` else null
    }
    /**
     * Get an optional long value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    /**
     * Get an optional long value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @return An object which is the value.
     */
    @JvmOverloads
    fun optLong(key: String?, defaultValue: Long = 0): Long {
        val `val` = optNumber(key, null) ?: return defaultValue
        return `val`.longValue()
    }
    /**
     * Get an optional [Number] value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number. This method
     * would be used in cases where type coercion of the number value is unwanted.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    /**
     * Get an optional [Number] value associated with a key, or `null`
     * if there is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number ([BigDecimal]). This method
     * would be used in cases where type coercion of the number value is unwanted.
     *
     * @param key
     * A key string.
     * @return An object which is the value.
     */
    @JvmOverloads
    fun optNumber(key: String?, defaultValue: Number? = null): Number? {
        val `val` = opt(key)
        if (NULL == `val`) {
            return defaultValue
        }
        return if (`val` is Number) {
            `val`
        } else try {
            stringToNumber(`val`.toString())
        } catch (e: Exception) {
            defaultValue
        }
    }
    /**
     * Get an optional string associated with a key. It returns the defaultValue
     * if there is no such key.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return A string which is the value.
     */
    /**
     * Get an optional string associated with a key. It returns an empty string
     * if there is no such key. If the value is not a string and is not null,
     * then it is converted to a string.
     *
     * @param key
     * A key string.
     * @return A string which is the value.
     */
    @JvmOverloads
    fun optString(key: String?, defaultValue: String = ""): String {
        val `object` = opt(key)
        return if (NULL == `object`) defaultValue else `object`.toString()
    }

    /**
     * Populates the internal map of the JSONObject with the bean properties. The
     * bean can not be recursive.
     *
     * @see JSONObject.JSONObject
     * @param bean
     * the bean
     */
    private fun populateMap(bean: Any) {
        val klass: Class<*> = bean.javaClass

        // If klass is a System class then set includeSuperClass to false.
        val includeSuperClass = klass.classLoader != null
        val methods =
            if (includeSuperClass) klass.methods else klass.declaredMethods
        for (method in methods) {
            val modifiers = method.modifiers
            if (Modifier.isPublic(modifiers)
                && !Modifier.isStatic(modifiers)
                && method.parameterTypes.size == 0 && !method.isBridge
                && method.returnType != Void.TYPE && isValidMethodName(method.name)
            ) {
                val key = getKeyNameFromMethod(method)
                if (key != null && !key.isEmpty()) {
                    try {
                        val result = method.invoke(bean)
                        if (result != null) {
                            map[key] = wrap(result)
                            // we don't use the result anywhere outside of wrap
                            // if it's a resource we should be sure to close it
                            // after calling toString
                            if (result is Closeable) {
                                try {
                                    result.close()
                                } catch (ignore: IOException) {
                                }
                            }
                        }
                    } catch (ignore: IllegalAccessException) {
                    } catch (ignore: IllegalArgumentException) {
                    } catch (ignore: InvocationTargetException) {
                    }
                }
            }
        }
    }

    /**
     * Put a key/boolean pair in the JSONObject.
     *
     * @param key
     * A key string.
     * @param value
     * A boolean which is the value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Boolean): JSONObject {
        return this.put(key, if (value) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE)
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONArray which is produced from a Collection.
     *
     * @param key
     * A key string.
     * @param value
     * A Collection value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Collection<*>?): JSONObject {
        return this.put(key, JSONArray(value))
    }

    /**
     * Put a key/double pair in the JSONObject.
     *
     * @param key
     * A key string.
     * @param value
     * A double which is the value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Double): JSONObject {
        return this.put(key, java.lang.Double.valueOf(value))
    }

    /**
     * Put a key/float pair in the JSONObject.
     *
     * @param key
     * A key string.
     * @param value
     * A float which is the value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Float): JSONObject {
        return this.put(key, java.lang.Float.valueOf(value))
    }

    /**
     * Put a key/int pair in the JSONObject.
     *
     * @param key
     * A key string.
     * @param value
     * An int which is the value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Int): JSONObject {
        return this.put(key, Integer.valueOf(value))
    }

    /**
     * Put a key/long pair in the JSONObject.
     *
     * @param key
     * A key string.
     * @param value
     * A long which is the value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Long): JSONObject {
        return this.put(key, java.lang.Long.valueOf(value))
    }

    /**
     * Put a key/value pair in the JSONObject, where the value will be a
     * JSONObject which is produced from a Map.
     *
     * @param key
     * A key string.
     * @param value
     * A Map value.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Map<*, *>?): JSONObject {
        return this.put(key, JSONObject(value))
    }

    /**
     * Put a key/value pair in the JSONObject. If the value is `null`, then the
     * key will be removed from the JSONObject if it is present.
     *
     * @param key
     * A key string.
     * @param value
     * An object which is the value. It should be of one of these
     * types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
     * String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException
     * If the value is non-finite number.
     * @throws NullPointerException
     * If the key is `null`.
     */
    @Throws(JSONException::class)
    fun put(key: String?, value: Any?): JSONObject {
        if (key == null) {
            throw NullPointerException("Null key.")
        }
        if (value != null) {
            testValidity(value)
            map[key] = value
        } else {
            this.remove(key)
        }
        return this
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value
     * are both non-null, and only if there is not already a member with that
     * name.
     *
     * @param key
     * key to insert into
     * @param value
     * value to insert
     * @return this.
     * @throws JSONException
     * if the key is a duplicate
     */
    @Throws(JSONException::class)
    fun putOnce(key: String?, value: Any?): JSONObject {
        if (key != null && value != null) {
            if (opt(key) != null) {
                throw JSONException("Duplicate key \"$key\"")
            }
            return this.put(key, value)
        }
        return this
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the value
     * are both non-null.
     *
     * @param key
     * A key string.
     * @param value
     * An object which is the value. It should be of one of these
     * types: Boolean, Double, Integer, JSONArray, JSONObject, Long,
     * String, or the JSONObject.NULL object.
     * @return this.
     * @throws JSONException
     * If the value is a non-finite number.
     */
    @Throws(JSONException::class)
    fun putOpt(key: String?, value: Any?): JSONObject {
        return if (key != null && value != null) {
            this.put(key, value)
        } else this
    }

    /**
     * Creates a JSONPointer using an initialization string and tries to
     * match it to an item within this JSONObject. For example, given a
     * JSONObject initialized with this document:
     * <pre>
     * {
     * "a":{"b":"c"}
     * }
    </pre> *
     * and this JSONPointer string:
     * <pre>
     * "/a/b"
    </pre> *
     * Then this method will return the String "c".
     * A JSONPointerException may be thrown from code called by this method.
     *
     * @param jsonPointer string that can be used to create a JSONPointer
     * @return the item matched by the JSONPointer, otherwise null
     */
    fun query(jsonPointer: String?): Any {
        return query(JSONPointer(jsonPointer))
    }

    /**
     * Uses a user initialized JSONPointer  and tries to
     * match it to an item within this JSONObject. For example, given a
     * JSONObject initialized with this document:
     * <pre>
     * {
     * "a":{"b":"c"}
     * }
    </pre> *
     * and this JSONPointer:
     * <pre>
     * "/a/b"
    </pre> *
     * Then this method will return the String "c".
     * A JSONPointerException may be thrown from code called by this method.
     *
     * @param jsonPointer string that can be used to create a JSONPointer
     * @return the item matched by the JSONPointer, otherwise null
     */
    fun query(jsonPointer: JSONPointer): Any {
        return jsonPointer.queryFrom(this)
    }

    /**
     * Queries and returns a value from this object using `jsonPointer`, or
     * returns null if the query fails due to a missing key.
     *
     * @param jsonPointer the string representation of the JSON pointer
     * @return the queried value or `null`
     * @throws IllegalArgumentException if `jsonPointer` has invalid syntax
     */
    fun optQuery(jsonPointer: String?): Any? {
        return optQuery(JSONPointer(jsonPointer))
    }

    /**
     * Queries and returns a value from this object using `jsonPointer`, or
     * returns null if the query fails due to a missing key.
     *
     * @param jsonPointer The JSON pointer
     * @return the queried value or `null`
     * @throws IllegalArgumentException if `jsonPointer` has invalid syntax
     */
    fun optQuery(jsonPointer: JSONPointer): Any? {
        return try {
            jsonPointer.queryFrom(this)
        } catch (e: JSONPointerException) {
            null
        }
    }

    /**
     * Remove a name and its value, if present.
     *
     * @param key
     * The name to be removed.
     * @return The value that was associated with the name, or null if there was
     * no value.
     */
    fun remove(key: String?): Any? {
        return map.remove(key)
    }

    /**
     * Determine if two JSONObjects are similar.
     * They must contain the same set of names which must be associated with
     * similar values.
     *
     * @param other The other JSONObject
     * @return true if they are equal
     */
    fun similar(other: Any?): Boolean {
        return try {
            if (other !is JSONObject) {
                return false
            }
            if (keySet() != other.keySet()) {
                return false
            }
            for ((name, valueThis) in entrySet()) {
                val valueOther = other[name]
                if (valueThis === valueOther) {
                    continue
                }
                if (valueThis == null) {
                    return false
                }
                if (valueThis is JSONObject) {
                    if (!valueThis.similar(valueOther)) {
                        return false
                    }
                } else if (valueThis is JSONArray) {
                    if (!valueThis.similar(valueOther)) {
                        return false
                    }
                } else if (valueThis != valueOther) {
                    return false
                }
            }
            true
        } catch (exception: Throwable) {
            false
        }
    }

    /**
     * Produce a JSONArray containing the values of the members of this
     * JSONObject.
     *
     * @param names
     * A JSONArray containing a list of key strings. This determines
     * the sequence of the values in the result.
     * @return A JSONArray of values.
     * @throws JSONException
     * If any of the values are non-finite numbers.
     */
    @Throws(JSONException::class)
    fun toJSONArray(names: JSONArray?): JSONArray? {
        if (names == null || names.isEmpty) {
            return null
        }
        val ja = JSONArray()
        var i = 0
        while (i < names.length()) {
            ja.put(opt(names.getString(i)))
            i += 1
        }
        return ja
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace is
     * added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     *
     * **
     * Warning: This method assumes that the data structure is acyclical.
     ** *
     *
     * @return a printable, displayable, portable, transmittable representation
     * of the object, beginning with `{`&nbsp;<small>(left
     * brace)</small> and ending with `}`&nbsp;<small>(right
     * brace)</small>.
     */
    override fun toString(): String {
        return try {
            this.toString(0)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Make a pretty-printed JSON text of this JSONObject.
     *
     *
     * If <pre>`indentFactor > 0`</pre> and the [JSONObject]
     * has only one key, then the object will be output on a single line:
     * <pre>`{"key": 1}`</pre>
     *
     *
     * If an object has 2 or more keys, then it will be output across
     * multiple lines: <pre>`{
     * "key1": 1,
     * "key2": "value 2",
     * "key3": 3
     * }`</pre>
     *
     * **
     * Warning: This method assumes that the data structure is acyclical.
     ** *
     *
     * @param indentFactor
     * The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation
     * of the object, beginning with `{`&nbsp;<small>(left
     * brace)</small> and ending with `}`&nbsp;<small>(right
     * brace)</small>.
     * @throws JSONException
     * If the object contains an invalid number.
     */
    @Throws(JSONException::class)
    fun toString(indentFactor: Int): String {
        val w = StringWriter()
        synchronized(w.buffer) { return write(w, indentFactor, 0).toString() }
    }
    /**
     * Write the contents of the JSONObject as JSON text to a writer.
     *
     *
     * If <pre>`indentFactor > 0`</pre> and the [JSONObject]
     * has only one key, then the object will be output on a single line:
     * <pre>`{"key": 1}`</pre>
     *
     *
     * If an object has 2 or more keys, then it will be output across
     * multiple lines: <pre>`{
     * "key1": 1,
     * "key2": "value 2",
     * "key3": 3
     * }`</pre>
     *
     * **
     * Warning: This method assumes that the data structure is acyclical.
     ** *
     *
     * @param writer
     * Writes the serialized JSON
     * @param indentFactor
     * The number of spaces to add to each level of indentation.
     * @param indent
     * The indentation of the top level.
     * @return The writer.
     * @throws JSONException if a called function has an error or a write error
     * occurs
     */
    /**
     * Write the contents of the JSONObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     *
     * **
     * Warning: This method assumes that the data structure is acyclical.
     ** *
     * @param writer the writer object
     * @return The writer.
     * @throws JSONException if a called function has an error
     */
    @JvmOverloads
    @Throws(JSONException::class)
    fun write(writer: Writer, indentFactor: Int = 0, indent: Int = 0): Writer {
        return try {
            var needsComma = false
            val length = length()
            writer.write('{'.toInt())
            if (length == 1) {
                val entry = entrySet().iterator().next()
                val key = entry.key
                writer.write(quote(key))
                writer.write(':'.toInt())
                if (indentFactor > 0) {
                    writer.write(' '.toInt())
                }
                try {
                    writeValue(writer, entry.value, indentFactor, indent)
                } catch (e: Exception) {
                    throw JSONException("Unable to write JSONObject value for key: $key", e)
                }
            } else if (length != 0) {
                val newIndent = indent + indentFactor
                for ((key, value) in entrySet()) {
                    if (needsComma) {
                        writer.write(','.toInt())
                    }
                    if (indentFactor > 0) {
                        writer.write('\n'.toInt())
                    }
                    indent(writer, newIndent)
                    writer.write(quote(key))
                    writer.write(':'.toInt())
                    if (indentFactor > 0) {
                        writer.write(' '.toInt())
                    }
                    try {
                        writeValue(writer, value, indentFactor, newIndent)
                    } catch (e: Exception) {
                        throw JSONException("Unable to write JSONObject value for key: $key", e)
                    }
                    needsComma = true
                }
                if (indentFactor > 0) {
                    writer.write('\n'.toInt())
                }
                indent(writer, indent)
            }
            writer.write('}'.toInt())
            writer
        } catch (exception: IOException) {
            throw JSONException(exception)
        }
    }

    /**
     * Returns a java.util.Map containing all of the entries in this object.
     * If an entry in the object is a JSONArray or JSONObject it will also
     * be converted.
     *
     *
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a java.util.Map containing the entries of this object
     */
    fun toMap(): Map<String?, Any?> {
        val results: MutableMap<String?, Any?> = HashMap()
        for ((key, value1) in entrySet()) {
            var value: Any?
            value = if (value1 == null || NULL == value1) {
                null
            } else if (value1 is JSONObject) {
                (value1 as JSONObject?)!!.toMap()
            } else if (value1 is JSONArray) {
                (value1 as JSONArray?)!!.toList()
            } else {
                value1
            }
            results[key] = value
        }
        return results
    }

    companion object {
        /**
         * Regular Expression Pattern that matches JSON Numbers. This is primarily used for
         * output to guarantee that we are always writing valid JSON.
         */
        val NUMBER_PATTERN =
            Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")

        /**
         * It is sometimes more convenient and less ambiguous to have a
         * `NULL` object than to use Java's `null` value.
         * `JSONObject.NULL.equals(null)` returns `true`.
         * `JSONObject.NULL.toString()` returns `"null"`.
         */
        val NULL: Any = Null()

        /**
         * Produce a string from a double. The string "null" will be returned if the
         * number is not finite.
         *
         * @param d
         * A double.
         * @return A String.
         */
        fun doubleToString(d: Double): String {
            if (java.lang.Double.isInfinite(d) || java.lang.Double.isNaN(d)) {
                return "null"
            }

// Shave off trailing zeros and decimal point, if possible.
            var string = java.lang.Double.toString(d)
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0
            ) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length - 1)
                }
            }
            return string
        }

        /**
         * Get an array of field names from a JSONObject.
         *
         * @param jo
         * JSON object
         * @return An array of field names, or null if there are no names.
         */
        fun getNames(jo: JSONObject): Array<String?>? {
            return if (jo.isEmpty) {
                null
            } else jo.keySet().toTypedArray()
        }

        /**
         * Get an array of public field names from an Object.
         *
         * @param object
         * object to read
         * @return An array of field names, or null if there are no names.
         */
        fun getNames(`object`: Any?): Array<String?>? {
            if (`object` == null) {
                return null
            }
            val klass: Class<*> = `object`.javaClass
            val fields = klass.fields
            val length = fields.size
            if (length == 0) {
                return null
            }
            val names = arrayOfNulls<String>(length)
            var i = 0
            while (i < length) {
                names[i] = fields[i].name
                i += 1
            }
            return names
        }

        /**
         * Produce a string from a Number.
         *
         * @param number
         * A Number
         * @return A String.
         * @throws JSONException
         * If n is a non-finite number.
         */
        @Throws(JSONException::class)
        fun numberToString(number: Number?): String {
            if (number == null) {
                throw JSONException("Null pointer")
            }
            testValidity(number)

            // Shave off trailing zeros and decimal point, if possible.
            var string = number.toString()
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0
            ) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length - 1)
                }
            }
            return string
        }

        /**
         * @param val value to convert
         * @param defaultValue default value to return is the conversion doesn't work or is null.
         * @return BigDecimal conversion of the original value, or the defaultValue if unable
         * to convert.
         */
        fun objectToBigDecimal(`val`: Any?, defaultValue: BigDecimal?): BigDecimal? {
            if (NULL == `val`) {
                return defaultValue
            }
            if (`val` is BigDecimal) {
                return `val`
            }
            if (`val` is BigInteger) {
                return BigDecimal(`val` as BigInteger?)
            }
            if (`val` is Double || `val` is Float) {
                val d: Double = (`val` as Number).doubleValue()
                return if (java.lang.Double.isNaN(d)) {
                    defaultValue
                } else BigDecimal(`val`.doubleValue())
            }
            return if (`val` is Long || `val` is Int
                || `val` is Short || `val` is Byte
            ) {
                BigDecimal((`val` as Number).longValue())
            } else try {
                BigDecimal(`val`.toString())
            } catch (e: Exception) {
                defaultValue
            }
            // don't check if it's a string in case of unchecked Number subclasses
        }

        /**
         * @param val value to convert
         * @param defaultValue default value to return is the conversion doesn't work or is null.
         * @return BigInteger conversion of the original value, or the defaultValue if unable
         * to convert.
         */
        fun objectToBigInteger(`val`: Any?, defaultValue: BigInteger?): BigInteger? {
            if (NULL == `val`) {
                return defaultValue
            }
            if (`val` is BigInteger) {
                return `val`
            }
            if (`val` is BigDecimal) {
                return `val`.toBigInteger()
            }
            if (`val` is Double || `val` is Float) {
                val d: Double = (`val` as Number).doubleValue()
                return if (java.lang.Double.isNaN(d)) {
                    defaultValue
                } else BigDecimal(d).toBigInteger()
            }
            return if (`val` is Long || `val` is Int
                || `val` is Short || `val` is Byte
            ) {
                BigInteger.valueOf((`val` as Number).longValue())
            } else try {
                // the other opt functions handle implicit conversions, i.e. 
                // jo.put("double",1.1d);
                // jo.optInt("double"); -- will return 1, not an error
                // this conversion to BigDecimal then to BigInteger is to maintain
                // that type cast support that may truncate the decimal.
                val valStr = `val`.toString()
                if (isDecimalNotation(valStr)) {
                    BigDecimal(valStr).toBigInteger()
                } else BigInteger(valStr)
            } catch (e: Exception) {
                defaultValue
            }
            // don't check if it's a string in case of unchecked Number subclasses
        }

        private fun isValidMethodName(name: String): Boolean {
            return "getClass" != name && "getDeclaringClass" != name
        }

        private fun getKeyNameFromMethod(method: Method): String? {
            val ignoreDepth = getAnnotationDepth(
                method,
                JSONPropertyIgnore::class.java
            )
            if (ignoreDepth > 0) {
                val forcedNameDepth = getAnnotationDepth(
                    method,
                    JSONPropertyName::class.java
                )
                if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) {
                    // the hierarchy asked to ignore, and the nearest name override
                    // was higher or non-existent
                    return null
                }
            }
            val annotation =
                getAnnotation(
                    method,
                    JSONPropertyName::class.java
                )
            if (annotation != null && annotation.value() != null && !annotation.value().isEmpty()) {
                return annotation.value()
            }
            var key: String
            val name = method.name
            key = if (name.startsWith("get") && name.length > 3) {
                name.substring(3)
            } else if (name.startsWith("is") && name.length > 2) {
                name.substring(2)
            } else {
                return null
            }
            // if the first letter in the key is not uppercase, then skip.
            // This is to maintain backwards compatibility before PR406
            // (https://github.com/stleary/JSON-java/pull/406/)
            if (Character.isLowerCase(key[0])) {
                return null
            }
            if (key.length == 1) {
                key = key.toLowerCase(Locale.ROOT)
            } else if (!Character.isUpperCase(key[1])) {
                key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1)
            }
            return key
        }

        /**
         * Searches the class hierarchy to see if the method or it's super
         * implementations and interfaces has the annotation.
         *
         * @param <A>
         * type of the annotation
         *
         * @param m
         * method to check
         * @param annotationClass
         * annotation to look for
         * @return the [Annotation] if the annotation exists on the current method
         * or one of it's super class definitions
        </A> */
        private fun <A : Annotation?> getAnnotation(
            m: Method?,
            annotationClass: Class<A>?
        ): A? {
            // if we have invalid data the result is null
            if (m == null || annotationClass == null) {
                return null
            }
            if (m.isAnnotationPresent(annotationClass)) {
                return m.getAnnotation(annotationClass)
            }

            // if we've already reached the Object class, return null;
            val c = m.declaringClass
            if (c.superclass == null) {
                return null
            }

            // check directly implemented interfaces for the method being checked
            for (i in c.interfaces) {
                return try {
                    val im = i.getMethod(m.name, *m.parameterTypes)
                    getAnnotation(im, annotationClass)
                } catch (ex: SecurityException) {
                    continue
                } catch (ex: NoSuchMethodException) {
                    continue
                }
            }
            return try {
                getAnnotation(
                    c.superclass.getMethod(m.name, *m.parameterTypes),
                    annotationClass
                )
            } catch (ex: SecurityException) {
                null
            } catch (ex: NoSuchMethodException) {
                null
            }
        }

        /**
         * Searches the class hierarchy to see if the method or it's super
         * implementations and interfaces has the annotation. Returns the depth of the
         * annotation in the hierarchy.
         *
         * @param <A>
         * type of the annotation
         *
         * @param m
         * method to check
         * @param annotationClass
         * annotation to look for
         * @return Depth of the annotation or -1 if the annotation is not on the method.
        </A> */
        private fun getAnnotationDepth(
            m: Method?,
            annotationClass: Class<out Annotation>?
        ): Int {
            // if we have invalid data the result is -1
            if (m == null || annotationClass == null) {
                return -1
            }
            if (m.isAnnotationPresent(annotationClass)) {
                return 1
            }

            // if we've already reached the Object class, return -1;
            val c = m.declaringClass
            if (c.superclass == null) {
                return -1
            }

            // check directly implemented interfaces for the method being checked
            for (i in c.interfaces) {
                try {
                    val im = i.getMethod(m.name, *m.parameterTypes)
                    val d = getAnnotationDepth(im, annotationClass)
                    if (d > 0) {
                        // since the annotation was on the interface, add 1
                        return d + 1
                    }
                } catch (ex: SecurityException) {
                    continue
                } catch (ex: NoSuchMethodException) {
                    continue
                }
            }
            return try {
                val d = getAnnotationDepth(
                    c.superclass.getMethod(m.name, *m.parameterTypes),
                    annotationClass
                )
                if (d > 0) {
                    // since the annotation was on the superclass, add 1
                    d + 1
                } else -1
            } catch (ex: SecurityException) {
                -1
            } catch (ex: NoSuchMethodException) {
                -1
            }
        }

        /**
         * Produce a string in double quotes with backslash sequences in all the
         * right places. A backslash will be inserted within &lt;/, producing
         * &lt;\/, allowing JSON text to be delivered in HTML. In JSON text, a
         * string cannot contain a control character or an unescaped quote or
         * backslash.
         *
         * @param string
         * A String
         * @return A String correctly formatted for insertion in a JSON text.
         */
        fun quote(string: String?): String {
            val sw = StringWriter()
            synchronized(sw.buffer) {
                return try {
                    quote(string, sw).toString()
                } catch (ignored: IOException) {
                    // will never happen - we are writing to a string writer
                    ""
                }
            }
        }

        @Throws(IOException::class)
        fun quote(string: String?, w: Writer): Writer {
            if (string == null || string.isEmpty()) {
                w.write("\"\"")
                return w
            }
            var b: Char
            var c = 0.toChar()
            var hhhh: String
            var i: Int
            val len = string.length
            w.write('"'.toInt())
            i = 0
            while (i < len) {
                b = c
                c = string[i]
                when (c) {
                    '\\', '"' -> {
                        w.write('\\'.toInt())
                        w.write(c.toInt())
                    }
                    '/' -> {
                        if (b == '<') {
                            w.write('\\'.toInt())
                        }
                        w.write(c.toInt())
                    }
                    '\b' -> w.write("\\b")
                    '\t' -> w.write("\\t")
                    '\n' -> w.write("\\n")
                    '\f' -> w.write("\\f")
                    '\r' -> w.write("\\r")
                    else -> if (c < ' ' || c >= '\u0080' && c < '\u00a0'
                        || c >= '\u2000' && c < '\u2100'
                    ) {
                        w.write("\\u")
                        hhhh = Integer.toHexString(c.toInt())
                        w.write("0000", 0, 4 - hhhh.length)
                        w.write(hhhh)
                    } else {
                        w.write(c.toInt())
                    }
                }
                i += 1
            }
            w.write('"'.toInt())
            return w
        }

        /**
         * Tests if the value should be tried as a decimal. It makes no test if there are actual digits.
         *
         * @param val value to test
         * @return true if the string is "-0" or if it contains '.', 'e', or 'E', false otherwise.
         */
        protected fun isDecimalNotation(`val`: String): Boolean {
            return `val`.indexOf('.') > -1 || `val`.indexOf('e') > -1 || `val`.indexOf('E') > -1 || "-0" == `val`
        }

        /**
         * Converts a string to a number using the narrowest possible type. Possible
         * returns for this function are BigDecimal, Double, BigInteger, Long, and Integer.
         * When a Double is returned, it should always be a valid Double and not NaN or +-infinity.
         *
         * @param val value to convert
         * @return Number representation of the value.
         * @throws NumberFormatException thrown if the value is not a valid number. A public
         * caller should catch this and wrap it in a [JSONException] if applicable.
         */
        @Throws(NumberFormatException::class)
        protected fun stringToNumber(`val`: String): Number {
            val initial = `val`[0]
            if (initial >= '0' && initial <= '9' || initial == '-') {
                // decimal representation
                if (isDecimalNotation(`val`)) {
                    // Use a BigDecimal all the time so we keep the original
                    // representation. BigDecimal doesn't support -0.0, ensure we
                    // keep that by forcing a decimal.
                    return try {
                        val bd = BigDecimal(`val`)
                        if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) {
                            java.lang.Double.valueOf(-0.0)
                        } else bd
                    } catch (retryAsDouble: NumberFormatException) {
                        // this is to support "Hex Floats" like this: 0x1.0P-1074
                        try {
                            val d = java.lang.Double.valueOf(`val`)
                            if (d.isNaN() || d.isInfinite()) {
                                throw NumberFormatException("val [$`val`] is not a valid number.")
                            }
                            d
                        } catch (ignore: NumberFormatException) {
                            throw NumberFormatException("val [$`val`] is not a valid number.")
                        }
                    }
                }
                // block items like 00 01 etc. Java number parsers treat these as Octal.
                if (initial == '0' && `val`.length > 1) {
                    val at1 = `val`[1]
                    if (at1 >= '0' && at1 <= '9') {
                        throw NumberFormatException("val [$`val`] is not a valid number.")
                    }
                } else if (initial == '-' && `val`.length > 2) {
                    val at1 = `val`[1]
                    val at2 = `val`[2]
                    if (at1 == '0' && at2 >= '0' && at2 <= '9') {
                        throw NumberFormatException("val [$`val`] is not a valid number.")
                    }
                }
                // integer representation.
                // This will narrow any values to the smallest reasonable Object representation
                // (Integer, Long, or BigInteger)

                // BigInteger down conversion: We use a similar bitLenth compare as
                // BigInteger#intValueExact uses. Increases GC, but objects hold
                // only what they need. i.e. Less runtime overhead if the value is
                // long lived.
                val bi = BigInteger(`val`)
                if (bi.bitLength() <= 31) {
                    return Integer.valueOf(bi.intValue())
                }
                return if (bi.bitLength() <= 63) {
                    java.lang.Long.valueOf(bi.longValue())
                } else bi
            }
            throw NumberFormatException("val [$`val`] is not a valid number.")
        }

        /**
         * Try to convert a string into a number, boolean, or null. If the string
         * can't be converted, return the string.
         *
         * @param string
         * A String. can not be null.
         * @return A simple JSON value.
         * @throws NullPointerException
         * Thrown if the string is null.
         */
        // Changes to this method must be copied to the corresponding method in
        // the XML class to keep full support for Android
        fun stringToValue(string: String): Any {
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
                return NULL
            }

            /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */
            val initial = string[0]
            if (initial >= '0' && initial <= '9' || initial == '-') {
                try {
                    return stringToNumber(string)
                } catch (ignore: Exception) {
                }
            }
            return string
        }

        /**
         * Throw an exception if the object is a NaN or infinite number.
         *
         * @param o
         * The object to test.
         * @throws JSONException
         * If o is a non-finite number.
         */
        @Throws(JSONException::class)
        fun testValidity(o: Any?) {
            if (o != null) {
                if (o is Double) {
                    if (o.isInfinite() || o.isNaN()) {
                        throw JSONException(
                            "JSON does not allow non-finite numbers."
                        )
                    }
                } else if (o is Float) {
                    if (o.isInfinite() || o.isNaN()) {
                        throw JSONException(
                            "JSON does not allow non-finite numbers."
                        )
                    }
                }
            }
        }

        /**
         * Make a JSON text of an Object value. If the object has an
         * value.toJSONString() method, then that method will be used to produce the
         * JSON text. The method is required to produce a strictly conforming text.
         * If the object does not contain a toJSONString method (which is the most
         * common case), then a text will be produced by other means. If the value
         * is an array or Collection, then a JSONArray will be made from it and its
         * toJSONString method will be called. If the value is a MAP, then a
         * JSONObject will be made from it and its toJSONString method will be
         * called. Otherwise, the value's toString method will be called, and the
         * result will be quoted.
         *
         *
         *
         * Warning: This method assumes that the data structure is acyclical.
         *
         * @param value
         * The value to be serialized.
         * @return a printable, displayable, transmittable representation of the
         * object, beginning with `{`&nbsp;<small>(left
         * brace)</small> and ending with `}`&nbsp;<small>(right
         * brace)</small>.
         * @throws JSONException
         * If the value is or contains an invalid number.
         */
        @Throws(JSONException::class)
        fun valueToString(value: Any?): String {
            // moves the implementation to JSONWriter as:
            // 1. It makes more sense to be part of the writer class
            // 2. For Android support this method is not available. By implementing it in the Writer
            //    Android users can use the writer with the built in Android JSONObject implementation.
            return JSONWriter.valueToString(value)
        }

        /**
         * Wrap an object, if necessary. If the object is `null`, return the NULL
         * object. If it is an array or collection, wrap it in a JSONArray. If it is
         * a map, wrap it in a JSONObject. If it is a standard property (Double,
         * String, et al) then it is already wrapped. Otherwise, if it comes from
         * one of the java packages, turn it into a string. And if it doesn't, try
         * to wrap it in a JSONObject. If the wrapping fails, then null is returned.
         *
         * @param object
         * The object to wrap
         * @return The wrapped value
         */
        fun wrap(`object`: Any?): Any? {
            return try {
                if (`object` == null) {
                    return NULL
                }
                if (`object` is JSONObject || `object` is JSONArray
                    || NULL == `object` || `object` is JSONString
                    || `object` is Byte || `object` is Char
                    || `object` is Short || `object` is Int
                    || `object` is Long || `object` is Boolean
                    || `object` is Float || `object` is Double
                    || `object` is String || `object` is BigInteger
                    || `object` is BigDecimal || `object` is Enum<*>
                ) {
                    return `object`
                }
                if (`object` is Collection<*>) {
                    return JSONArray(`object`)
                }
                if (`object`.javaClass.isArray) {
                    return JSONArray(`object`)
                }
                if (`object` is Map<*, *>) {
                    return JSONObject(`object`)
                }
                val objectPackage = `object`.javaClass.getPackage()
                val objectPackageName = if (objectPackage != null) objectPackage
                    .name else ""
                if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || `object`.javaClass.classLoader == null
                ) {
                    `object`.toString()
                } else JSONObject(`object`)
            } catch (exception: Exception) {
                null
            }
        }

        @Throws(JSONException::class, IOException::class)
        fun writeValue(
            writer: Writer, value: Any?,
            indentFactor: Int, indent: Int
        ): Writer {
            if (value == null || value == null) {
                writer.write("null")
            } else if (value is JSONString) {
                val o: Any?
                o = try {
                    value.toJSONString()
                } catch (e: Exception) {
                    throw JSONException(e)
                }
                writer.write(o?.toString() ?: quote(value.toString()))
            } else if (value is Number) {
                // not all Numbers may match actual JSON Numbers. i.e. fractions or Imaginary
                val numberAsString =
                    numberToString(value as Number?)
                if (NUMBER_PATTERN.matcher(numberAsString).matches()) {
                    writer.write(numberAsString)
                } else {
                    // The Number value is not a valid JSON number.
                    // Instead we will quote it as a string
                    quote(numberAsString, writer)
                }
            } else if (value is Boolean) {
                writer.write(value.toString())
            } else if (value is Enum<*>) {
                writer.write(quote(value.name))
            } else if (value is JSONObject) {
                value.write(writer, indentFactor, indent)
            } else if (value is JSONArray) {
                value.write(writer, indentFactor, indent)
            } else if (value is Map<*, *>) {
                JSONObject(value).write(writer, indentFactor, indent)
            } else if (value is Collection<*>) {
                JSONArray(value).write(writer, indentFactor, indent)
            } else if (value.javaClass.isArray) {
                JSONArray(value).write(writer, indentFactor, indent)
            } else {
                quote(value.toString(), writer)
            }
            return writer
        }

        @Throws(IOException::class)
        fun indent(writer: Writer, indent: Int) {
            var i = 0
            while (i < indent) {
                writer.write(' '.toInt())
                i += 1
            }
        }

        /**
         * Create a new JSONException in a common format for incorrect conversions.
         * @param key name of the key
         * @param valueType the type of value being coerced to
         * @param cause optional cause of the coercion failure
         * @return JSONException that can be thrown.
         */
        private fun wrongValueFormatException(
            key: String?,
            valueType: String,
            cause: Throwable?
        ): JSONException {
            return JSONException(
                "JSONObject[" + quote(key) + "] is not a " + valueType + "."
                , cause
            )
        }

        /**
         * Create a new JSONException in a common format for incorrect conversions.
         * @param key name of the key
         * @param valueType the type of value being coerced to
         * @param cause optional cause of the coercion failure
         * @return JSONException that can be thrown.
         */
        private fun wrongValueFormatException(
            key: String?,
            valueType: String,
            value: Any?,
            cause: Throwable?
        ): JSONException {
            return JSONException(
                "JSONObject[" + quote(key) + "] is not a " + valueType + " (" + value + ")."
                , cause
            )
        }
    }
}