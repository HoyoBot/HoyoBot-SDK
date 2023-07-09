package cn.hoyobot.sdk.plugin

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.proxy.ProxyPluginEnableEvent
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import org.yaml.snakeyaml.representer.Representer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class PluginManager(proxy: HoyoBot) {
    private val proxy: HoyoBot
    private val pluginLoader: PluginLoader
    val pluginClassLoaders = HashMap<String, PluginClassLoader>()
    private val pluginMap = HashMap<String, Plugin>()
    private val cachedClasses = HashMap<String, Class<*>>()

    init {
        this.proxy = proxy
        pluginLoader = PluginLoader(this)
        try {
            loadPluginsIn(Paths.get(this.proxy.getPluginPath()))
        } catch (e: IOException) {
            this.proxy.getLogger().error("Error while filtering plugin files", e)
        }
    }

    @Throws(IOException::class)
    fun loadPluginsIn(folderPath: Path?) {
        val comparator = Comparator label@{ o1: PluginYAML, o2: PluginYAML ->
            if (o2.name == o1.name) {
                return@label 0
            }
            if (o2.depends.contains(o1.name)) -1 else 1
        }
        val plugins: MutableMap<PluginYAML, Path> = TreeMap(comparator)
        Files.walk(folderPath).filter { path: Path ->
            Files.isRegularFile(
                path
            )
        }.filter(PluginLoader::isJarFile).forEach { jarPath: Path ->
            val config = loadPluginConfig(jarPath)
            if (config != null) {
                plugins[config] = jarPath
            }
        }
        plugins.forEach { (config: PluginYAML, path: Path) ->
            loadPlugin(
                config,
                path
            )
        }
    }

    fun loadPluginConfig(path: Path): PluginYAML? {
        if (!Files.isRegularFile(path) || !PluginLoader.isJarFile(path)) {
            proxy.getLogger().warn("无法加载插件,它不是一个完整的jar包: " + path.fileName)
            return null
        }
        val pluginFile = path.toFile()
        return if (!pluginFile.exists()) {
            null
        } else pluginLoader.loadPluginData(pluginFile, yamlLoader)
    }

    fun loadPlugin(config: PluginYAML, path: Path): Plugin? {
        val pluginFile = path.toFile()
        if (getPluginByName(config.name) != null) {
            proxy.getLogger().warn("该插件已经加载过了: " + config.name)
            return null
        }
        val plugin = pluginLoader.loadPluginJAR(config, pluginFile) ?: return null
        try {
            plugin.onStartup()
        } catch (e: Exception) {
            proxy.getLogger().error("无法加载目标插件 " + config.name + "!", e)
            return null
        }
        proxy.getLogger()
            .info("插件 " + config.name + " 加载成功! (版本=" + config.version + ",作者=" + config.author + ")")
        pluginMap[config.name] = plugin
        HoyoBot.instance.getEventManager().callEvent(ProxyPluginEnableEvent(plugin))
        return plugin
    }

    fun enableAllPlugins() {
        val failed = LinkedList<Plugin?>()
        for (plugin in pluginMap.values) {
            if (!enablePlugin(plugin, null)) {
                failed.add(plugin)
            }
        }
        if (failed.isEmpty()) {
            return
        }
        val builder = StringBuilder("插件加载失败: ")
        while (failed.peek() != null) {
            val plugin = failed.poll()
            builder.append(plugin!!.name)
            if (failed.peek() != null) {
                builder.append(", ")
            }
        }
        proxy.getLogger().warn(builder.toString())
    }

    fun enablePlugin(plugin: Plugin, parent: String?): Boolean {
        if (plugin.isEnabled()) return true
        val pluginName = plugin.name
        this.getProxy().getLogger().info("启动插件 $pluginName 中")
        for (depend in plugin.getDescription().depends) {
            if (depend == parent) {
                proxy.getLogger().warn("无法加载插件 $pluginName 因为循环依赖了Library $parent!")
                return false
            }
            val dependPlugin = getPluginByName(depend)
            if (dependPlugin == null) {
                proxy.getLogger().warn("无法加载插件 $pluginName 因为没安装前置依赖插件 $depend!")
                return false
            }
            if (!dependPlugin.isEnabled() && !enablePlugin(dependPlugin, pluginName)) {
                return false
            }
        }
        try {
            plugin.setEnabled(true)
        } catch (e: Exception) {
            proxy.getLogger().error(e.message, e.cause)
            return false
        }
        return true
    }

    fun disableAllPlugins() {
        for (plugin in pluginMap.values) {
            proxy.getLogger().info("关闭插件 " + plugin.name + "!")
            try {
                plugin.setEnabled(false)
            } catch (e: Exception) {
                proxy.getLogger().error(e.message, e.cause)
            }
        }
    }

    fun getClassFromCache(className: String): Class<*>? {
        val clazz = cachedClasses[className]
        if (clazz != null) {
            return clazz
        }
        for (loader in pluginClassLoaders.values) {
            try {
                return clazz
            } catch (e: ClassNotFoundException) {
                //ignore
            }
        }
        return null
    }

    fun cacheClass(className: String, clazz: Class<*>) {
        cachedClasses.putIfAbsent(className, clazz)
    }

    fun getPluginMap(): Map<String, Plugin> {
        return Collections.unmodifiableMap(pluginMap)
    }

    val plugins: Collection<Plugin>
        get() = Collections.unmodifiableCollection(pluginMap.values)

    fun getPluginByName(pluginName: String): Plugin? {
        return pluginMap.getOrDefault(pluginName, null)
    }

    fun getProxy(): HoyoBot {
        return proxy
    }

    companion object {
        var yamlLoader: Yaml

        init {
            val represent = Representer()
            represent.propertyUtils.setSkipMissingProperties(true)
            yamlLoader = Yaml(CustomClassLoaderConstructor(PluginManager::class.java.classLoader), represent)
        }
    }
}