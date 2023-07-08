package cn.hoyobot.sdk.network.protocol

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.FileRegion
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpContentCompressor

class HttpChunkContentCompressor : HttpContentCompressor() {
    @Throws(Exception::class)
    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (msg is FileRegion || msg is DefaultHttpResponse) {
            ctx.write(msg, promise)
        } else {
            super.write(ctx, msg, promise)
        }
    }
}