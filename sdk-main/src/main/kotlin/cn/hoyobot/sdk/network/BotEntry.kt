package cn.hoyobot.sdk.network

import cn.hoyobot.sdk.network.protocol.mihoyo.MihoyoAPI
import cn.hoyobot.sdk.network.protocol.mihoyo.Villa
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON


class BotEntry {

    lateinit var botID: String
    lateinit var botSecret: String
    lateinit var villaID: String

    fun getVilla(): Villa {
        val response = this.request(MihoyoAPI.API_VILLA)
        val jsonObject = JSONObject(response.body())
        val villa = Villa()
        villa.name = jsonObject.getByPath("data.villa.name").toString()
        villa.villaAvatarUrl = jsonObject.getByPath("data.villa.villa_avatar_url").toString()
        villa.isOfficial = jsonObject.getByPath("data.villa.is_official").toString().toBoolean()
        villa.ownerUID = jsonObject.getByPath("data.villa.owner_uid").toString().toInt()
        villa.id = jsonObject.getByPath("data.villa.villa_id").toString().toInt()
        villa.introduce = jsonObject.getByPath("data.villa.introduce").toString()
        (jsonObject.getByPath("data.villa.tags") as JSONArray).forEachIndexed { _, any -> villa.tags.add(any as String) }

        return villa
    }

    fun sendMessage(message: String) {

    }

    private fun request(api: String): HttpResponse {
        var request = HttpRequest(api)
        request.contentType(APPLICATION_JSON.toString())
        val map: MutableMap<String, String> = HashMap()
        map["x-rpc-bot_id"] = this.botID
        map["x-rpc-bot_secret"] = this.botSecret
        map["x-rpc-bot_villa_id"] = this.villaID
        request = request.addHeaders(map)
        return request.execute()

    }
}