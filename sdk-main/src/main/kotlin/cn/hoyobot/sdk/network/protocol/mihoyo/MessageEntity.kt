package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hoyobot.sdk.network.protocol.type.MessageEntityType

class MessageEntity {

    var offset = 0
    var length = 0
    var type = MessageEntityType.UNKNOWN
    var value = ""

}