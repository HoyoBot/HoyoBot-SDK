package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyRequest
import cn.hoyobot.sdk.network.protocol.ProxyResponse
import io.netty.handler.codec.http.HttpResponseStatus


class DefaultHttpPatcher : ProxyActionInterface {

    override fun doAction(request: ProxyRequest, response: ProxyResponse) {
        response.setContent("HoyoBot Server ${HoyoBot.instance.getVersion()}")
        HoyoBot.instance.getLogger().info("Default: $request")
        //https://webstatic.mihoyo.com/vila/bot/doc/flow.html
        //开发者应返回状态码200
        response.setStatus(HttpResponseStatus.OK)
    }

}