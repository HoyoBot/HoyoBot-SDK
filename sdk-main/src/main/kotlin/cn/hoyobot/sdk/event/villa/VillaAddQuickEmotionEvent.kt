package cn.hoyobot.sdk.event.villa

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.VillaEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.Emotion
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hoyobot.sdk.network.protocol.type.EmotionActionType
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hutool.json.JSONObject

class VillaAddQuickEmotionEvent(type: ProtocolEventType) : VillaEvent(type) {

    private var villaID = 0
    private var roomID = 0
    private var uid = 0
    private var emotionID = 0
    private var emotionName = ""
    private var targetMsgID = ""
    private var botMsgID = ""
    private var action = EmotionActionType.DEFAULT

    override fun putString(jsonObject: JSONObject) {
        this.villaID = jsonObject.getInt("villa_id")
        this.roomID = jsonObject.getInt("room_id")
        this.uid = jsonObject.getInt("uid")
        this.emotionID = jsonObject.getInt("emoticon_id")
        this.emotionName = jsonObject.getStr("emoticon")
        this.targetMsgID = jsonObject.getStr("msg_uid")
        this.action = if (jsonObject.getBool("is_cancel")) EmotionActionType.REMOVE_AUDIT else EmotionActionType.ADD_AUDIT
        this.botMsgID = jsonObject.getStr("bot_msg_id")
    }

    fun getEmotion(): Emotion {
        return Emotion(this.emotionID, this.emotionName)
    }

    fun getAction(): EmotionActionType {
        return this.action
    }

    fun getRoomID(): Int {
        return this.roomID
    }

    fun getMsgID(): String {
        return this.targetMsgID
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

}