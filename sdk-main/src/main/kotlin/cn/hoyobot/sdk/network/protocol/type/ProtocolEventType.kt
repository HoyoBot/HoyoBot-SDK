package cn.hoyobot.sdk.network.protocol.type

enum class ProtocolEventType(private val id: Int) {

    JOIN_VILLA(1), SEND_MESSAGE(2), CREATE_ROBOT(3), DELETE_ROBOT(4), ADD_QUICK_EMOTION(5), AUDIT_CALL_BACK(6);

    fun getID() {
        this.id
    }
}