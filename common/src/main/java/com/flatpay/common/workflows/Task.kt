package com.flatpay.common.workflows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Task() {
    private var executedFromWorkflow: Boolean = false

    var preHooks: MutableList<Task> = mutableListOf()
    var postHooks: MutableList<Task> = mutableListOf()

    val taskName = this::class.simpleName?: "Unknown?"

    /**
     * Task processing function with coroutines.
     * It suspends the current coroutine while the task is being processed.
     */
    abstract suspend fun execute(
        context: DBContext,
        dependencies: Dependencies
    ): TaskResult

    suspend fun runTask(context: DBContext, dependencies: Dependencies, lastTaskResult: TaskResult): TaskResult {
        return withContext(Dispatchers.Default) {
            execute(context, dependencies)
        }
    }

    fun setExecutedFromWorkflow() {
        executedFromWorkflow = true
    }

    suspend inline fun <reified ItemType : Task> addPreHook(): Task {
        val preHook = ItemType::class.java.getDeclaredConstructor().newInstance()
        preHooks.add(preHook)
        return this
    }

    suspend inline fun <reified ItemType : Task> addPostHook(): Task {
        val postHook = ItemType::class.java.getDeclaredConstructor().newInstance()
        postHooks.add(postHook)
        return this
    }

    fun hasPreHooks(): Boolean {
        return !preHooks.isEmpty()
    }

    fun hasPostHooks(): Boolean {
        return !postHooks.isEmpty()
    }
}