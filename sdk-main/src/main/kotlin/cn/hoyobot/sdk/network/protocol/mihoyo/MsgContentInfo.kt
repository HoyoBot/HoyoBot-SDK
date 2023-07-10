package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.type.MessageEntityType
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject

class MsgContentInfo(var value: String) : Message {

    private val entities: ArrayList<MessageEntity> = ArrayList()
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
        return jsonObject
    }

    fun appendMentionedMessage(uid: Int, type: MessageEntityType): MsgContentInfo {
        val entity = MessageEntity()
        entity.type = type
        entity.value = uid.toString()
        entity.offset = this.value.length
        //似乎米哈游没有提供获取机器人ID的接口
        val name =
            "@" + when (type) {
                MessageEntityType.MENTIONED_USER -> HoyoBot.instance.getBot().getMember(uid).name
                MessageEntityType.MENTIONED_ALL -> "全体成员"
                else -> ""
            }
        entity.length = name.length
        this.value += name
        this.addEntity(entity)
        return this
    }

    fun append(msg: String): MsgContentInfo {
        this.value += msg
        return this
    }

    fun addEntity(entity: MessageEntity): MsgContentInfo {
        this.entities.add(entity)
        return this
    }

}