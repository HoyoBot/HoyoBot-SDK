package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender
import cn.hoyobot.sdk.network.protocol.mihoyo.MsgContentInfo
import cn.hoyobot.sdk.network.protocol.type.TextType

class SendCommand : Command(
    "send", CommandSettings.builder().put(
        "/send <选填:大别野ID> <房间ID> <消息>", "发送一条自定义消息", arrayOf(
            "sm", "sendmessage"
        )
    ).build()
) {

    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {
        if (sender !is ConsoleCommandSender) return true
        var success = false
        when (args.size) {
            2 -> {
                val message = args[1]
                val roomID = Integer.parseInt(args[0])
                HoyoBot.instance.getBot().sendMessage(roomID, MsgContentInfo(message), TextType.MESSAGE)
                success = true
            }
            3 -> {
                val message = args[2]
                val roomID = Integer.parseInt(args[1])
                val villaID = args[0]
                val resetID = HoyoBot.instance.getBot().villaID
                HoyoBot.instance.getBot().villaID = villaID
                HoyoBot.instance.getBot().sendMessage(roomID, MsgContentInfo(message), TextType.MESSAGE)
                HoyoBot.instance.getBot().villaID = resetID
                success = true
            }
        }
        if (success) sender.sendMessage("消息发送成功!") else
            sender.sendMessage("消息发送失败! 正确用法: ${this.usageMessage}")
        return true
    }

}