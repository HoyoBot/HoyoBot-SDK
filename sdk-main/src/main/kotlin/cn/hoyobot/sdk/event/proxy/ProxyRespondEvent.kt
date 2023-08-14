package cn.hoyobot.sdk.event.proxy

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.ProxyEvent
import cn.hutool.json.JSONObject

class ProxyRespondEvent(bot: HoyoBot, private val data: JSONObject) : ProxyEvent() {

    val botEntry = bot.getBot()
    var headers: Map<String, String> = HashMap()
    private var used = false

    fun isUsed(): Boolean {
        return this.used
    }

    fun setUsed() {
        this.used = true
    }

    fun getData(): JSONObject {
        return this.data
    }

}