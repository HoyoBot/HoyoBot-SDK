package cn.hoyobot.sdk.utils

import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

open class ConfigSection() : LinkedHashMap<String, Any>() {
    /**
     * Constructor of ConfigSection that contains initial key/value data
     *
     * @param key
     * @param value
     */
    constructor(key: String, value: Any) : this() {
        this[key] = value
    }

    /**
     * Constructor of ConfigSection, based on values stored in map.
     *
     * @param map
     */
    constructor(map: LinkedHashMap<*, *>) : this() {
        if (map.isEmpty()) return

        map.forEach { (key, value) ->
            run {
                if (value is LinkedHashMap<*, *>) {
                    super.put(key.toString(), ConfigSection(value))
                } else {
                    super.put(key.toString(), value)
                }
            }
        }
    }

    /**
     * Get root section as LinkedHashMap
     *
     * @return
     */
    val allMap: Map<String, Any>
        get() {
            val map = LinkedHashMap<String, Any>()
            map.putAll(this)
            return map
        }

    /**
     * Get new instance of config section
     *
     * @return
     */
    private val all: ConfigSection
        get() = ConfigSection(this)

    /**
     * Get object by key. If section does not contain value, return null
     *
     * @param key
     * @return
     */
    override fun get(key: String): Any {
        return this[key, Any()]
    }

    /**
     * Get object by key. If section does not contain value, return default value
     *
     * @param key
     * @param defaltValue
     * @param <T>
     * @return
    </T> */
    operator fun <T> get(key: String, defaltValue: T): T {
        if (key.isEmpty()) return defaltValue
        if (super.containsKey(key)) return super.get(key) as T
        val keys = key.split("\\.".toRegex(), limit = 2).toTypedArray()
        if (!super.containsKey(keys[0])) return defaltValue
        val value = super.get(keys[0])
        if (value != null && value is ConfigSection) {
            return value[keys[1], defaltValue]
        }
        return defaltValue
    }

    /**
     * Store value into config section
     *
     * @param key
     * @param value
     */
    operator fun set(key: String, value: Any) {
        val subKeys = key.split("\\.".toRegex(), limit = 2).toTypedArray()
        if (subKeys.size > 1) {
            var childSection: ConfigSection? = ConfigSection()
            if (this.containsKey(subKeys[0]) && super.get(subKeys[0]) is ConfigSection) childSection =
                super.get(subKeys[0]) as ConfigSection?
            childSection!![subKeys[1]] = value
            super.put(subKeys[0], childSection)
        } else super.put(subKeys[0], value)
    }

    /**
     * Check type of section element defined by key. Return true this element is ConfigSection
     *
     * @param key
     * @return
     */
    fun isSection(key: String): Boolean {
        val value = this[key]
        return value is ConfigSection
    }

    /**
     * Get config section element defined by key
     *
     * @param key
     * @return
     */
    fun getSection(key: String): ConfigSection {
        return this[key, ConfigSection()]
    }

    /**
     * Get all ConfigSections in root path.
     * Example config:
     * a1:
     * b1:
     * c1:
     * c2:
     * a2:
     * b2:
     * c3:
     * c4:
     * a3: true
     * a4: "hello"
     * a5: 100
     *
     *
     * getSections() will return new ConfigSection, that contains sections a1 and a2 only.
     *
     * @return
     */
    val sections: ConfigSection
        get() = getSections(null)

    /**
     * Get sections (and only sections) from provided path
     *
     * @param key - config section path, if null or empty root path will used.
     * @return
     */
    fun getSections(key: String?): ConfigSection {
        val sections = ConfigSection()
        val parent = (if (key == null || key.isEmpty()) all else getSection(key))
        parent.entries.forEach(Consumer<Map.Entry<String, Any>> { (key1, value): Map.Entry<String, Any> ->
            if (value is ConfigSection) sections.put(
                key1,
                value
            )
        })
        return sections
    }

    /**
     * Get int value of config section element
     *
     * @param key - key (inside) current section (default value equals to 0)
     * @return
     */
    fun getInt(key: String): Int {
        return this.getInt(key, 0)
    }

    /**
     * Get int value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getInt(key: String, defaultValue: Int): Int {
        return this[key, defaultValue].toInt()
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    fun isInt(key: String): Boolean {
        val `val` = get(key)
        return `val` is Int
    }

    /**
     * Get long value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getLong(key: String): Long {
        return this.getLong(key, 0)
    }

    /**
     * Get long value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getLong(key: String, defaultValue: Long): Long {
        return this[key, defaultValue].toLong()
    }

    /**
     * Check type of section element defined by key. Return true this element is Long
     *
     * @param key
     * @return
     */
    fun isLong(key: String): Boolean {
        val `val` = get(key)
        return `val` is Long
    }

    /**
     * Get double value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getDouble(key: String): Double {
        return this.getDouble(key, 0.0)
    }

    /**
     * Get double value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getDouble(key: String, defaultValue: Double): Double {
        return this[key, defaultValue].toDouble()
    }

    /**
     * Check type of section element defined by key. Return true this element is Double
     *
     * @param key
     * @return
     */
    fun isDouble(key: String): Boolean {
        val `val` = get(key)
        return `val` is Double
    }

    /**
     * Get String value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getString(key: String): String {
        return this.getString(key, "")
    }

    /**
     * Get String value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getString(key: String, defaultValue: String): String {
        val result: Any = this[key, defaultValue]
        return result.toString()
    }

    /**
     * Check type of section element defined by key. Return true this element is String
     *
     * @param key
     * @return
     */
    fun isString(key: String): Boolean {
        val `val` = get(key)
        return `val` is String
    }

