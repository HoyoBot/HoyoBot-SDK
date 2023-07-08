package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class AsyncPool(server: HoyoBot, size: Int) {
    private val pool: ThreadPoolExecutor
    private val server: HoyoBot
    private val size: Int
    private val currentThread: AtomicInteger = AtomicInteger()

    init {
        this.size = size
        pool = ThreadPoolExecutor(
            size, Int.MAX_VALUE,
            60, TimeUnit.MILLISECONDS, SynchronousQueue()
        ) { runnable: Runnable? ->
            object : Thread(runnable) {
                init {
                    isDaemon = true
                    name = String.format(
                        "Nukkit Asynchronous Task Handler #%s",
                        currentThread.incrementAndGet()
                    )
                }
            }
        }
        this.server = server
    }

    fun submitTask(runnable: Runnable?) {
        pool.execute(runnable)
    }

    fun getSize(): Int {
        return this.size
    }

    fun getServer(): HoyoBot {
        return server
    }
}