package cn.hoyobot.sdk.utils

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.scheduler.FileWriteTask
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import kotlin.collections.ArrayList


class Config {

    private var config: ConfigSection = ConfigSection()
    private val nestedCache: MutableMap<String, Any> = HashMap()
    private lateinit var file: File
    private var isCorrect = false
    private var type = DETECT
    /**
     * Constructor for Config instance with undefined file object
     *
     * @param type - Config type
     */
    /**
     * Constructor for Config (YAML) instance with undefined file object
     */
    @JvmOverloads
    constructor(type: Int = YAML) {
        this.type = type
        isCorrect = true
        config = ConfigSection()
    }

    constructor(file: File) : this(file.toString(), DETECT)
    constructor(file: File, type: Int) : this(file.toString(), type, ConfigSection())

    @Deprecated("")
    constructor(file: String, type: Int, defaultMap: LinkedHashMap<String, Any>) {
        ConfigSection(defaultMap).let { this.load(file, type, it) }
    }

    @JvmOverloads
    constructor(file: String, type: Int = DETECT, defaultMap: ConfigSection = ConfigSection()) {
        this.load(file, type, defaultMap)
    }

    @Deprecated("")
    constructor(file: File, type: Int, defaultMap: LinkedHashMap<String, Any>) : this(
        file.toString(),
        type,
        defaultMap.let { ConfigSection(it) }
    )

    fun reload() {
        config.clear()
        nestedCache.clear()
        isCorrect = false
        //this.load(this.file.toString());
        this.load(file.toString(), type)
    }

    @JvmOverloads
    fun load(file: String?, type: Int = DETECT, defaultMap: ConfigSection = ConfigSection()): Boolean {
        isCorrect = true
        this.type = type
        this.file = File(file)
        if (!this.file.exists()) {
            try {
                this.file.createNewFile()
            } catch (e: IOException) {
                HoyoBot.instance.getLogger().error("Could not create Config " + this.file.toString(), e)
            }
            config = defaultMap
            this.save()
        } else {
            if (this.type == DETECT) {
                var extension = ""
                if (this.file.name.lastIndexOf(".") != -1 && this.file.name.lastIndexOf(".") != 0) {
                    extension = this.file.name.substring(this.file.name.lastIndexOf(".") + 1)
                }
                if (format.containsKey(extension)) {
                    this.type = format[extension]!!
                } else {
                    isCorrect = false
                }
            }
            if (isCorrect) {
                var content = ""
                try {
                    content = Utils.readFile(this.file)
                } catch (e: IOException) {
                    HoyoBot.instance.getLogger().error(e)
                }
                parseContent(content)
                if (!isCorrect) return false
                if (this.setDefault(defaultMap) > 0) {
                    this.save()
                }
            } else {
                return false
            }
        }
        return true
    }

    fun load(inputStream: InputStream?): Boolean {
        if (inputStream == null) return false
        if (isCorrect) {
            var content = ""
            content = try {
                Utils.readFile(inputStream)
            } catch (e: IOException) {
                HoyoBot.instance.getLogger().error(e)
                return false
            }
            parseContent(content)
        }
        return isCorrect
    }

    fun check(): Boolean {
        return isCorrect
    }

    /**
     * Save configuration into provided file. Internal file object will be set to new file.
     *
     * @param file
     * @param async
     * @return
     */
    fun save(file: File, async: Boolean): Boolean {
        this.file = file
        return save(async)
    }

    fun save(file: File): Boolean {
        this.file = file
        return save()
    }

