package cn.hoyobot.sdk.event.villa

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.VillaEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.Member
import cn.hoyobot.sdk.network.protocol.mihoyo.MsgContentInfo
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hoyobot.sdk.network.protocol.type.TextType
import cn.hutool.json.JSONObject

class VillaSendMessageEvent(type: ProtocolEventType) : VillaEvent(type) {

    private var content = ""
    private var senderUID = 0
    private var senderNickName = ""
    private var sendAt = 0
    private var roomID = 0
    private var msgID = ""
    private var botMsgID = ""
    private var villaID = 0

    //https://webstatic.mihoyo.com/vila/bot/doc/callback.html
    //米哈游暂时只支持消息类型
    private var objectName = TextType.MESSAGE

    override fun putString(jsonObject: JSONObject) {
        this.content = jsonObject.getJSONObject("content").toJSONString(0)
        this.senderUID = jsonObject.getInt("from_user_id")
        this.sendAt = jsonObject.getInt("send_at")
        this.roomID = jsonObject.getInt("room_id")
        this.villaID = jsonObject.getInt("villa_id")
        this.senderNickName = jsonObject.getStr("nickname")
        this.msgID = jsonObject.getStr("msg_uid")
        if (jsonObject.containsKey("bot_msg_id")) this.botMsgID = jsonObject.getStr("bot_msg_id")
    }

    fun getJsonMessage(): JSONObject {
        return JSONObject(this.content)
    }

    fun getMsgContentInfo(): MsgContentInfo {
        return MsgContentInfo("").importFromJson(this.getJsonMessage())
    }

    fun getSendAt(): Int {
        return this.sendAt
    }

    fun getRoomID(): Int {
        return this.roomID
    }

    fun getMsgID(): String {
        return this.msgID
    }

    fun getBotMsgID(): String {
        return this.botMsgID
    }

    fun getVilla(): Villa {
        val oldID = HoyoBot.instance.getBot().villaID
        HoyoBot.instance.getBot().villaID = this.villaID.toString()
        val resultVilla = HoyoBot.instance.getBot().getVilla()
        HoyoBot.instance.getBot().villaID = oldID
        return resultVilla
    }

    fun getSenderByMember(): Member {
        return HoyoBot.instance.getBot().getMember(this.senderUID)
    }

    fun getSenderName(): String {
        return this.senderNickName
    }

    fun getMessageType(): TextType {
        return this.objectName
    }

}