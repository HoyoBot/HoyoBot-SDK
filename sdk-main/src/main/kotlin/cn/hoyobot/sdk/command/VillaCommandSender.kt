package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.mihoyo.MsgContentInfo
import cn.hoyobot.sdk.network.protocol.type.MessageEntityType
import cn.hoyobot.sdk.network.protocol.type.TextType

class VillaCommandSender(private val roomID: Int, private val uid: Int) : CommandSender {

    override val name: String
        get() = HoyoBot.instance.getBot().getMember(uid).name
    override val isPlayer: Boolean
        get() = false

    override fun sendMessage(message: String) {
        HoyoBot.instance.getBot().sendMessage(
            roomID,
            MsgContentInfo("").appendMentionedMessage(uid, MessageEntityType.MENTIONED_USER).append(" $message"),
            TextType.MESSAGE
        );
    }

    fun getProxy(): HoyoBot {
        return HoyoBot.instance
    }

}