package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class PluginCommand :

    Command("plugins", CommandSettings.builder().put("/plugins", "查看机器人安装的插件列表", arrayOf("")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        val builder = StringBuilder("机器人插件列表(${HoyoBot.instance.getPluginManager().getPluginMap().size}):").append("\n")
        HoyoBot.instance.getPluginManager().plugins.forEach {
            builder.append(it.getDescription().name).append("(").append(it.getDescription().version).append(") ")
        }
        sender.sendMessage(builder.toString().trim())
        return true
    }

}