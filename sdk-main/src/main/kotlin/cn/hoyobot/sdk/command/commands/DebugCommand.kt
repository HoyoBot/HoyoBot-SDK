package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class DebugCommand : Command("debug", CommandSettings.builder().put("/debug", "开关机器人调试模式", arrayOf("")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        val message = if (HoyoBot.instance.getBot().debug) "调试模式关闭成功!" else "调试模式开启成功!"
        HoyoBot.instance.getBot().debug = !HoyoBot.instance.getBot().debug
        sender.sendMessage(message)
        return true
    }

}