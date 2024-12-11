package com.flatpay.common.core.event

import com.flatpay.log.AppLog
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass


class EventBus {
    private val eventChannel = Channel<AppEvent>(Channel.BUFFERED)
    companion object {
        val instance: EventBus by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EventBus()
        }// by lazy(LazyThreadSafetyMode.SYNCHRONIZED)
        val eventScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)  // Removed private
    }
    //val events = mutableMapOf<String, MutableList<(Any) -> Unit>>()  //waiting events

    //fun getInstance

    fun initialize() {
        AppLog.LOGI("initialize EVENT BUS")
    }

    fun cleanup() {
        eventScope.cancel() // Cancel scope when needed
    }

    //fun publish(eventName: String, data: Any) {
      //  events[eventName]?.forEach { it(data) }
    //}

    fun post(event: AppEvent) {
        AppLog.LOGI("EventBus: About to post event: $event")
        eventScope.launch(Dispatchers.Main.immediate) {
            eventChannel.send(event)
            AppLog.LOGI("EventBus: Event posted: $event")
        }
    }

    // Wait for multiple event types with optional timeout and auto-cancel
    suspend fun <T : AppEvent> waitMultiple(
        eventTypes: Set<KClass<out T>>, timeoutMillis: Long = -1 // Default value for infinite wait
    ): List<T> {
        AppLog.LOGI("EventBus: Waiting for events: $eventTypes")
        val collectedEvents = mutableListOf<T>()
        val filteredChannel = Channel<T>()

        val job = CoroutineScope(Dispatchers.Default).launch {
            try {
                if (timeoutMillis > 0) {
                    withTimeout(timeoutMillis) {
                        for (event in eventChannel) {
                            AppLog.LOGI("EventBus: Received event in wait1: $event")
                            if (eventTypes.any { it.isInstance(event) }) {
                                @Suppress("UNCHECKED_CAST") filteredChannel.send(event as T)
                            }
                        }
                    }
                } else {
                    for (event in eventChannel) {
                        AppLog.LOGI("EventBus: Received event in wait2: $event")
                        if (eventTypes.any { it.isInstance(event) }) {
                            AppLog.LOGI("EventBus: SENDING RESPONSE")
                            @Suppress("UNCHECKED_CAST") filteredChannel.send(event as T)
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                AppLog.LOGE("Waiting for events timed out after $timeoutMillis ms.")
            } finally {
                filteredChannel.close()
            }
        }

        for (event in filteredChannel) {
            collectedEvents.add(event)
        }

        job.cancel() // Ensure the coroutine is cancelled after events are collected
        return collectedEvents
    }

    // Peek multiple event types without consuming them
    suspend fun <T : AppEvent> peekMultiple(eventTypes: Set<KClass<out T>>): Boolean {
        val temporaryList = mutableListOf<AppEvent>()
        var found = false

        for (event in eventChannel) {
            if (eventTypes.any { it.isInstance(event) }) {
                found = true
            }
            temporaryList.add(event)
        }

        // Restore events back to the channel
        eventChannel.close()
        for (event in temporaryList) {
            eventChannel.send(event)
        }

        return found
    }

    // Clear multiple event types from the queue
    suspend fun <T : AppEvent> clearMultiple(eventTypes: Set<KClass<out T>>) {
        val remainingEvents = mutableListOf<AppEvent>()

        for (event in eventChannel) {
            if (!eventTypes.any { it.isInstance(event) }) {
                remainingEvents.add(event)
            }
        }

        // Re-populate the channel with remaining events
        eventChannel.close()
        for (event in remainingEvents) {
            eventChannel.send(event)
        }
    }

    fun collectEvents(
        eventTypes: Set<KClass<out AppEvent>>,
        onEvent: suspend (AppEvent) -> Unit
    ) = eventScope.launch(Dispatchers.Main.immediate) {
        AppLog.LOGI("EventBus: Starting collection for: $eventTypes")
        eventChannel.consumeAsFlow()
            .collect { event ->
                if (eventTypes.any { it.isInstance(event) }) {
                    withContext(Dispatchers.Main) {
                        onEvent(event)
                    }
                }
            }
    }

}