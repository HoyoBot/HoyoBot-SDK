package cn.hoyobot.sdk.network.protocol.type

enum class MessageEntityType(val value: String) {

    UNKNOWN("unknown"), MENTIONED_USER("mentioned_user"), MENTIONED_BOT("mentioned_robot"),
    MENTIONED_ALL("mention_all"), ROOM_LINK("villa_room_link"), LINK("link");

    fun getType(): String {
        return this.value
    }

}