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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.*


class VillaPatcher : ProxyActionInterface {
    override fun doAction(request: ProxyRequest, response: ProxyResponse) {

        //HoyoBot.instance.getLogger().debug("Villa: $request")
        val requestJson = request.jsonData
        //处理来自米哈游的请求
        if (!requestJson.containsKey("event")) {
            HoyoBot.instance.getLogger().error("无法处理回调事件! $request")
            response.setStatus(404)
            return
        }
        if (!request.getHeaders().containsKey("X-Rpc-Bot_sign") && HoyoBot.instance.getBot().botKey != "") {
            HoyoBot.instance.getLogger().error("无法校验回调事件的签名,因为不存在签名内容! $request")
            response.setStatus(404)
            return
        }
        try {

            if (HoyoBot.instance.getBot().botKey != "") if (!this.verifyValidity(
                    request.getHeader("X-Rpc-Bot_sign")!!, requestJson.toJSONString(0)
                )
            ) {
                HoyoBot.instance.getLogger().error("未知的事件: 签名校验错误!")
                response.setStatus(404)
                return
            }

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

    private fun verifyValidity(sign: String, body: String): Boolean {
        val pubKey = HoyoBot.instance.getBot().botKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\r?\n|\r".toRegex(), "")
        val signArg = Base64.getDecoder().decode(sign)
        val encodedBody = URLEncoder.encode(body, "UTF-8")
        val encodedSecret = URLEncoder.encode(HoyoBot.instance.getBot().botSecret, "UTF-8")
        val str = "body=$encodedBody&secret=$encodedSecret"
        val pubKeyBytes: ByteArray = Base64.getDecoder().decode(pubKey)
        val keySpec = X509EncodedKeySpec(pubKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(str.toByteArray(StandardCharsets.UTF_8))
        return signature.verify(signArg)
    }

}