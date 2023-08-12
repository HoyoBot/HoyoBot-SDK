package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class GetMemberCommand :
    Command("getmember", CommandSettings.builder().put("/getmember <UID>", "获取指定用户的信息", arrayOf("gm")).build()) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        if (args.size == 1) {
            val uid = Integer.parseInt(args[0])
            val member = HoyoBot.instance.getBot().getMember(uid)
            val builder = StringBuilder("用户信息查询:").append("\n")
                .append("UID: ").append(member.uid).append("\n")
                .append("用户名: ").append(member.name).append("\n")
                .append("简介: ").append(member.introduce).append("\n")
                .append("加入时间: ").append(member.joinAt).append("\n")
                .append("头像链接: ").append(member.avatarUrl)
            sender.sendMessage(builder.toString())
        } else sender.sendMessage("用法不正确! 请使用 ${this.usageMessage}")
        return true
    }

}