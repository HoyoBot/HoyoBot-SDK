package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.plugin.Plugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class BotScheduler {
    private val asyncPool: AsyncPool
    private val pending: Queue<TaskHandler>
    private val queue: Queue<TaskHandler>
    private val taskMap: MutableMap<Int, TaskHandler>
    private val currentTaskId: AtomicInteger

    @Volatile
    private var currentTick = 0

    init {
        pending = ConcurrentLinkedQueue()
        currentTaskId = AtomicInteger()
        queue = PriorityQueue(
            11
        ) { left: TaskHandler, right: TaskHandler -> left!!.nextRunTick - right!!.nextRunTick }
        taskMap = ConcurrentHashMap()
        asyncPool = AsyncPool(HoyoBot.instance, WORKERS)
    }

    fun scheduleTask(task: Task): TaskHandler? {
        return addTask(task, 0, 0, false)
    }

    fun scheduleTask(task: Runnable): TaskHandler? {
        return addTask(null, task, 0, 0, false)
    }

    fun scheduleTask(task: Runnable, asynchronous: Boolean): TaskHandler? {
        return addTask(null, task, 0, 0, asynchronous)
    }

    fun scheduleAsyncTask(task: AsyncTask): TaskHandler? {
        return addTask(null, task, 0, 0, true)
    }

    @Deprecated("")
    fun scheduleAsyncTaskToWorker(task: AsyncTask, worker: Int) {
        scheduleAsyncTask(task)
    }

    val asyncTaskPoolSize: Int
        get() = asyncPool.getSize()

    fun increaseAsyncTaskPoolSize(newSize: Int) {
        throw UnsupportedOperationException("Cannot increase a working pool size.")
    }

    fun scheduleDelayedTask(task: Task, delay: Int): TaskHandler? {
        return this.addTask(task, delay, 0, false)
    }

    fun scheduleDelayedTask(task: Task, delay: Int, asynchronous: Boolean): TaskHandler? {
        return this.addTask(task, delay, 0, asynchronous)
    }

    fun scheduleDelayedTask(task: Runnable, delay: Int): TaskHandler? {
        return addTask(null, task, delay, 0, false)
    }

    fun scheduleDelayedTask(task: Runnable, delay: Int, asynchronous: Boolean): TaskHandler? {
        return addTask(null, task, delay, 0, asynchronous)
    }

    fun scheduleRepeatingTask(task: Runnable, period: Int): TaskHandler? {
        return addTask(null, task, 0, period, false)
    }

    fun scheduleRepeatingTask(task: Runnable, period: Int, asynchronous: Boolean): TaskHandler? {
        return addTask(null, task, 0, period, asynchronous)
    }

    fun scheduleRepeatingTask(task: Task, period: Int): TaskHandler? {
        return addTask(task, 0, period, false)
    }

    fun scheduleRepeatingTask(task: Task, period: Int, asynchronous: Boolean): TaskHandler? {
        return addTask(task, 0, period, asynchronous)
    }

    fun scheduleDelayedRepeatingTask(task: Task, delay: Int, period: Int): TaskHandler? {
        return addTask(task, delay, period, false)
    }

    fun scheduleDelayedRepeatingTask(task: Task, delay: Int, period: Int, asynchronous: Boolean): TaskHandler? {
        return addTask(task, delay, period, asynchronous)
    }

    fun scheduleDelayedRepeatingTask(task: Runnable, delay: Int, period: Int): TaskHandler? {
        return addTask(null, task, delay, period, false)
    }

    fun scheduleDelayedRepeatingTask(task: Runnable, delay: Int, period: Int, asynchronous: Boolean): TaskHandler? {
        return addTask(null, task, delay, period, asynchronous)
    }

    fun cancelTask(taskId: Int) {
        if (taskMap.containsKey(taskId)) {
            try {
                taskMap.remove(taskId)!!.cancel()
            } catch (ex: RuntimeException) {
                HoyoBot.instance.getLogger().error("Exception while invoking onCancel", ex)
            }
        }
    }

    fun cancelTask(plugin: Plugin) {
        taskMap.forEach { (_, taskHandler) ->
            run {
                if (plugin == taskHandler.getPlugin()) {
                    try {
                        taskHandler.cancel() /* It will remove from task map automatic in next main heartbeat. */
                    } catch (ex: RuntimeException) {
                        HoyoBot.instance.getLogger().error("Exception while invoking onCancel", ex)
                    }
                }
            }
        }
    }

    fun cancelAllTasks() {
        taskMap.forEach { (_, value) ->
            run {
                try {
                    value.cancel()
                } catch (ex: RuntimeException) {
                    HoyoBot.instance.getLogger().error("Exception while invoking onCancel", ex)
                }
            }
        }
        taskMap.clear()
        queue.clear()
        currentTaskId.set(0)
    }

    fun isQueued(taskId: Int): Boolean {
        return taskMap.containsKey(taskId)
    }

    private fun addTask(task: Task, delay: Int, period: Int, asynchronous: Boolean): TaskHandler? {
        return (if (task is PluginTask<*>) task.owner else null)?.let {
            addTask(
                it,
                { task.onRun(currentTick + delay) }, delay, period, asynchronous
            )
        }
    }

    private fun addTask(plugin: Plugin?, task: Runnable, delay: Int, period: Int, asynchronous: Boolean): TaskHandler? {
        if (!plugin!!.isEnabled()) {
            HoyoBot.instance.getLogger()
                .error("Plugin '" + plugin.name + "' attempted to register a task while disabled.")
            return null
        }
        if (delay < 0 || period < 0) {
            HoyoBot.instance.getLogger().error("Attempted to register a task with negative delay or period.")
            return null
        }
        val taskHandler = TaskHandler(plugin, "Unknown", task, nextTaskId(), asynchronous)
        taskHandler.delay = delay
        taskHandler.period = period
        taskHandler.nextRunTick = if (taskHandler.isDelayed) currentTick + taskHandler.delay else currentTick
        pending.offer(taskHandler)
        taskMap[taskHandler.taskId] = taskHandler
        return taskHandler
    }

    fun mainThreadHeartbeat(currentTick: Int) {
        this.currentTick = currentTick
        // Accepts pending.
        while (!pending.isEmpty()) {
            queue.offer(pending.poll())
        }
        // Main heart beat.
        while (isReady(currentTick)) {
            val taskHandler = queue.poll()
            if (taskHandler!!.isCancelled) {
                taskMap.remove(taskHandler.taskId)
                continue
            } else if (taskHandler.isAsynchronous) {
                asyncPool.submitTask(taskHandler.task)
            } else {
                try {
                    taskHandler.run(currentTick)
                } catch (e: Exception) {
                    HoyoBot.instance.getLogger()
                        .error("Could not execute taskHandler " + taskHandler.taskName + ": " + e.message)
                    HoyoBot.instance.getLogger().error(e)
                }
            }
            if (taskHandler.isRepeating) {
                taskHandler.nextRunTick = currentTick + taskHandler.period
                pending.offer(taskHandler)
            } else {
                try {
                    taskMap.remove(taskHandler.taskId)!!.cancel()
                } catch (ex: RuntimeException) {
                    HoyoBot.instance.getLogger().error("Exception while invoking onCancel", ex)
                }
            }
        }
        AsyncTask.collectTask()
    }

    val queueSize: Int
        get() = queue.size + pending.size

    private fun isReady(currentTick: Int): Boolean {
        return queue.peek() != null && queue.peek()!!.nextRunTick <= currentTick
    }

    private fun nextTaskId(): Int {
        return currentTaskId.incrementAndGet()
    }

    companion object {
        var WORKERS = 4
        var store: ConcurrentHashMap<String, Any> = ConcurrentHashMap()
    }
}