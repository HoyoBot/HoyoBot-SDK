package cn.hoyobot.sdk.network.protocol.type

enum class ProtocolEventType(private val id: Int, private val eventName: String) {

    UNKNOWN(0, "UNKNOWN"), JOIN_VILLA(
        1, "JoinVilla"
    ),
    SEND_MESSAGE(2, "SendMessage"), CREATE_ROBOT(3, "CreateRobot"), DELETE_ROBOT(
        4, "DeleteRobot"
    ),
    ADD_QUICK_EMOTION(5, "AddQuickEmoticon"), AUDIT_CALL_BACK(
        6, "AuditCallback"
    );

    fun getID(): Int {
        return this.id
    }

    fun getEventName(): String {
        return this.eventName
    }

    companion object {
        @JvmStatic
        fun getTypeByID(id: Int): ProtocolEventType {
            ProtocolEventType.values().forEach { if (it.getID() == id) return it }
            return UNKNOWN
        }
    }

}