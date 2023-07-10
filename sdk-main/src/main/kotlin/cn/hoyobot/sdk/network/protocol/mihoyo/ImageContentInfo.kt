package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hutool.json.JSONObject

class ImageContentInfo : Message {

    var url = ""
    var height = 0
    var width = 0

    override fun build(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.putByPath("content.url", this.url)
        jsonObject.putByPath("content.size.width", this.width)
        jsonObject.putByPath("content.size.height", this.height)
        return jsonObject
    }

}