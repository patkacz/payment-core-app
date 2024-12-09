package com.flatpay.common.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass


class EventBus {
    private val eventChannel = Channel<AppEvent>(Channel.UNLIMITED)
    private val events = mutableMapOf<String, MutableList<(Any) -> Unit>>()  //waiting events

    fun initialize() {

    }

    fun publish(eventName: String, data: Any) {
        events[eventName]?.forEach { it(data) }
    }

    suspend fun post(event: AppEvent) {
        eventChannel.send(event)
    }

    // Wait for multiple event types with optional timeout and auto-cancel
    suspend fun <T : AppEvent> waitMultiple(
        eventTypes: Set<KClass<out T>>, timeoutMillis: Long = -1 // Default value for infinite wait
    ): List<T> {
        val collectedEvents = mutableListOf<T>()
        val filteredChannel = Channel<T>()

        val job = CoroutineScope(Dispatchers.Default).launch {
            try {
                if (timeoutMillis > 0) {
                    withTimeout(timeoutMillis) {
                        for (event in eventChannel) {
                            if (eventTypes.any { it.isInstance(event) }) {
                                @Suppress("UNCHECKED_CAST") filteredChannel.send(event as T)
                            }
                        }
                    }
                } else {
                    for (event in eventChannel) {
                        if (eventTypes.any { it.isInstance(event) }) {
                            @Suppress("UNCHECKED_CAST") filteredChannel.send(event as T)
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                println("Waiting for events timed out after $timeoutMillis ms.")
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


}