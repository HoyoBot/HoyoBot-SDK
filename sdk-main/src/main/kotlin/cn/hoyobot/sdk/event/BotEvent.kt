package cn.hoyobot.sdk.event

import cn.hutool.json.JSONObject
import com.google.common.base.Preconditions
import java.util.*
import java.util.concurrent.CompletableFuture


abstract class BotEvent {

    private val completableFuture: MutableList<CompletableFuture<Void>> = Collections.synchronizedList(ArrayList())
    private var cancelled = false

    open fun putString(jsonObject: JSONObject) {}
    fun isCancelled(): Boolean {
        if (this !is CancellableEvent) {
            throw Exception("Event is not Cancellable")
        }
        return cancelled
    }

    fun setCancelled(cancelled: Boolean) {
        if (this !is CancellableEvent) {
            throw Exception("Event is not Cancellable")
        }
        this.cancelled = cancelled
    }

    fun setCancelled() {
        if (this !is CancellableEvent) {
            throw Exception("Event is not Cancellable")
        }
        cancelled = true
    }

    fun addCompletableFuture(future: CompletableFuture<Void>) {
        Preconditions.checkArgument(
            isCompletable, "Can not add complete future to event which is not @CompletableEvent or @AsyncEvent!"
        )
        completableFuture.add(future)
    }

    val completableFutures: MutableList<CompletableFuture<Void>>
        get() {
            return completableFuture
        }

    fun <T : BotEvent> completeFuture(future: CompletableFuture<T>) {
        if (completableFuture.isEmpty()) {
            future.complete(this as T)
            return
        }
        CompletableFuture.allOf(*completableFuture.toTypedArray<CompletableFuture<*>>())
            .whenComplete { _: Void, error: Throwable ->
                future.completeExceptionally(error)
            }
    }

    val isAsync: Boolean
        get() = this.javaClass.isAnnotationPresent(AsyncEvent::class.java)
    val isCompletable: Boolean
        get() = this.javaClass.isAnnotationPresent(CompletableEvent::class.java) || isAsync
}