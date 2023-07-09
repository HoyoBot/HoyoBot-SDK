package cn.hoyobot.sdk.plugin

import java.io.File
import java.net.URL
import java.net.URLClassLoader

open class PluginClassLoader(private val pluginManager: PluginManager, parent: ClassLoader, file: File) :
    URLClassLoader(arrayOf<URL>(file.toURI().toURL()), parent) {
    private val classes = HashMap<String, Class<*>>()

    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        return this.findClass(name, true)
    }

    @Throws(ClassNotFoundException::class)
    fun findClass(name: String, checkGlobal: Boolean): Class<*> {
        if (name.startsWith("cn.hoyobot.sdk.")) {
            //防止SDK本身被替代
            throw ClassNotFoundException(name)
        }
        var result = this.classes[name]
        if (result != null) {
            return result
        }
        if (checkGlobal) {
            result = pluginManager.getClassFromCache(name)
        }
        if (super.findClass(name).also { result = it } != null) {
            result?.let { pluginManager.cacheClass(name, it) }
        }
        this.classes[name] = result!!
        return result!!
    }
}