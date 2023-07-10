package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.commands.PluginCommand
import cn.hoyobot.sdk.command.commands.VersionCommand

class DefaultCommandMap(proxy: HoyoBot, prefix: String) : SimpleCommandMap(proxy, prefix) {
    init {
        registerDefaults()
    }

    private fun registerDefaults() {
        this.registerCommand(PluginCommand())
        this.registerCommand(VersionCommand())
    }
}