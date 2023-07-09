package cn.hoyobot.sdk.event

import cn.hoyobot.sdk.HoyoBot
import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.*
import java.util.function.Consumer

class EventManager(proxy: HoyoBot) {
    private val proxy: HoyoBot
    val threadedExecutor: ExecutorService
    private val handlerMap: HashMap<Class<out BotEvent>, EventHandler> = HashMap()

    init {
        this.proxy = proxy
        val builder = ThreadFactoryBuilder()
        builder.setNameFormat("HoyoBot Executor")
        val idleThreads = 0
        threadedExecutor =
            ThreadPoolExecutor(idleThreads, Int.MAX_VALUE, 60, TimeUnit.SECONDS, SynchronousQueue(), builder.build())
    }

    fun <T : BotEvent> subscribe(event: Class<T>, handler: Consumer<T>) {
        this.subscribe(event, handler, EventPriority.NORMAL)
    }

    /**
     * Can be used to subscribe to events. Once subscribed, the handler will be called each time the event is called.
     *
     * @param event    A class reference to the target event you want to subscribe to, for example ProxyPingEvent.class
     * @param handler  A method reference or lambda with one parameter, the event which you want to handle
     * @param priority The Priority of your event handler. Can be used to execute one handler after / before another
     * @param <T>      The class reference to the event you want to subscribe to
     * @see AsyncEvent
     *
     * @see EventPriority
    </T> */
    fun <T : BotEvent> subscribe(event: Class<T>, handler: Consumer<T>, priority: EventPriority) {
        val eventHandler: EventHandler = handlerMap.computeIfAbsent(
            event
        ) {
            EventHandler(
                event,
                this
            )
        }
        eventHandler.subscribe(handler as Consumer<BotEvent>, priority)
    }

    /**
     * Used to call an provided event.
     * If the target event has the annotation AsyncEvent present, the CompletableFuture.whenComplete can be used to
     * execute code once the event has passed the whole event pipeline. If the annotation is not present, you can
     * ignore the return and use the direct variable reference of your event
     *
     * @param event the instance of an event to be called
     * @return CompletableFuture<Event> if event has AsyncEvent annotation present or null in case of non-async event
    </Event> */
    fun <T : BotEvent> callEvent(event: T): CompletableFuture<T> {
        val eventHandler: EventHandler = handlerMap.computeIfAbsent(
            event.javaClass
        ) {
            EventHandler(
                event.javaClass,
                this
            )
        }
        return eventHandler.handle(event)
    }
}