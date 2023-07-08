package cn.hoyobot.sdk.network

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.dispatcher.DefaultHttpPatcher
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyFilter
import cn.hoyobot.sdk.network.protocol.ProxyRoute
import cn.hutool.core.io.FileUtil
import cn.hutool.core.lang.Singleton
import cn.hutool.core.util.StrUtil
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentHashMap


object RaknetInfo {

    const val DEFAULT_CHARSET = "utf-8"
    const val MAPPING_ALL = "/*"
    const val MAPPING_ERROR = "/_error"
    var charset = DEFAULT_CHARSET
    var port = 80
    lateinit var root: File
    private var filterMap: MutableMap<String, ProxyFilter> = ConcurrentHashMap<String, ProxyFilter>()
    private var actionMap: MutableMap<String, ProxyActionInterface> = ConcurrentHashMap<String, ProxyActionInterface>()

    init {
        actionMap[StrUtil.SLASH] = DefaultHttpPatcher()
        actionMap[MAPPING_ERROR] = DefaultHttpPatcher()
    }

    fun charset(): Charset {
        return Charset.forName(charset)
    }

    val isRootAvailable: Boolean = root.isDirectory && root.isHidden == false && root.canRead()
    val rootPath: String = FileUtil.getAbsolutePath(root)

    fun setRoot(root: String?) {
        RaknetInfo.root = FileUtil.mkdir(root)
        HoyoBot.instance.getLogger().debug("Set root to [{}]", RaknetInfo.root.absolutePath)
    }

    fun setRoot(root: File) {
        if (!root.exists()) {
            root.mkdirs()
        } else if (!root.isDirectory) {
            throw Exception(StrUtil.format("{} is not a directory!", root.path))
        }
        RaknetInfo.root = root
    }

    private val proxyFilterMap: Map<String, Any>
        get() = filterMap

    fun getProxyFilter(path: String): ProxyFilter? {
        var path = path
        if (StrUtil.isBlank(path)) {
            path = StrUtil.SLASH
        }
        return proxyFilterMap[path.trim { it <= ' ' }] as ProxyFilter?
    }

    fun setProxyFilterMap(filterMap: MutableMap<String, ProxyFilter>) {
        RaknetInfo.filterMap = filterMap
    }

    fun setProxyFilter(path: String, filter: ProxyFilter) {
        var path = path
        if (StrUtil.isBlank(path)) {
            path = StrUtil.SLASH
        }
        if (!path.startsWith(StrUtil.SLASH)) {
            path = StrUtil.SLASH + path
        }
        filterMap[path] = filter
    }

    fun setProxyFilter(path: String, filterClass: Class<out ProxyFilter>) {
        setProxyFilter(path, Singleton.get(filterClass) as ProxyFilter)
    }

    fun getActionMap(): Map<String, ProxyActionInterface> {
        return actionMap
    }

    fun getAction(path: String): ProxyActionInterface? {
        var path = path
        if (StrUtil.isBlank(path)) {
            path = StrUtil.SLASH
        }
        return getActionMap()[path.trim { it <= ' ' }]
    }

    fun setActionMap(actionMap: MutableMap<String, ProxyActionInterface>) {
        RaknetInfo.actionMap = actionMap
    }

    fun setAction(path: String, action: ProxyActionInterface) {
        var path = path
        if (StrUtil.isBlank(path)) {
            path = StrUtil.SLASH
        }
        if (!path.startsWith(StrUtil.SLASH)) {
            path = StrUtil.SLASH + path
        }
        actionMap[path] = action
    }

    fun setAction(path: String, actionClass: Class<out ProxyActionInterface>) {
        Singleton.get(actionClass)?.let { setAction(path, it) }
    }

    fun setAction(action: ProxyActionInterface) {
        val route: ProxyRoute = action.javaClass.getAnnotation(ProxyRoute::class.java)
        val path: String = route.value
        if (StrUtil.isNotBlank(path)) {
            setAction(path, action)
            return
        }
    }

    fun setAction(actionClass: Class<out ProxyActionInterface>) {
        setAction(Singleton.get(actionClass) as ProxyActionInterface)
    }
}