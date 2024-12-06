package com.flatpay.common.workflows

import com.flatpay.log.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Represents a workflow that executes a sequence of tasks.
 *
 * A workflow manages the execution order of tasks, including their pre-hooks and post-hooks,
 * and supports dynamic jumping between tasks based on task results.
 */
class Workflow : Task() {
    /**
     * Index of the currently executing task.
     */
    private var currentTaskIndex: Int = 0

    /**
     * Result of the last executed task.
     */
    private var lastTaskResult: TaskResult = TaskResult(TaskResult.ResultCodes.OK)

    /**
     * List of tasks in this workflow.
     */
    val workflowItems: MutableList<Task> = mutableListOf()

    /**
     * Mapping of task names to their positions in the workflow.
     */
    val jumpMap: MutableMap<String, Int> = mutableMapOf()

    /**
     * Add a task to the workflow.
     */
    inline fun <reified T : Task> addItem(): Workflow {
        val task = T::class.java.getDeclaredConstructor().newInstance()
        workflowItems.add(task)
        jumpMap[task.taskName] = workflowItems.size - 1
        return this
    }

    /**
     * Retrieve a specific task from the workflow.
     */
    inline fun <reified T : Task> getItem(): T {
        val taskName = T::class.simpleName ?: "Unknown?"
        return workflowItems[jumpMap[taskName]!!] as T //TODO: change !! to something safer
    }

    /**
     * Insert a task before a specific task.
     */
    inline fun <reified InsertItem : Task, reified BeforeItem : Task> insertBefore(): Workflow {
        val beforeTaskName = BeforeItem::class.simpleName ?: "Unknown?"
        val pos = jumpMap[beforeTaskName]
            ?: throw IllegalArgumentException("Inserting task before non-existent task")
        return insert<InsertItem>(pos)
    }

    /**
     * Insert a task after a specific task.
     */
    inline fun <reified InsertItem : Task, reified AfterItem : Task> insertAfter(): Workflow {
        val afterTaskName = AfterItem::class.simpleName ?: "Unknown?"
        val pos = jumpMap[afterTaskName]
            ?: throw IllegalArgumentException("Inserting task after non-existent task")
        return insert<InsertItem>(pos + 1)
    }

    /**
     * Insert a task at a specific position.
     */
    inline fun <reified T : Task> insert(pos: Int): Workflow {
        val task = T::class.java.getDeclaredConstructor().newInstance()
        workflowItems.add(pos, task)
        jumpMap.forEach { (key, value) ->
            if (value >= pos) {
                jumpMap[key] = value + 1
            }
        }
        jumpMap[task.taskName] = pos
        return this
    }

    /**
     * Executes the workflow asynchronously.
     */
    override suspend fun execute(
        context: DBContext,
        dependencies: Dependencies
    ): TaskResult = withContext(Dispatchers.Default) {
        val currentWorkflow = taskName
        AppLog.LOGI("Execute workflow $currentWorkflow")

        var workflowResult = TaskResult(TaskResult.ResultCodes.OK)

        while (currentTaskIndex < workflowItems.size) {
            executeTask(context, dependencies)
            workflowResult = this@Workflow.lastTaskResult

            if (workflowResult.retCode == TaskResult.ResultCodes.ERROR) {
                //TODO: New Relic integration
                break
            }

            if (workflowResult.retCode == TaskResult.ResultCodes.CANCEL) {
                break
            }
        }

        currentTaskIndex = 0
        workflowResult
    }

    /**
     * Execute task, handle pre and post hoooks
     */
    private suspend fun executeTask(context: DBContext, dependencies: Dependencies) {
        val currentTask = workflowItems[currentTaskIndex]
        AppLog.LOGI("Execute task $(currentTask.taskName)")

        // Execute pre-hooks if any
        if (currentTask.hasPreHooks()) {
            AppLog.LOGI("Execute pre hooks")
            val hookResult = executeHooks(context, dependencies, currentTask.preHooks)
            if (hookResult == TaskResult(TaskResult.ResultCodes.CANCEL)) {
                AppLog.LOGI("Pre hook return CANCEL")
                lastTaskResult = hookResult
                return
            }
            if (hookResult.isJumping) {
                AppLog.LOGI("*** Pre hook jump to: $(hookResult.getNextItem())")
                if (!setNextProcessingItem(hookResult.nextWorkflowItem ?: "")) {
                    lastTaskResult = TaskResult(TaskResult.ResultCodes.CANCEL)
                    return
                }
                AppLog.LOGI("*** Pre hook jump to: $(workflowItems[currentTaskIndex].taskName)")
                return
            }
        }

        // Execute main task
        lastTaskResult = currentTask.runTask(context, dependencies, lastTaskResult)

        // Handle jump if needed
        if (lastTaskResult.isJumping) {
            AppLog.LOGI("*** $(currentTask.taskName) jump to: $(lastTaskResult.getNextItem())")
            if (!setNextProcessingItem(lastTaskResult.nextWorkflowItem ?: "")) {
                lastTaskResult = TaskResult(TaskResult.ResultCodes.CANCEL)
                return
            }
        }

        // Execute post-hooks if any
        if (currentTask.hasPostHooks()) {
            AppLog.LOGI("Execute post hooks")
            val hookResult = executeHooks(context, dependencies, currentTask.postHooks)
            if (hookResult == TaskResult(TaskResult.ResultCodes.CANCEL)) {
                AppLog.LOGI("Post hook return CANCEL")
                lastTaskResult = hookResult
                return
            }
            if (hookResult.isJumping) {
                AppLog.LOGI("*** Post hook jump to: $hookResult.getNextItem()")
                if (!setNextProcessingItem(hookResult.nextWorkflowItem ?: "Unknown!")) {
                    lastTaskResult = TaskResult(TaskResult.ResultCodes.CANCEL)
                    return
                }
                AppLog.LOGI("*** Post hook jump to: $(workflowItems[currentTaskIndex].taskName)")
                return
            }
        }

        currentTaskIndex++
    }

    /**
     * Execute hooks for the task
     */
    private suspend fun executeHooks(
        context: DBContext,
        dependencies: Dependencies,
        hooksList: List<Task>?
    ): TaskResult = coroutineScope {
        hooksList?.forEach { hook ->
            val result = async {
                hook.runTask(context, dependencies, lastTaskResult)
            }.await()

            if (result.isJumping || result.retCode == TaskResult.ResultCodes.CANCEL) {
                return@coroutineScope result
            }
        }
        TaskResult()
    }

    /**
     * Set the next processing task in the flow.
     */
    private fun setNextProcessingItem(nextItemName: String): Boolean {
        val pos = jumpMap[nextItemName]
        return if (pos != null) {
            currentTaskIndex = pos
            true
        } else {
            AppLog.LOGI("Invalid attempt to jump to: $(nextItemName)")
            false
        }
    }
}
