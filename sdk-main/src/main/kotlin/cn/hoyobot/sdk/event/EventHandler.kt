package cn.hoyobot.sdk.event

import cn.hoyobot.sdk.HoyoBot
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.collections.ArrayList


class EventHandler(eventClass: Class<out BotEvent>, eventManager: EventManager) {
    private val eventManager: EventManager
    private val eventClass: Class<out BotEvent>
    private val priority2handlers: MutableMap<EventPriority, ArrayList<Consumer<BotEvent>>> =
        EnumMap(EventPriority::class.java)

    init {
        this.eventClass = eventClass
        this.eventManager = eventManager
        EventPriority.values().forEach { this.priority2handlers[it] = ArrayList() }
    }

    fun <T : BotEvent> handle(event: T): CompletableFuture<T> {
        if (!eventClass.isInstance(event)) {
            throw Exception("Tried to handle invalid event type!")
        }
        if (!event.isAsync) {
            return handleSync(event)
        }
        val future = CompletableFuture<T>()
        CompletableFuture.supplyAsync<Any>({
            for (priority in EventPriority.values()) {
                handlePriority(priority, event)
            }
            event
        }, eventManager.threadedExecutor).thenAccept { futureEvent ->
            (futureEvent as BotEvent).completeFuture(future)
        }.whenComplete { _: Void, error: Throwable ->
            if (!future.isDone) {
                future.completeExceptionally(error)
                HoyoBot.instance.getLogger().error("Exception was thrown in event handler", error)
            }
        }

        return future
    }

    private fun <T : BotEvent> handleSync(event: T): CompletableFuture<T> {
        if (!event.isCompletable) {
            for (priority in EventPriority.values()) {
                handlePriority(priority, event)
            }
            // Non-completable events does not provide future.
        }
        try {
            for (priority in EventPriority.values()) {
                handlePriority(priority, event)
            }
        } catch (e: Exception) {
            HoyoBot.instance.getLogger().error(e)
        }
        try {
            if (event.completableFutures.isEmpty()) {
                return CompletableFuture.completedFuture(event)
            }
        } catch (_: Exception) {
        }
        val future = CompletableFuture<T>()
        event.completeFuture(future)
        return future
    }

    private fun handlePriority(priority: EventPriority, event: BotEvent) {
        val handlerList: ArrayList<Consumer<BotEvent>> = priority2handlers[priority]!!
        for (eventHandler in handlerList) {
            eventHandler.accept(event)
        }
    }

    fun subscribe(handler: Consumer<BotEvent>, priority: EventPriority?) {
        val handlerList: ArrayList<Consumer<BotEvent>>? = priority?.let {
            priority2handlers.computeIfAbsent(
                it
            ) { ArrayList() }
        }
        // Check if event is already registered
        if (!handlerList!!.contains(handler)) {
            // Handler is not registered yet
            handlerList.add(handler)
        }
    }
}