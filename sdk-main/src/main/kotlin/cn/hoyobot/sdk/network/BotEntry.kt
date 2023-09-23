package cn.hoyobot.sdk.network

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.proxy.ProxySendMessageEvent
import cn.hoyobot.sdk.event.villa.VillaSendMessageEvent
import cn.hoyobot.sdk.network.protocol.mihoyo.*
import cn.hoyobot.sdk.network.protocol.type.TextType
import cn.hoyobot.sdk.utils.Utils
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import cn.hutool.http.Method
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON


class BotEntry {

    lateinit var botID: String
    lateinit var botSecret: String
    lateinit var botPrivateSecret: String
    lateinit var botKey: String
    lateinit var villaID: String
    var debug = false

    fun getVilla(): Villa {
        val response = this.request(MihoyoAPI.API_VILLA)
        val jsonObject = JSONObject(response.body())
        val villa = Villa()
        try {
            villa.name = jsonObject.getByPath("data.villa.name").toString()
            villa.villaAvatarUrl = jsonObject.getByPath("data.villa.villa_avatar_url").toString()
            villa.isOfficial = jsonObject.getByPath("data.villa.is_official").toString().toBoolean()
            villa.ownerUID = jsonObject.getByPath("data.villa.owner_uid").toString().toInt()
            villa.id = jsonObject.getByPath("data.villa.villa_id").toString().toInt()
            villa.introduce = jsonObject.getByPath("data.villa.introduce").toString()
            (jsonObject.getByPath("data.villa.tags") as JSONArray).forEachIndexed { _, any -> villa.tags.add(any as String) }
        } catch (_: Exception) {
        }

        return villa
    }

    fun getRoom(roomID: Int): Room {
        val params = JSONObject()
        params["room_id"] = roomID
        val response = this.request(MihoyoAPI.API_ROOM, params, Method.GET)
        val jsonObject = JSONObject(response.body())
        val room = Room()
        try {
            room.roomID = jsonObject.getByPath("data.room.room_id").toString().toInt()
            room.roomName = jsonObject.getByPath("data.room.room_name").toString()
            room.groupID = jsonObject.getByPath("data.room.group_id").toString().toInt()
            room.roomType = jsonObject.getByPath("data.room.room_type").toString()
        } catch (_: Exception) {
        }
        return room
    }

    fun getMember(id: Int): Member {
        val params = JSONObject()
        params["uid"] = id
        val response = this.request(MihoyoAPI.API_MEMBER, params, Method.GET)
        val jsonObject = JSONObject(response.body())
        val member = Member()
        try {
            member.joinAt = jsonObject.getByPath("data.member.joined_at").toString().toLong()
            member.uid = jsonObject.getByPath("data.member.basic.uid").toString().toInt()
            member.avatarUrl = jsonObject.getByPath("data.member.basic.avatar_url").toString()
            member.introduce = jsonObject.getByPath("data.member.basic.introduce").toString()
            member.name = jsonObject.getByPath("data.member.basic.nickname").toString()
        } catch (_: Exception) {
        }
        return member
    }

    fun getMemberList(offsetStr: String, size: Int): VillaMemberList {
        val params = JSONObject()
        params["offset_str"] = offsetStr
        params["size"] = size
        val response = this.request(MihoyoAPI.API_MEMBER_LIST, params, Method.GET)
        val jsonObject = JSONObject(response.body())
        val memberList = VillaMemberList()
        try {
            memberList.nextOffsetStr = jsonObject.getByPath("data.next_offset_str").toString()
            (jsonObject.getByPath("data.list") as JSONArray).forEachIndexed { _, any ->
                run {
                    val memberJsonData = any as JSONObject
                    val member = Member()
                    member.joinAt = memberJsonData.getLong("joined_at")
                    member.uid = memberJsonData.getByPath("basic.uid").toString().toInt()
                    member.avatarUrl = memberJsonData.getByPath("basic.avatar_url").toString()
                    member.introduce = memberJsonData.getByPath("basic.introduce").toString()
                    member.name = memberJsonData.getByPath("basic.nickname").toString()
                    memberList.members.add(member)
                }
            }
        } catch (_: Exception) {
        }
        return memberList
    }

    fun kickMember(uid: Int) {
        val params = JSONObject()
        params["uid"] = uid
        this.request(MihoyoAPI.API_MEMBER_KICK, params, Method.POST)
    }

