package cn.hoyobot.sdk.network.protocol

interface ProxyActionInterface {
    fun doAction(request: ProxyRequest, response: ProxyResponse)
}