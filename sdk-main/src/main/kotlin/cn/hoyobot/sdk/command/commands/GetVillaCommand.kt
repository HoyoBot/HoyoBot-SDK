package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class GetVillaCommand :
    Command("getvilla", CommandSettings.builder().put("/getvilla", "获取当前大别野的信息", arrayOf("gv")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        val villa = HoyoBot.instance.getBot().getVilla()
        val builder = StringBuilder("大别野信息查询:").append("\n")
            .append("名称: ").append(villa.name).append("\n")
            .append("ID: ").append(villa.id).append("\n")
            .append("房东: ").append(villa.name).append("\n")
            .append("房东UID: ").append(villa.ownerUID).append("\n")
            .append("官方频道: ").append(villa.isOfficial).append("\n")
            .append("简介: ").append(villa.introduce).append("\n")
            .append("头像链接: ").append(villa.villaAvatarUrl)
        sender.sendMessage(builder.toString())
        return true
    }

}