package cn.hoyobot.sdk.network.protocol.type

enum class AuditResult(val key: Int, val value: String) {
    DEFAULT(0, "默认"), PASS(1, "通过"), BLOCK(2, "驳回");

    companion object {
        @JvmStatic
        fun getResultByID(id: Int): AuditResult {
            for (result in values()) if (result.key == id) return result
            return DEFAULT
        }
    }

}