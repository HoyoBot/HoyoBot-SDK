package cn.hoyobot.sdk.event.villa

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.VillaEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.Member
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hutool.json.JSONObject

class VillaMemberJoinEvent(type: ProtocolEventType) : VillaEvent(type) {

    private var joinUID = 0
    private var joinUserNickName = ""
    private var joinAt = 0
    private var villaID = 0

    override fun putString(jsonObject: JSONObject) {
        this.joinUID = jsonObject.getInt("join_uid")
        this.joinUserNickName = jsonObject.getStr("join_user_nickname")
        this.joinAt = jsonObject.getInt("join_at")
        this.villaID = jsonObject.getInt("villa_id")
    }

    fun getMember(): Member {
        return HoyoBot.instance.getBot().getMember(this.joinUID)
    }

    fun getMemberNickName(): String {
        return this.joinUserNickName
    }

    fun getVilla(): Villa {
        val oldID = HoyoBot.instance.getBot().villaID
        HoyoBot.instance.getBot().villaID = this.villaID.toString()
        val resultVilla = HoyoBot.instance.getBot().getVilla()
        HoyoBot.instance.getBot().villaID = oldID
        return resultVilla
    }

    fun getJoinAt(): Int {
        return this.joinAt
    }

}