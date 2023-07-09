package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyRequest
import cn.hoyobot.sdk.network.protocol.ProxyResponse
import cn.hutool.json.JSONObject
import io.netty.handler.codec.http.HttpResponseStatus

class VillaPatcher : ProxyActionInterface {
    override fun doAction(request: ProxyRequest, response: ProxyResponse) {

        //HoyoBot.instance.getLogger().debug("Villa: $request")
        val requestJson = request.jsonData
        //HoyoBot.instance.getLogger()
        //    .info("JsonData: size: ${request.getParams().size}\n ${requestJson.toJSONString(4)}")
        //处理来自米哈游的请求


        val responseJson = JSONObject()
        responseJson["message"] = ""
        responseJson["retcode"] = 0
        response.setJsonContent(responseJson.toJSONString(4))
        response.setStatus(HttpResponseStatus.OK)
    }
}