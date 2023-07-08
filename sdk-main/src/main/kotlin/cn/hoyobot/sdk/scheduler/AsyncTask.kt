package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


abstract class AsyncTask : Runnable {

    var result: Any? = null
    var taskId = 0
    var isFinished = false
        private set

    override fun run() {
        result = null
        onRun()
        isFinished = true
        FINISHED_LIST.offer(this)
    }

    fun hasResult(): Boolean {
        return result != null
    }

    fun getFromThreadStore(identifier: String): Any? {
        return if (isFinished) null else BotScheduler.store[identifier]
    }

    fun saveToThreadStore(identifier: String, value: Any) {
        if (!isFinished) {
            BotScheduler.store[identifier] = value
        }
    }

    abstract fun onRun()
    fun onCompletion(server: HoyoBot) {}
    fun cleanObject() {
        result = null
        taskId = 0
        isFinished = false
    }

    companion object {
        val FINISHED_LIST: Queue<AsyncTask> = ConcurrentLinkedQueue()
        fun collectTask() {
            while (!FINISHED_LIST.isEmpty()) {
                FINISHED_LIST.poll().onCompletion(HoyoBot.instance)
            }
        }
    }
}