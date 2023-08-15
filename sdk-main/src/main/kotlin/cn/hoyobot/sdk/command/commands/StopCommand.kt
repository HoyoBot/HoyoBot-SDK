package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class StopCommand : Command("stop", CommandSettings.builder().put("/stop", "关闭机器人", arrayOf()).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        sender.sendMessage("HoyoBot 关闭中...")
        HoyoBot.instance.shutdown()
        return true
    }

}