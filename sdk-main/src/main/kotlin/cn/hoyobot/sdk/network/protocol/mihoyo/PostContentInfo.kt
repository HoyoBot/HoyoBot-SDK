package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hutool.json.JSONObject

class PostContentInfo : Message {

    var postID = ""

    override fun build(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.putByPath("content.post_id", this.postID)
        return jsonObject
    }

}