package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.BotEvent
import cn.hoyobot.sdk.event.villa.*
import cn.hoyobot.sdk.network.protocol.ProxyActionInterface
import cn.hoyobot.sdk.network.protocol.ProxyRequest
import cn.hoyobot.sdk.network.protocol.ProxyResponse
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType
import cn.hutool.json.JSONObject
import io.netty.handler.codec.http.HttpResponseStatus

class VillaPatcher : ProxyActionInterface {
    override fun doAction(request: ProxyRequest, response: ProxyResponse) {

        //HoyoBot.instance.getLogger().debug("Villa: $request")
        val requestJson = request.jsonData
        //HoyoBot.instance.getLogger()
        //    .info("JsonData: size: ${request.getParams().size}\n ${requestJson.toJSONString(4)}")
        //处理来自米哈游的请求
        if (!requestJson.containsKey("event")) {
            HoyoBot.instance.getLogger().error("无法处理回调事件! $request")
            response.setStatus(404)
            return
        }
        try {
            val eventID = requestJson.getByPath("event.type").toString().toInt()
            val protocolType = ProtocolEventType.getTypeByID(eventID)
            val villaEvent: BotEvent = when (protocolType) {
                ProtocolEventType.JOIN_VILLA -> VillaMemberJoinEvent(ProtocolEventType.JOIN_VILLA)
                ProtocolEventType.ADD_QUICK_EMOTION -> VillaAddQuickEmotionEvent(ProtocolEventType.ADD_QUICK_EMOTION)
                ProtocolEventType.AUDIT_CALL_BACK -> VillaAuditCallbackEvent(ProtocolEventType.AUDIT_CALL_BACK)
                ProtocolEventType.CREATE_ROBOT -> VillaCreateBotEvent(ProtocolEventType.CREATE_ROBOT)
                ProtocolEventType.DELETE_ROBOT -> VillaDeleteBotEvent(ProtocolEventType.DELETE_ROBOT)
                ProtocolEventType.SEND_MESSAGE -> VillaSendMessageEvent(ProtocolEventType.SEND_MESSAGE)
                else -> return
            }
            villaEvent.putString(requestJson.getByPath("event.extend_data.EventData.${protocolType.getEventName()}") as JSONObject)
            HoyoBot.instance.getEventManager().callEvent(villaEvent)

        } catch (e: Exception) {
            HoyoBot.instance.getLogger().error("处理事件的时候出现错误!", e)
        }

        val responseJson = JSONObject()
        responseJson["message"] = "回调处理成功!"
        responseJson["retcode"] = 0
        response.setJsonContent(responseJson.toJSONString(4))
        response.setStatus(HttpResponseStatus.OK)
    }
}