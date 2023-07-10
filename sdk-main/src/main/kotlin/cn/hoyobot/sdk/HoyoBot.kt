package cn.hoyobot.sdk

import cn.hoyobot.sdk.command.*
import cn.hoyobot.sdk.command.console.TerminalConsole
import cn.hoyobot.sdk.event.EventManager
import cn.hoyobot.sdk.event.proxy.ProxyBotStartEvent
import cn.hoyobot.sdk.logger.Logger
import cn.hoyobot.sdk.network.BotEntry
import cn.hoyobot.sdk.network.RaknetInterface
import cn.hoyobot.sdk.plugin.PluginManager
import cn.hoyobot.sdk.scheduler.BotScheduler
import cn.hoyobot.sdk.utils.Config
import cn.hoyobot.sdk.utils.ConfigSection
import cn.hutool.log.Log
import cn.hutool.log.LogFactory
import lombok.Getter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates
import kotlin.system.exitProcess


@Getter
open class HoyoBot {

    companion object {
        lateinit var instance: HoyoBot
    }

    private var address = "0.0.0.0"
    private var port = 80
    private var handlerPath = "/bot"
    private val version = "1.0.0"
    private val path = System.getProperty("user.dir") + "/"
    private val pluginPath = path + "plugins"
    private val subLogger: Log = LogFactory.get("HoyoBot")
    private lateinit var logger: Logger
    private var isRunning = false
    private lateinit var botScheduler: BotScheduler
    private lateinit var eventManager: EventManager
    private lateinit var pluginManager: PluginManager
    private lateinit var consoleSender: CommandSender
    private lateinit var commandMap: CommandMap
    private lateinit var console: TerminalConsole
    private var botEntry: BotEntry = BotEntry()
    private lateinit var properties: Config
    private var runningTime by Delegates.notNull<Long>()
    private lateinit var raknetInterface: RaknetInterface
    private var currentTick = 0
    private var discardOldLogs = 1
    private var httpFilter = false

    fun initBotProxy() {
        instance = this
        this.logger = Logger()
        this.runningTime = System.currentTimeMillis()
        this.logger.info("HoyoBot - ${this.version}")
        this.logger.info("在开源仓库寻找更新: https://github.com/HoyoBot/HoyoBot-SDK")

        if (!File(pluginPath).exists()) {
            File(pluginPath).mkdirs()
        }

        this.logger.info("加载 HoyoBot properties 中...")
        properties = Config(this.path + "bot.properties", Config.PROPERTIES, object : ConfigSection() {
            init {
                put("bot_id", "")
                put("bot_secret", "")
                put("server-ip", "0:0:0:0")
                put("port", 80)
                put("villa-id", "0")
                put("discard-old-logs-days", "1")
                put("http_filter", false)
                put("http_call_back", "/bot")
            }
        })
        this.botEntry.botID = this.properties.getString("bot_id")
        this.botEntry.botSecret = this.properties.getString("bot_secret")
        this.botEntry.villaID = this.properties.getString("villa-id")
        this.address = this.properties.getString("server-ip")
        this.port = this.properties.getString("port").toInt()
        this.discardOldLogs = this.properties.getString("discard-old-logs-days").toInt()
        this.handlerPath = this.properties.getString("http_call_back")
        this.httpFilter = this.properties.getBoolean("http_filter", false)
        this.logger.info("HoyoBot已成功在米游社创建!")

        this.botScheduler = BotScheduler()
        this.eventManager = EventManager(this)
        this.properties.save(true)

        this.raknetInterface = RaknetInterface(this)
        this.raknetInterface.start()

        this.getLogger().info("加载插件中...")
        this.pluginManager = PluginManager(this)
        this.getPluginManager().enableAllPlugins()

        this.commandMap = DefaultCommandMap(this, SimpleCommandMap.DEFAULT_PREFIX)
        this.console = TerminalConsole(this)
        this.consoleSender = ConsoleCommandSender(this)

        this.getEventManager().callEvent(ProxyBotStartEvent(this))
        this.initProxy()
    }

