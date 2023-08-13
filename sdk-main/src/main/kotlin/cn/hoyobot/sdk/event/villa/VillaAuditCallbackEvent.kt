package cn.hoyobot.sdk.event.villa

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.VillaEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hoyobot.sdk.network.protocol.type.AuditResult
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hutool.json.JSONObject

class VillaAuditCallbackEvent(type: ProtocolEventType) : VillaEvent(type) {

    private var auditID = 0
    private var botID = ""
    private var villaID = 0
    private var roomID = 0
    private var uid = 0
    private var passThrough = ""
    private var result = AuditResult.DEFAULT

    override fun putString(jsonObject: JSONObject) {
        this.auditID = jsonObject.getInt("audit_id")
        this.botID = jsonObject.getStr("bot_tpl_id")
        this.villaID = jsonObject.getInt("villa_id")
        this.roomID = jsonObject.getInt("room_id")
        this.uid = jsonObject.getInt("user_id")
        this.passThrough = jsonObject.getStr("pass_through")
        this.result = AuditResult.getResultByID(jsonObject.getInt("audit_result"))
    }

    fun getResult(): AuditResult {
        return this.result
    }

    fun getPassThrough(): String {
        return this.passThrough
    }

    fun getUID(): Int {
        return this.uid
    }

    fun getRoomID(): Int {
        return this.roomID
    }

    fun getVilla(): Villa {
        val oldID = HoyoBot.instance.getBot().villaID
        HoyoBot.instance.getBot().villaID = this.villaID.toString()
        val resultVilla = HoyoBot.instance.getBot().getVilla()
        HoyoBot.instance.getBot().villaID = oldID
        return resultVilla
    }

    fun getAuditID(): Int {
        return this.auditID
    }

    fun getBotID(): String {
        return this.botID
    }

}