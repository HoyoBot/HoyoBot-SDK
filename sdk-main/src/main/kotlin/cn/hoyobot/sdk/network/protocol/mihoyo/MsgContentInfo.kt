package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.protocol.type.MessageEntityType
import cn.hutool.json.JSONObject

class MsgContentInfo(val value: String) : Message {

    private val entities: ArrayList<MessageEntity> = ArrayList()
    override fun build(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.putByPath("content.text", this.value)
        val emptyArray: ArrayList<String> = ArrayList()
        jsonObject.putByPath("content.entities", emptyArray)
        this.entities.forEach {
            run {
                val entityJson = JSONObject()
                entityJson.putByPath("entity.type", it.type.getValue())
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
                emptyArray.add(entityJson.toString())
            }
        }
        jsonObject.putByPath("content.entities", emptyArray)
        return jsonObject
    }

    fun addEntity(entity: MessageEntity): MsgContentInfo {
        this.entities.add(entity)
        return this
    }

}