package cn.hoyobot.sdk.network.protocol

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.RaknetInfo
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import java.io.IOException


class ProxyActionHandler : SimpleChannelInboundHandler<FullHttpRequest?>() {
    @Throws(Exception::class)
    override fun channelRead0(ctx: ChannelHandlerContext, fullHttpRequest: FullHttpRequest?) {
        val request = ProxyRequest.build(ctx, fullHttpRequest!!)
        val response = ProxyResponse.build(ctx, request)
        try {
            val isPass = doProxyFilter(request, response)
            if (isPass) {
                doAction(request, response)
            }
        } catch (e: Exception) {
            val errorAction: ProxyActionInterface = RaknetInfo.getAction(RaknetInfo.MAPPING_ERROR)!!
            request.putParam("_e", e)
            errorAction.doAction(request, response)
        }
        if (!response.isSent) {
            response.send()
        }
    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is IOException) {
            HoyoBot.instance.getLogger().error(cause)
        } else {
            super.exceptionCaught(ctx, cause)
        }
    }

    private fun doProxyFilter(request: ProxyRequest, response: ProxyResponse): Boolean {
        if (!HoyoBot.instance.isEnabledFilter()) return true
        var filter: ProxyFilter? = RaknetInfo.getProxyFilter(RaknetInfo.MAPPING_ALL) ?: return true
        if (!filter!!.doFilter(request, response)) {
            return false
        }
        filter = RaknetInfo.getProxyFilter(request.path)!!
        if (!filter.doFilter(request, response)) {
            return false
        }
        return true
    }

    private fun doAction(request: ProxyRequest, response: ProxyResponse) {
        HoyoBot.instance.getLogger().debug("${request.path} 被尝试调用了")
        val action: ProxyActionInterface = RaknetInfo.getAction(request.path)!!
        action.doAction(request, response)
    }
}