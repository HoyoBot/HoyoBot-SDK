package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hutool.json.JSONObject

interface Message {

    fun build(): JSONObject

}