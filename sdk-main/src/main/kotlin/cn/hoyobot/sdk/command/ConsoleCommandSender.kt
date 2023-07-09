package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot

class ConsoleCommandSender(val botProxy: HoyoBot) : CommandSender {

    override val name: String
        get() = "Console"
    override val isPlayer: Boolean
        get() = false

    override fun sendMessage(message: String) {
        for (line in message.trim { it <= ' ' }.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            botProxy.getLogger().info(line)
        }
    }

    fun getProxy(): HoyoBot {
        return botProxy
    }
}