package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender
import cn.hoyobot.sdk.network.RaknetInfo

class VersionCommand : Command("version", CommandSettings.builder().put("/version", "查看机器人框架版本", arrayOf("")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true

        sender.sendMessage("HoyoBot ${HoyoBot.instance.getVersion()} 协议版本:${RaknetInfo.PROTOCOL_VERSION}")

        return true;
    }


}