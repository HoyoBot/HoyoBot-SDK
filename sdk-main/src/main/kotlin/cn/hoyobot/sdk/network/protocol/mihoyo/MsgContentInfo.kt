package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hutool.json.JSONObject

class MsgContentInfo(val value: String) : Message {

    override fun build(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.putByPath("content.text", this.value)
        val emptyArray: Array<String> = emptyArray()
        jsonObject.putByPath("content.entities", emptyArray)
        return jsonObject
    }

}