    @JvmOverloads
    fun save(async: Boolean = false): Boolean {
        return if (isCorrect) {
            var content = ""
            when (type) {
                PROPERTIES -> content = writeProperties()
                JSON -> content = GsonBuilder().setPrettyPrinting().create().toJson(config)
                YAML -> {
                    val dumperOptions = DumperOptions()
                    dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                    val yaml = Yaml(dumperOptions)
                    content = yaml.dump(config)
                }
                ENUM -> for (o in config.entries.toSet()) {
                    val (key) = o
                    content += """
                        ${key.toString()}
                        
                        """.trimIndent()
                }
            }
            if (async) {
                HoyoBot.instance.getScheduler().scheduleAsyncTask(FileWriteTask(file, content))
            } else {
                try {
                    Utils.writeFile(file, content)
                } catch (e: IOException) {
                    HoyoBot.instance.getLogger().error(e)
                }
            }
            true
        } else {
            false
        }
    }

    operator fun set(key: String, value: Any) {
        config[key] = value
    }

    operator fun get(key: String): Any? {
        return this.get<Any?>(key, null)
    }

    operator fun <T> get(key: String, defaultValue: T): T {
        return if (isCorrect) config[key, defaultValue] else defaultValue
    }

    fun getSection(key: String): ConfigSection {
        return if (isCorrect) config.getSection(key) else ConfigSection()
    }

    fun isSection(key: String): Boolean {
        return config.isSection(key)
    }

    fun getSections(key: String): ConfigSection {
        return if (isCorrect) config.getSections(key) else ConfigSection()
    }

    fun getInt(key: String): Int {
        return this.getInt(key, 0)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return if (isCorrect) config.getInt(key, defaultValue) else defaultValue
    }

    fun isInt(key: String): Boolean {
        return config.isInt(key)
    }

    fun getLong(key: String): Long {
        return this.getLong(key, 0)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return if (isCorrect) config.getLong(key, defaultValue) else defaultValue
    }

    fun isLong(key: String): Boolean {
        return config.isLong(key)
    }

    fun getDouble(key: String): Double {
        return this.getDouble(key, 0.0)
    }

    fun getDouble(key: String, defaultValue: Double): Double {
        return if (isCorrect) config.getDouble(key, defaultValue) else defaultValue
    }

    fun isDouble(key: String): Boolean {
        return config.isDouble(key)
    }

    fun getString(key: String): String {
        return this.getString(key, "")
    }

    fun getString(key: String, defaultValue: String): String {
        return if (isCorrect) config.getString(key, defaultValue) else defaultValue!!
    }

    fun isString(key: String): Boolean {
        return config.isString(key)
    }

    fun getBoolean(key: String): Boolean {
        return this.getBoolean(key, false)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (isCorrect) config.getBoolean(key, defaultValue) else defaultValue
    }

    fun isBoolean(key: String): Boolean {
        return config.isBoolean(key)
    }

    fun getList(key: String): List<String> {
        return this.getList(key, ArrayList())
    }

    fun getList(key: String, defaultList: List<String>): List<String> {
        return if (isCorrect) config.getList(key, defaultList) else defaultList
    }

    fun isList(key: String): Boolean {
        return config.isList(key)
    }

    fun getStringList(key: String): List<String> {
        return config.getStringList(key)
    }

    fun getIntegerList(key: String): List<Int> {
        return config.getIntegerList(key)
    }

    fun getBooleanList(key: String): List<Boolean> {
        return config.getBooleanList(key)
    }

    fun getDoubleList(key: String): List<Double> {
        return config.getDoubleList(key)
    }

    fun getFloatList(key: String): List<Float> {
        return config.getFloatList(key)
    }

    fun getLongList(key: String): List<Long> {
        return config.getLongList(key)
    }

    fun getByteList(key: String): List<Byte> {
        return config.getByteList(key)
    }

    fun getCharacterList(key: String): List<Char> {
        return config.getCharacterList(key)
    }

    fun getShortList(key: String): List<Short> {
        return config.getShortList(key)
    }

    fun setAll(map: LinkedHashMap<String, Any>) {
        config = ConfigSection(map)
    }

    fun exists(key: String): Boolean {
        return config.exists(key)
    }

    fun exists(key: String, ignoreCase: Boolean): Boolean {
        return config.exists(key, ignoreCase)
    }

