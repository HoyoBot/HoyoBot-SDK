package cn.hoyobot.sdk.network.protocol

interface ProxyFilter {
    fun doFilter(request: ProxyRequest, response: ProxyResponse): Boolean
}