package cn.hoyobot.sdk.command
interface CommandSender {
    val name: String
    val isPlayer: Boolean
    fun sendMessage(message: String)
}