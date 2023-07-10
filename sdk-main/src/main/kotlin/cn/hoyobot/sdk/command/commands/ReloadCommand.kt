package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class ReloadCommand : Command("reload", CommandSettings.builder().put("/reload", "热重载机器人插件", arrayOf("")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {

        if (sender !is ConsoleCommandSender) return true

        sender.sendMessage("插件热重载中...")
        HoyoBot.instance.getPluginManager().plugins.forEach {
            it.setEnabled(false)
            it.onDisable()
            it.onEnable()
            it.setEnabled(true)
        }
        sender.sendMessage("插件热重载完成!")

        return true
    }


}