package cn.hoyobot.sdk.network.protocol.type

enum class TextType(private val type: String) {

    MESSAGE("MHY:Text"), IMAGE("MHY:Image"), POST("MHY:Post");

    fun getType(): String {
        return this.type
    }

}