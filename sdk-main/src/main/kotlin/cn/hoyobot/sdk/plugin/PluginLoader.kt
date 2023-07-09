package cn.hoyobot.sdk.plugin

import cn.hoyobot.sdk.HoyoBot
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Path
import java.util.jar.JarFile

open class PluginLoader(private val pluginManager: PluginManager) {

    fun loadPluginJAR(pluginConfig: PluginYAML, pluginJar: File): Plugin? {

        try {
            val loader = PluginClassLoader(pluginManager, this.javaClass.classLoader, pluginJar)
            pluginManager.pluginClassLoaders[pluginConfig.name] = loader
            val mainClass = loader.loadClass(pluginConfig.main)
            if (!Plugin::class.java.isAssignableFrom(mainClass)) {
                return null
            }
            val castedMain = mainClass.asSubclass(
                Plugin::class.java
            )
            val plugin = castedMain.getDeclaredConstructor().newInstance()
            plugin.init(pluginConfig, pluginManager.getProxy(), pluginJar)
            return plugin
        } catch (e: Exception) {
            HoyoBot.instance.getLogger().error(
                "无法读取插件的主类(main=" + pluginConfig.main + ",plugin=" + pluginConfig.name + ")",
                e
            )
        }
        return null
    }

    fun loadPluginData(file: File, yaml: Yaml): PluginYAML? {
        try {
            JarFile(file).use { pluginJar ->
                var configEntry = pluginJar.getJarEntry("bot.yml")
                if (configEntry == null) {
                    configEntry = pluginJar.getJarEntry("plugin.yml")
                }
                if (configEntry == null) {
                    HoyoBot.instance.getLogger()
                        .warn("插件 " + file.name + " 中没有plugin.yml文件,无法找到主类并进行加载")
                    return null
                }
                pluginJar.getInputStream(configEntry).use { fileStream ->
                    return yaml.loadAs(fileStream, PluginYAML::class.java)
                }
            }
        } catch (e: Exception) {
            HoyoBot.instance.getLogger().error("插件加载失败! " + file.path, e)
        }
        return null
    }

    companion object {
        fun isJarFile(file: Path): Boolean {
            return file.fileName.toString().endsWith(".jar")
        }
    }
}