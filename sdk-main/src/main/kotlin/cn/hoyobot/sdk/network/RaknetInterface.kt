package cn.hoyobot.sdk.network

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.dispatcher.VillaPatcher
import cn.hoyobot.sdk.network.protocol.ProxyActionHandler
import cn.hoyobot.sdk.scheduler.AsyncTask
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.proxy.ProxyHandler
import io.netty.handler.stream.ChunkedWriteHandler


open class RaknetInterface(private val proxy: HoyoBot) {
    fun start() {
        try {
            RaknetInfo.setAction(this.proxy.getHttpCallBackPath(), VillaPatcher::class.java)
            RaknetInfo.port = proxy.getPort()
            this.start(proxy)
        } catch (e: InterruptedException) {
            proxy.getLogger().error(e)
        }
    }

    @Throws(InterruptedException::class)
    protected fun start(proxy: HoyoBot) {
        this.proxy.getScheduler().scheduleAsyncTask(object : AsyncTask() {
            override fun onRun() {
                val port: Int = proxy.getPort()
                val bossGroup: EventLoopGroup = NioEventLoopGroup(1)
                val workerGroup: EventLoopGroup = NioEventLoopGroup()
                try {
                    val b = ServerBootstrap()
                    b.group(bossGroup, workerGroup)
                        .option<Int>(ChannelOption.SO_BACKLOG, 1024)
                        .channel(NioServerSocketChannel::class.java)
                        .childHandler(object : ChannelInitializer<SocketChannel>() {
                            @Throws(Exception::class)
                            override fun initChannel(ch: SocketChannel) {
                                ch.pipeline()
                                    .addLast(HttpServerCodec())
                                    .addLast(HttpObjectAggregator(65536))
                                    .addLast(ChunkedWriteHandler())
                                    .addLast(ProxyActionHandler())
                            }
                        })
                    val ch = b.bind(port).sync().channel()
                    ch.closeFuture().sync()
                } catch (e: InterruptedException) {
                    bossGroup.shutdownGracefully()
                    workerGroup.shutdownGracefully()
                    throw RuntimeException(e)
                }
            }
        })
    }
}