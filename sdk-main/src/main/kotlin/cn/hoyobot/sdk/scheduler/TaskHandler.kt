package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.plugin.Plugin

class TaskHandler(plugin: Plugin, timingName: String?, task: Runnable, taskId: Int, val isAsynchronous: Boolean) {

    val taskName: String
    val taskId: Int
    private val plugin: Plugin
    val task: Runnable
    var delay = 0
    var period = 0
    var lastRunTick = 0
    var nextRunTick = 0
    var isCancelled = false
        private set

    init {
        this.plugin = plugin
        this.task = task
        this.taskId = taskId
        taskName = timingName ?: "Unknown"
    }

    val isDelayed: Boolean
        get() = delay > 0
    val isRepeating: Boolean
        get() = period > 0

    fun getPlugin(): Plugin {
        return plugin
    }

    fun cancel() {
        if (!isCancelled && task is Task) {
            task.onCancel()
        }
        isCancelled = true
    }

    @Deprecated("")
    fun remove() {
        isCancelled = true
    }

    fun run(currentTick: Int) {
        try {
            lastRunTick = currentTick
            task.run()
        } catch (ex: RuntimeException) {
            HoyoBot.instance.getLogger().error("Exception while invoking run", ex)
        }
    }
}