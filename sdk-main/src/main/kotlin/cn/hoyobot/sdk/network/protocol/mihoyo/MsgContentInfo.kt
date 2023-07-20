package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.type.MessageEntityType
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject

class MsgContentInfo(var value: String) : Message {

    private val entities: ArrayList<MessageEntity> = ArrayList()
    private val quotedParent = QuoteInfo()
    override fun build(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.putByPath("content.text", this.value)
        val emptyArray = JSONArray()
        jsonObject.putByPath("content.entities", emptyArray)
        this.entities.forEach {
            run {
                val entityJson = JSONObject()
                entityJson.putByPath("entity.type", it.type.getType())
                when (it.type) {
                    MessageEntityType.MENTIONED_BOT -> {
                        entityJson.putByPath("entity.bot_id", it.value)
                    }
                    MessageEntityType.MENTIONED_USER -> {
                        entityJson.putByPath("entity.user_id", it.value)
                    }
                    MessageEntityType.ROOM_LINK -> {
                        entityJson.putByPath("entity.villa_id", HoyoBot.instance.getVillaID())
                        entityJson.putByPath("entity.room_id", it.value)
                    }
                    MessageEntityType.LINK -> {
                        entityJson.putByPath("entity.url", it.value)
                        entityJson.putByPath("entity.requires_bot_access_token", true)
                    }
                    else -> {}
                }
                entityJson["offset"] = it.offset
                entityJson["length"] = it.length
                emptyArray.add(entityJson)
            }
        }
        jsonObject.putByPath("content.entities", emptyArray)
        if (this.quotedParent.enable) {
            jsonObject.putByPath("quote.original_message_id", this.quotedParent.originalMessageID)
            jsonObject.putByPath("quote.original_message_send_time", this.quotedParent.originalMessageSendAt)
            jsonObject.putByPath("quote.quoted_message_id", this.quotedParent.quotedMessageID)
            jsonObject.putByPath("quote.quoted_message_send_time", this.quotedParent.quotedMessageSendAt)
        }
        return jsonObject
    }

    fun importFromJson(jsonObject: JSONObject): MsgContentInfo {
        val entitiesArray = jsonObject.getByPath("content.entities") as JSONArray
        entitiesArray.forEach {
            run {
                val entity = it as JSONObject
                val msgEntity = MessageEntity()
                msgEntity.offset = entity.getInt("offset")
                msgEntity.length = entity.getInt("length")
                msgEntity.type =
                    MessageEntityType.getMessageEntityTypeFromStr(entity.getByPath("entity.type").toString())
                msgEntity.value = when (msgEntity.type) {
                    MessageEntityType.MENTIONED_USER -> entity.getByPath("entity.user_id").toString()
                    MessageEntityType.MENTIONED_BOT -> entity.getByPath("entity.bot_id").toString()
                    else -> {
                        ""
                    }
                }
                this.entities.add(msgEntity)
            }
        }
        this.value = jsonObject.getByPath("content.text").toString()
        return this
    }

    fun appendMentionedMessage(uid: Int, type: MessageEntityType): MsgContentInfo {
        val entity = MessageEntity()
        entity.type = type
        entity.value = uid.toString()
        entity.offset = this.value.length
        //似乎米哈游没有提供获取机器人ID的接口
        val name = "@" + when (type) {
            MessageEntityType.MENTIONED_USER -> HoyoBot.instance.getBot().getMember(uid).name
            MessageEntityType.MENTIONED_ALL -> "全体成员"
            else -> ""
        }
        entity.length = name.length
        this.value += name
        this.addEntity(entity)
        return this
    }

    fun appendRoomLink(roomID: Int): MsgContentInfo {
        val entity = MessageEntity()
        entity.type = MessageEntityType.ROOM_LINK
        entity.value = roomID.toString()
        entity.offset = this.value.length
        val name = "#" + HoyoBot.instance.getBot().getRoom(roomID).roomName
        entity.length = name.length
        this.addEntity(entity)
        return this
    }

    fun appendLink(url: String): MsgContentInfo {
        val entity = MessageEntity()
        entity.type = MessageEntityType.LINK
        entity.value = url
        entity.offset = this.value.length
        entity.length = url.length
        this.addEntity(entity)
        return this
    }

    fun append(msg: String): MsgContentInfo {
        this.value += msg
        return this
    }

    fun setQuotedParent(id: String, time: Int) {
        this.quotedParent.enable = true
        this.quotedParent.quotedMessageID = id
        this.quotedParent.originalMessageID = id
        this.quotedParent.quotedMessageSendAt = time
        this.quotedParent.originalMessageSendAt = time
    }

    fun addEntity(entity: MessageEntity): MsgContentInfo {
        this.entities.add(entity)
        return this
    }

}