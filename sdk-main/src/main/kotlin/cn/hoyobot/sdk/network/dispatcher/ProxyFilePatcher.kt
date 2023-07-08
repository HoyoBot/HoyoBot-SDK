package cn.hoyobot.sdk.network.dispatcher

import cn.hoyobot.sdk.HoyoBot
import cn.hutool.core.io.IoUtil
import io.netty.channel.ChannelProgressiveFuture
import io.netty.channel.ChannelProgressiveFutureListener
import java.io.RandomAccessFile


class ProxyFileListener(private val raf: RandomAccessFile) : ChannelProgressiveFutureListener {
    override fun operationProgressed(future: ChannelProgressiveFuture, progress: Long, total: Long) {
        HoyoBot.instance.getLogger().debug("Transfer progress: {} / {}", progress, total)
    }

    override fun operationComplete(future: ChannelProgressiveFuture) {
        IoUtil.close(raf)
        HoyoBot.instance.getLogger().debug("Transfer complete.")
    }

    companion object {
        fun build(raf: RandomAccessFile): ProxyFileListener {
            return ProxyFileListener(raf)
        }
    }
}