    /**
     * Get boolean value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getBoolean(key: String): Boolean {
        return this.getBoolean(key, false)
    }

    /**
     * Get boolean value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return this[key, defaultValue]
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    fun isBoolean(key: String): Boolean {
        val `val` = get(key)
        return `val` is Boolean
    }

    /**
     * Get List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    private fun getList(key: String): List<String> {
        return this.getList(key, defaultList = kotlin.collections.ArrayList())
    }

    /**
     * Get List value of config section element
     *
     * @param key         - key (inside) current section
     * @param defaultList - default value that will returned if section element is not exists
     * @return
     */
    fun getList(key: String, defaultList: List<String>): List<String> {
        return this[key, defaultList]
    }

    /**
     * Check type of section element defined by key. Return true this element is List
     *
     * @param key
     * @return
     */
    fun isList(key: String): Boolean {
        val `val` = get(key)
        return `val` is List<*>
    }

    /**
     * Get String List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getStringList(key: String): List<String> {
        val value = this.getList(key)
        val result: MutableList<String> = ArrayList()
        for (o in value) {
            result.add(o)
        }
        return result
    }

    /**
     * Get Integer List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getIntegerList(key: String): List<Int> {
        val list = getList(key)
        val result: MutableList<Int> = ArrayList()
        for (`object` in list) {
            when (`object`) {
                else -> {
                    try {
                        result.add(Integer.valueOf(`object` as String?))
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Get Boolean List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getBooleanList(key: String): List<Boolean> {
        val list = getList(key)
        val result: MutableList<Boolean> = ArrayList()
        for (`object` in list) {
            if (java.lang.Boolean.TRUE.toString() == `object`) {
                result.add(true)
            } else if (java.lang.Boolean.FALSE.toString() == `object`) {
                result.add(false)
            }
        }
        return result
    }

    /**
     * Get Double List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getDoubleList(key: String): List<Double> {
        val list = getList(key)
        val result: MutableList<Double> = ArrayList()
        for (`object` in list) {
            when (`object`) {
                else -> {
                    try {
                        result.add(java.lang.Double.valueOf(`object` as String?))
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Get Float List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getFloatList(key: String): List<Float> {
        val list = getList(key)
        val result: MutableList<Float> = ArrayList()
        for (`object` in list) {
            when (`object`) {
                else -> {
                    try {
                        result.add(java.lang.Float.valueOf(`object` as String?))
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Get Long List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getLongList(key: String): List<Long> {
        val list = getList(key)
        val result: MutableList<Long> = ArrayList()
        for (`object` in list) {
            when (`object`) {
                else -> {
                    try {
                        result.add(java.lang.Long.valueOf(`object` as String?))
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Get Byte List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getByteList(key: String): List<Byte> {
        val list = getList(key)
        val result: MutableList<Byte> = ArrayList()
        for (`object` in list) {
            when (`object`) {

                else -> {
                    try {
                        result.add(java.lang.Byte.valueOf(`object` as String?))
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Get Character List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getCharacterList(key: String): List<Char> {
        val list = getList(key)
        val result: MutableList<Char> = ArrayList()
        for (`object` in list) {
            if (`object`.length == 1) {
                result.add(`object`[0])
            }
        }
        return result
    }

    /**
     * Get Short List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getShortList(key: String): List<Short> {
        val list = getList(key)
        val result: MutableList<Short> = ArrayList()
        for (`object` in list) {
            when (`object`) {
                else -> {
                    try {
                        result.add(`object`.toShort())
                    } catch (ex: Exception) {
                        //ignore
                    }
                }
            }
        }
        return result
    }

    /**
     * Check existence of config section element
     *
     * @param key
     * @param ignoreCase
     * @return
     */
    /**
     * Check existence of config section element
     *
     * @param key
     * @return
     */
    @JvmOverloads
    fun exists(key: String, ignoreCase: Boolean = false): Boolean {
        var shortKey = key
        if (ignoreCase) shortKey = key.lowercase(Locale.getDefault())
        for (existKey in getKeys(true)) {
            var finalKey = existKey
            if (ignoreCase) finalKey = existKey.lowercase(Locale.getDefault())
            if (finalKey == shortKey) return true
        }
        return false
    }

    /**
     * Remove config section element
     *
     * @param key
     */
    override fun remove(key: String) {
        if (key.isEmpty()) return
        if (super.containsKey(key)) super.remove(key) else if (this.containsKey(".")) {
            val keys = key.split("\\.".toRegex(), limit = 2).toTypedArray()
            if (super.get(keys[0]) is ConfigSection) {
                val section = super.get(keys[0]) as ConfigSection?
                section!!.remove(keys[1])
            }
        }
    }

    /**
     * Get all keys
     *
     * @param child - true = include child keys
     * @return
     */
    fun getKeys(child: Boolean): Set<String> {
        val keys: MutableSet<String> = LinkedHashSet()
        entries.forEach(Consumer<Map.Entry<String, Any?>> { (key, value): Map.Entry<String, Any?> ->
            keys.add(key)
            if (value is ConfigSection) {
                if (child) value.getKeys(true)
                    .forEach(Consumer { childKey: String ->
                        keys.add(
                            "$key.$childKey"
                        )
                    })
            }
        })
        return keys
    }

}