package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class GetRoomCommand :
    Command("getroom", CommandSettings.builder().put("/getroom <roomID>", "获取当前大别野指定房间的信息", arrayOf("gr")).build()) {
    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        if (args.size == 1) {
            val uid = Integer.parseInt(args[0])
            val room = HoyoBot.instance.getBot().getRoom(uid)
            val builder = StringBuilder("房间信息查询:").append("\n")
                .append("房间ID: ").append(room.roomID).append("\n")
                .append("房间名称: ").append(room.roomName).append("\n")
            sender.sendMessage(builder.toString())
        } else sender.sendMessage("用法不正确! 请使用 ${this.usageMessage}")
        return true
    }
}