    fun remove(key: String) {
        config.remove(key)
    }

    /**
     * Get root (main) config section of the Config
     *
     * @return
     */
    val rootSection: ConfigSection
        get() = config

    fun setDefault(map: LinkedHashMap<String, Any>): Int {
        return setDefault(ConfigSection(map))
    }

    fun setDefault(map: ConfigSection): Int {
        val size: Int = config.size
        config = fillDefaults(map, config)
        return config.size - size
    }

    private fun fillDefaults(defaultMap: ConfigSection, data: ConfigSection): ConfigSection {
        for (key in defaultMap.keys.toSet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap[key])
            }
        }
        return data
    }

    private fun parseList(content: String) {
        var content = content
        content = content.replace("\r\n", "\n")
        for (v in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (v.trim { it <= ' ' }.isEmpty()) {
                continue
            }
            config.put(v, true)
        }
    }

    private fun writeProperties(): String {
        var content = """
            #Properties Config file
            #${SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date())}
            
            """.trimIndent()
        for (o in config.entries.toSet()) {
            val (key, value) = o
            var v = value
            val k = key
            if (v is Boolean) {
                v = if (v) "on" else "off"
            }
            content += "$k=$v\r\n"
        }
        return content
    }

    private fun parseProperties(content: String) {
        for (line in content.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (Pattern.compile("[a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                val b = line.split("=".toRegex()).toTypedArray()
                val k = b[0]
                val v = b[1].trim { it <= ' ' }
                val v_lower = v.lowercase(Locale.getDefault())
                if (config.containsKey(k)) {
                    HoyoBot.instance.getLogger()
                        .debug("[Config] Repeated property $k on file $file")
                }
                when (v_lower) {
                    "on", "true", "yes" -> config.put(k, true)
                    "off", "false", "no" -> config.put(k, false)
                    else -> config.put(k, v)
                }
            }
        }
    }

    @Deprecated("use {@link #get(String, T)} instead")
    fun <T> getNested(key: String, defaultValue: T): T {
        return get(key, defaultValue)
    }

    @Deprecated("use {@link #get(String)} instead")
    fun <T> getNestedAs(key: String, type: Class<T>): T {
        return get(key) as T
    }

    @Deprecated("use {@link #remove(String)} instead")
    fun removeNested(key: String) {
        remove(key)
    }

    private fun parseContent(content: String) {
        when (type) {
            PROPERTIES -> parseProperties(content)
            JSON -> {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                config = ConfigSection(
                    gson.fromJson(
                        content,
                        object : TypeToken<LinkedHashMap<String?, Any?>?>() {}.type
                    )
                )
            }
            YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                config = ConfigSection(yaml.loadAs(content, LinkedHashMap::class.java))
            }
            ENUM -> parseList(content)
            else -> isCorrect = false
        }
    }

    val keys: Set<String>
        get() = if (isCorrect) config.keys else HashSet()

    fun getKeys(child: Boolean): Set<String> {
        return if (isCorrect) config.getKeys(child) else HashSet()
    }

    companion object {
        const val DETECT = -1 //Detect by file extension
        const val PROPERTIES = 0 // .properties
        const val CNF = PROPERTIES // .cnf
        const val JSON = 1 // .js, .json
        const val YAML = 2 // .yml, .yaml

        //public static final int EXPORT = 3; // .export, .xport
        //public static final int SERIALIZED = 4; // .sl
        const val ENUM = 5 // .txt, .list, .enum
        const val ENUMERATION = ENUM
        var format: MutableMap<String, Int> = TreeMap()

        init {
            format["properties"] = PROPERTIES
            format["con"] = PROPERTIES
            format["conf"] = PROPERTIES
            format["config"] = PROPERTIES
            format["js"] = JSON
            format["json"] = JSON
            format["yml"] = YAML
            format["yaml"] = YAML
            format["txt"] = ENUM
            format["list"] = ENUM
            format["enum"] = ENUM
        }
    }
}