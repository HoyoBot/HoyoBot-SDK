package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot
import java.util.*
import kotlin.collections.HashMap


open class SimpleCommandMap(proxy: HoyoBot, prefix: String) : CommandMap {
    private val proxy: HoyoBot
    override val commandPrefix: String
    override val commands: HashMap<String, Command> = HashMap()
    private val aliasesMap = HashMap<String, Command?>()

    init {
        this.proxy = proxy
        commandPrefix = prefix
    }

    @Deprecated("")
    override fun registerCommand(name: String, command: Command): Boolean {
        if (commands.putIfAbsent(name.lowercase(Locale.getDefault()), command) != null) {
            return false
        }
        for (alias in command.aliases) {
            registerAlias(alias, command)
        }
        return true
    }

    override fun registerCommand(command: Command): Boolean {
        if (commands.putIfAbsent(command.name.lowercase(Locale.getDefault()), command) != null) {
            return false
        }
        for (alias in command.aliases) {
            registerAlias(alias, command)
        }
        return true
    }

    override fun registerAlias(name: String, command: Command): Boolean {
        return aliasesMap.putIfAbsent(name.lowercase(Locale.getDefault()), command) == null
    }

    override fun unregisterCommand(name: String): Boolean {
        val command = commands.remove(name.lowercase(Locale.getDefault())) ?: return false
        for (alias in command.aliases) {
            aliasesMap.remove(alias.lowercase(Locale.getDefault()))
        }
        return true
    }

    override fun getCommand(name: String): Command {
        var result = commands[name.lowercase(Locale.getDefault())]
        if (result == null) {
            result = aliasesMap[name.lowercase(Locale.getDefault())]
        }
        return result!!
    }

    override fun isRegistered(name: String): Boolean {
        return commands.containsKey(name.lowercase(Locale.getDefault()))
    }

    override fun handleMessage(sender: CommandSender, message: String): Boolean {
        return !message.trim { it <= ' ' }.isEmpty() && message.startsWith(commandPrefix)
    }

    override fun handleCommand(sender: CommandSender, commandName: String, args: Array<String>): Boolean {
        val command = commands[commandName.lowercase(Locale.getDefault())]
        if (command != null) {
            execute(command, sender, null, args)
            return true
        }
        val aliasCommand = aliasesMap[commandName.lowercase(Locale.getDefault())]
        if (aliasCommand != null) {
            execute(aliasCommand, sender, commandName, args)
            return true
        }
        if (!sender.isPlayer) { // Player commands may be handled by servers
            sender.sendMessage("未知的机器人命令,没有任何对象注册了这个命令")
        }
        return false
    }

    private fun execute(command: Command, sender: CommandSender, alias: String?, args: Array<String>) {
        try {
            val success = command.onExecute(sender, alias!!, args)
            if (!success) {
                sender.sendMessage("该命令正确的用法: " + command.usageMessage)
            }
        } catch (e: Exception) {
            proxy.getLogger().error("在处理命令的时候发生了错误!", e)
        }
    }

    companion object {
        const val DEFAULT_PREFIX = "/"
    }
}