    private fun initProxy() {
        this.getLogger().info("总共加载了 ${this.getPluginManager().getPluginMap().size} 个插件")
        this.isRunning = true
        this.getLogger()
            .info("完成! HoyoBot 正运行在 " + address + ":" + port + "上. (耗时:" + ((System.currentTimeMillis() - this.runningTime) / 1000).toDouble() + "秒)")
        this.console.consoleThread.start()
        this.tickProcessor()
    }

    open fun dispatchCommand(sender: CommandSender, message: String): Boolean {
        if (message.trim { it <= ' ' }.isEmpty()) return false
        val args = message.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (args.isEmpty()) return false
        if (!this.commandMap.commands.containsKey(args[0].lowercase(Locale.getDefault()))) {
            sender.sendMessage("未知的机器人命令${args[0]},没有任何对象注册了这个命令")
            return false
        }
        val command = getCommandMap().getCommand(args[0])
        var shiftedArgs = arrayOf("")
        if (command.settings.isQuoteAware) {
            val `val`: ArrayList<String> = this.parseArguments(message)
            `val`.removeAt(0)
            shiftedArgs = `val`.toArray(arrayOf())
        }
        return commandMap.handleCommand(sender, args[0], shiftedArgs)
    }

    private fun parseArguments(cmdLine: String?): ArrayList<String> {
        val sb = StringBuilder(cmdLine)
        val args = ArrayList<String>()
        var notQuoted = true
        var start = 0
        var i = 0
        while (i < sb.length) {
            if (sb[i] == '\\') {
                sb.deleteCharAt(i)
                i++
                continue
            }
            if (sb[i] == ' ' && notQuoted) {
                val arg = sb.substring(start, i)
                if (arg.isNotEmpty()) {
                    args.add(arg)
                }
                start = i + 1
            } else if (sb[i] == '"') {
                sb.deleteCharAt(i)
                --i
                notQuoted = !notQuoted
            }
            i++
        }
        val arg = sb.substring(start)
        if (arg.isNotEmpty()) {
            args.add(arg)
        }
        return args
    }

    fun shutdown() {
        this.logger.info("关闭HoyoBot中...")
        isRunning = false
        this.pluginManager.disableAllPlugins()
        exitProcess(0)
    }

    fun getBot(): BotEntry {
        return this.botEntry
    }

    open fun tickProcessor() {
        while (isRunning) {
            tick()
            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
                logger.error(e.stackTraceToString())
            }
        }
    }

    fun getEventManager(): EventManager {
        return this.eventManager
    }

    private fun tick() {
        ++this.currentTick
        this.getScheduler().mainThreadHeartbeat(this.currentTick)
    }

    fun getLogger(): Logger {
        return this.logger
    }

    fun getSubLogger(): Log {
        return this.subLogger
    }

    fun getPluginPath(): String {
        return this.pluginPath
    }

    fun getScheduler(): BotScheduler {
        return this.botScheduler
    }

    fun getPort(): Int {
        return this.port
    }

    fun getHttpCallBackPath(): String {
        return this.handlerPath
    }

    fun getVersion(): String {
        return this.version
    }

    fun isEnabledFilter(): Boolean {
        return this.httpFilter
    }

    fun getVillaID(): String {
        return this.botEntry.villaID
    }

    fun getPluginManager(): PluginManager {
        return this.pluginManager
    }

    fun getPath(): String {
        return this.path
    }

    fun getDiscardOldLogsDays(): Int {
        return this.discardOldLogs
    }

    fun isRunning(): Boolean {
        return this.isRunning
    }

    fun getConsoleSender(): CommandSender {
        return this.consoleSender
    }

    fun getCommandMap(): CommandMap {
        return this.commandMap
    }

}