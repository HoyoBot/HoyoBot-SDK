package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.commands.*

class DefaultCommandMap(proxy: HoyoBot, prefix: String) : SimpleCommandMap(proxy, prefix) {
    init {
        registerDefaults()
    }

    private fun registerDefaults() {
        this.registerCommand(PluginCommand())
        this.registerCommand(VersionCommand())
        this.registerCommand(HelpCommand())
        this.registerCommand(ReloadCommand())
        this.registerCommand(DebugCommand())
        this.registerCommand(GetMemberCommand())
        this.registerCommand(GetRoomCommand())
        this.registerCommand(GetVillaCommand())
        this.registerCommand(SendCommand())
    }
}