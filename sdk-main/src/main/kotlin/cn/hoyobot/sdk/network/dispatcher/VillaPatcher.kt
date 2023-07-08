package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyRequest
import cn.hoyobot.sdk.network.protocol.ProxyResponse
import cn.hutool.json.JSONObject
import io.netty.handler.codec.http.HttpResponseStatus

class VillaPatcher : ProxyActionInterface {
    override fun doAction(request: ProxyRequest, response: ProxyResponse) {
        HoyoBot.instance.getLogger().info("Villa: $request")
        val responseJson = JSONObject()
        responseJson["message"] = ""
        responseJson["retcode"] = 0
        response.setJsonContent(responseJson.toJSONString(0))
        response.setStatus(HttpResponseStatus.OK)
    }
}