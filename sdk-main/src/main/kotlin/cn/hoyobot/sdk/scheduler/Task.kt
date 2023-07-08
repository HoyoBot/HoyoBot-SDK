package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot

abstract class Task : Runnable {

    private lateinit var taskHandler: TaskHandler

    private var handler: TaskHandler
        get() = taskHandler
        set(taskHandler) {
        }

    val taskId: Int
        get() = taskHandler.taskId

    abstract fun onRun(currentTick: Int)

    override fun run() {}

    fun onCancel() {}

    fun cancel() {
        try {
            handler.cancel()
        } catch (ex: RuntimeException) {
            HoyoBot.instance.getLogger().error("Exception while invoking onCancel", ex)
        }
    }

}