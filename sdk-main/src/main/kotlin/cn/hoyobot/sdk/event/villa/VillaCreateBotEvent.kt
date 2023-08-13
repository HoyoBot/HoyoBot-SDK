package cn.hoyobot.sdk.event.villa

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.VillaEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hutool.json.JSONObject

class VillaCreateBotEvent(type: ProtocolEventType) : VillaEvent(type) {

    private var villaID = 0

    override fun putString(jsonObject: JSONObject) {
        this.villaID = jsonObject.getInt("villa_id")
    }

    fun getVilla(): Villa {
        val oldID = HoyoBot.instance.getBot().villaID
        HoyoBot.instance.getBot().villaID = this.villaID.toString()
        val resultVilla = HoyoBot.instance.getBot().getVilla()
        HoyoBot.instance.getBot().villaID = oldID
        return resultVilla
    }

}