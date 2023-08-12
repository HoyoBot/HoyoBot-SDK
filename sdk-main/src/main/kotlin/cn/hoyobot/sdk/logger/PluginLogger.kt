package cn.hoyobot.sdk.logger

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.plugin.Plugin

class PluginLogger(val plugin: Plugin) : Logger() {
    fun pluginInfo(message: String) {
        HoyoBot.instance.getLogger().info("[${this.plugin.name}] $message")
    }

    fun pluginError(message: String) {
        HoyoBot.instance.getLogger().error("[${this.plugin.name}] $message")
    }

    fun pluginWarn(message: String) {
        HoyoBot.instance.getLogger().warn("[${this.plugin.name}] $message")
    }

}