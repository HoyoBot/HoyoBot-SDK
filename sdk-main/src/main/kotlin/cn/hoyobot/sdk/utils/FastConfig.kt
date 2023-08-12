package cn.hoyobot.sdk.utils

import cn.hutool.core.io.file.FileReader
import cn.hutool.json.JSONObject
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * FastConfig
 *
 * 只支持Json类型,并且修复了YAML的问题
 */
class FastConfig(var path: String) {

    private var jsonObject: JSONObject = JSONObject();

    init {
        val file = File(path)
        try {
            if (!file.exists()) {
                if (!file.parentFile.exists()) file.parentFile.mkdirs()
                file.createNewFile()
                val out = BufferedWriter(FileWriter(file))
                val defaultJson = JSONObject()
                defaultJson["init_data"] = "true"
                out.write(defaultJson.toJSONString(4))
                out.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fileReader = FileReader(path)
        val message = fileReader.readString()
        jsonObject = JSONObject(message)
    }

    operator fun set(key: String, value: Any) {
        jsonObject[key] = value
    }

    fun put(key: String, value: Any) {
        this.set(key, value)
    }

    fun containsKey(key: String): Boolean {
        return jsonObject.containsKey(key)
    }

    fun exists(key: String): Boolean {
        return jsonObject.containsKey(key)
    }

    fun getString(key: String): String {
        return this.getStr(key)
    }

    fun getStr(key: String): String {
        return this.jsonObject.getStr(key)
    }

    fun getInt(key: String): Int {
        return this.jsonObject.getInt(key)
    }

    fun getDouble(key: String): Double {
        return this.jsonObject.getDouble(key)
    }

    fun getChar(key: String): Char {
        return this.jsonObject.getChar(key)
    }

    fun getShort(key: String): Short {
        return this.jsonObject.getShort(key)
    }

    fun remove(key: String) {
        this.jsonObject.remove(key)
    }

    fun getType(): Int {
        return Config.JSON
    }

    fun getIntegerList(key: String): List<Int> {
        val list: MutableList<Int> = ArrayList()
        val jsonArray = jsonObject.getJSONArray(key)
        for (`object` in jsonArray.toTypedArray()) {
            if (`object` is Int) list.add(`object`)
        }
        return list
    }

    fun getDoubleList(key: String): List<Double> {
        val list: MutableList<Double> = ArrayList()
        val jsonArray = jsonObject.getJSONArray(key)
        for (`object` in jsonArray.toTypedArray()) {
            if (`object` is Double) list.add(`object`)
        }
        return list
    }

    fun getCharacterList(key: String): List<Char> {
        val list: MutableList<Char> = ArrayList()
        val jsonArray = jsonObject.getJSONArray(key)
        for (`object` in jsonArray.toTypedArray()) {
            if (`object` is Char) list.add(`object`)
        }
        return list
    }

    fun getShortList(key: String): List<Short> {
        val list: MutableList<Short> = ArrayList()
        val jsonArray = jsonObject.getJSONArray(key)
        for (`object` in jsonArray.toTypedArray()) {
            if (`object` is Short) list.add(`object`)
        }
        return list
    }

    fun getStringList(key: String): List<String> {
        val list: MutableList<String> = ArrayList()
        val jsonArray = jsonObject.getJSONArray(key)
        for (`object` in jsonArray.toTypedArray()) {
            if (`object` is String) list.add(`object`)
        }
        return list
    }

    fun getSize(): Int {
        return this.jsonObject.size
    }

    override fun toString(): String {
        return this.jsonObject.toJSONString(1)
    }

    fun toString(indentFactor: Int): String {
        if (indentFactor < 0) return JSONObject().toJSONString(1)
        return this.jsonObject.toJSONString(indentFactor);
    }

    fun importFromJsonStr(jsonStr: String) {
        this.importFromJson(JSONObject(jsonStr))
    }

    fun importFromJson(json: JSONObject) {
        for (key in json.keys)
            this[key] = json[key]!!
    }

    fun save() {
        val file = File(path)
        try {
            val write: Writer = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)
            write.write(jsonObject.toJSONString(4))
            write.flush()
            write.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toMap(): Map<String, Any> {
        val map = LinkedHashMap<String, Any>()
        this.jsonObject.keys.forEach { map[it] = this.jsonObject[it]!! }
        return map
    }

    fun getKeys(): Set<String> {
        return this.toMap().keys
    }

    fun getValues(): Collection<Any> {
        return this.toMap().values
    }

}