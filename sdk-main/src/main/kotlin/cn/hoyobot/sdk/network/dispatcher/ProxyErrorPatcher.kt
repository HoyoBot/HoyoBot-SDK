package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyRequest
import cn.hoyobot.sdk.network.protocol.ProxyResponse
import cn.hutool.core.util.StrUtil
import io.netty.handler.codec.http.HttpResponseStatus
import java.io.PrintWriter
import java.io.StringWriter


class ProxyErrorPatcher : ProxyActionInterface {

    private val errorPage =
        "<!DOCTYPE html><html><head><title>HoyoBot - Error report</title><style>h1,h3 {color:white; background-color: gray;}</style></head><body><h1>HTTP Status {} - {}</h1><hr size=\"1\" noshade=\"noshade\" /><p>{}</p><hr size=\"1\" noshade=\"noshade\" /><h3>Feedback to HoyoBot</h3></body></html>"


    override fun doAction(request: ProxyRequest, response: ProxyResponse) {
        val eObj = request.getObjParam("_e")
        if (eObj == null) {
            response.sendError(HttpResponseStatus.NOT_FOUND, "404 File not found!")
            return
        }

        if (eObj is Exception) {
            HoyoBot.instance.getLogger().error("Server action internal error!", eObj)
            val writer = StringWriter()
            eObj.printStackTrace(PrintWriter(writer))
            var content = writer.toString().replace("\tat", "&nbsp;&nbsp;&nbsp;&nbsp;\tat")
            content = content.replace("\n", "<br/>\n")
            content = StrUtil.format(errorPage, 500, request.getNettyRequest().uri, content)
            response.sendServerError(content)
            response.setStatus(HttpResponseStatus.OK)
        }
    }
}