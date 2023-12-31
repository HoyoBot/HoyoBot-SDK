package cn.hoyobot.sdk.network.protocol.mihoyo

import cn.hoyobot.sdk.network.protocol.type.RoomNotifyType
import cn.hoyobot.sdk.network.protocol.type.RoomType

class Room {

    var roomID = 0
    var roomName = ""
    var roomType = RoomType.BOT_PLATFORM_ROOM_TYPE_INVALID.name
    var groupID = 0
    var defaultNotifyType = RoomNotifyType.BOT_PLATFORM_DEFAULT_NOTIFY_TYPE_NOTIFY.name
}