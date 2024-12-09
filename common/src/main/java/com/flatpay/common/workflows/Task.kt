package com.flatpay.common.workflows

import com.flatpay.common.database.WorkflowContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base class for all workflow tasks.
 *
 * A task represents a single unit of work in a workflow. Tasks can have pre-hooks
 * and post-hooks that are executed before and after the main task execution.
 */
abstract class Task {
    /**
     * List of tasks to execute before the main task.
     */
    private val _preHooks = mutableListOf<Task>()

    /**
     * List of tasks to execute after the main task.
     */
    private val _postHooks = mutableListOf<Task>()

    /**
     * Public immutable view of pre-hooks.
     */
    val preHooks: List<Task> get() = _preHooks

    /**
     * Public immutable view of post-hooks.
     */
    val postHooks: List<Task> get() = _postHooks

    /**
     * The name of this task, derived from the class name.
     */
    val taskName: String = this::class.simpleName ?: "Unknown?"

    /**
     * Checks if this task has any pre-hooks registered.
     *
     * @return true if there are pre-hooks, false otherwise
     */
    fun hasPreHooks(): Boolean = _preHooks.isNotEmpty()

    /**
     * Checks if this task has any post-hooks registered.
     *
     * @return true if there are post-hooks, false otherwise
     */
    fun hasPostHooks(): Boolean = _postHooks.isNotEmpty()

    /**
     * Adds a pre-hook object to the task.
     */
    fun addPreHookInstance(preHook: Task) {
        _preHooks.add(preHook)
    }

    /**
     * Adds a post-hook object to the task.
     */
    fun addPostHookInstance(postHook: Task) {
        _postHooks.add(postHook)
    }

    /**
     * Adds a pre-hook of the specified type to this task.
     *
     * @param T The type of task to add as a pre-hook
     * @return This task instance for method chaining
     */
    inline fun <reified T : Task> addPreHook(): Task {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        addPreHookInstance(instance)
        return this
    }

    /**
     * Adds a post-hook of the specified type to this task.
     *
     * @param T The type of task to add as a post-hook
     * @return This task instance for method chaining
     */
    inline fun <reified T : Task> addPostHook(): Task {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        addPostHookInstance(instance)
        return this
    }

    /**
     * Executes the task logic.
     *
     * @param context The database context for the task
     * @param dependencies Any dependencies required by the task
     * @return The result of the task execution
     */
    abstract suspend fun execute(
        context: WorkflowContext, dependencies: Dependencies
    ): TaskResult

    /**
     * Runs the task with the given context, dependencies, and previous task result.
     *
     * @param context The database context for the task
     * @param dependencies Any dependencies required by the task
     * @return The result of this task execution
     */
    suspend fun runTask(
        context: WorkflowContext, dependencies: Dependencies
    ): TaskResult {
        return withContext(Dispatchers.Default) {
            execute(context, dependencies)
        }
    }
}
