package com.flatpay.common.workflows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Task {
    private val _preHooks = mutableListOf<Task>()
    private val _postHooks = mutableListOf<Task>()

    // Expose immutable interfaces
    val preHooks: List<Task> get() = _preHooks
    val postHooks: List<Task> get() = _postHooks
    val taskName: String = this::class.simpleName ?: "Unknown?"

    fun hasPreHooks(): Boolean = _preHooks.isNotEmpty()
    fun hasPostHooks(): Boolean = _postHooks.isNotEmpty()

    fun addPreHookInstance(preHook: Task) {
        _preHooks.add(preHook)
    }

    fun addPostHookInstance(postHook: Task) {
        _postHooks.add(postHook)
    }

    inline fun <reified T : Task> addPreHook(): Task {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        addPreHookInstance(instance)
        return this
    }

    inline fun <reified T : Task> addPostHook(): Task {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        addPostHookInstance(instance)
        return this
    }

    /**
     * Task processing function with coroutines.
     * It suspends the current coroutine while the task is being processed.
     */
    abstract suspend fun execute(
        context: DBContext,
        dependencies: Dependencies
    ): TaskResult

    suspend fun runTask(
        context: DBContext,
        dependencies: Dependencies,
        lastTaskResult: TaskResult
    ): TaskResult {
        return withContext(Dispatchers.Default) {
            execute(context, dependencies)
        }

    }
}