    fun sendMessage(room: Int, message: Message, type: TextType) {
        val event = ProxySendMessageEvent(this, message)
        event.roomID = room
        event.villaID = this.villaID
        HoyoBot.instance.getEventManager().callEvent(event)
        if (event.isCancelled()) return
        when (type) {
            TextType.MESSAGE -> {
                if (message is MsgContentInfo) {
                    val params = JSONObject()
                    params["msg_content"] = message.build().toJSONString(1)
                    params["room_id"] = room.toLong()
                    params["object_name"] = type.getType()
                    this.request(MihoyoAPI.API_MESSAGE, params, Method.POST)
                }
            }
            TextType.IMAGE -> {
                if (message is ImageContentInfo) {
                    val params = JSONObject()
                    params["msg_content"] = message.build().toJSONString(1)
                    params["room_id"] = room.toLong()
                    params["object_name"] = type.getType()
                    this.request(MihoyoAPI.API_MESSAGE, params, Method.POST)
                }
            }
            TextType.POST -> {
                if (message is PostContentInfo) {
                    val params = JSONObject()
                    params["msg_content"] = message.build().toJSONString(1)
                    params["room_id"] = room.toLong()
                    params["object_name"] = type.getType()
                    this.request(MihoyoAPI.API_MESSAGE, params, Method.POST)
                }
            }
            else -> {}
        }
    }

    fun setPinMessage(messageID: String, pin: Boolean, roomID: Int, sendAt: Int) {
        val params = JSONObject()
        params["msg_uid"] = messageID
        params["is_cancel"] = pin
        params["room_id"] = roomID
        params["send_at"] = sendAt
        this.request(MihoyoAPI.API_PIN_MESSAGE, params, Method.POST)
    }

    fun setPinMessage(event: VillaSendMessageEvent, pin: Boolean) {
        this.setPinMessage(event.getMsgID(), pin, event.getRoomID(), event.getSendAt())
    }

    fun recallMessage(messageID: String, roomID: Int, sendAt: Int) {
        val params = JSONObject()
        params["msg_uid"] = messageID
        params["room_id"] = roomID
        params["msg_time"] = sendAt
        this.request(MihoyoAPI.API_RECALL_MESSAGE, params, Method.GET)
    }

    fun recallMessage(event: VillaSendMessageEvent) {
        this.recallMessage(event.getMsgID(), event.getRoomID(), event.getSendAt())
    }

    fun transferImage(originalLink: String): String {
        val params = JSONObject()
        params["url"] = originalLink;
        val response = this.request(MihoyoAPI.API_TRANSFER_IMAGE, params, Method.POST)
        val resultJson = JSONObject(response.body())
        return resultJson.getByPath("data.new_url").toString()
    }

    private fun request(api: String): HttpResponse {
        return this.request(api, JSONObject(), Method.GET)
    }

    private fun request(api: String, params: JSONObject, method: Method): HttpResponse {
        if (method == Method.GET) {
            var request = HttpRequest(api)
            request.contentType(APPLICATION_JSON.toString())
            val map: MutableMap<String, String> = HashMap()
            map["x-rpc-bot_id"] = this.botID
            map["x-rpc-bot_secret"] = if (HoyoBot.instance.isEncrypted()) Utils.encryptHAMCSha256(
                this.botKey, this.botSecret
            ) else this.botSecret
            map["x-rpc-bot_villa_id"] = this.villaID
            request = request.addHeaders(map)
            request.form(params)
            request.method(method)
            val result = request.execute()
            if (debug) HoyoBot.instance.getLogger().debug(result.body())
            return result
        } else {
            val map: MutableMap<String, String> = HashMap()
            map["Content-Type"] = "application/json;charset=UTF-8"
            map["x-rpc-bot_id"] = this.botID
            map["x-rpc-bot_secret"] = if (HoyoBot.instance.isEncrypted()) Utils.encryptHAMCSha256(
                this.botKey, this.botSecret
            ) else this.botSecret
            map["x-rpc-bot_villa_id"] = this.villaID
            val result = HttpRequest.post(api).addHeaders(map).body(params.toString()).timeout(5 * 60 * 1000).execute()
            if (debug) HoyoBot.instance.getLogger().debug(result.body())
            return result
        }
    }
}