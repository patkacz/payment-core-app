package com.flatpay.common.workflows

import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.core.model.Dependencies
import com.flatpay.log.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

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
     * List of tasks in this workflow.
     */
    val workflowItems: MutableList<Task> = mutableListOf()

    /**
     * Mapping of task names to their positions in the workflow.
     */
    val jumpMap: MutableMap<KClass<*>, Int> = mutableMapOf()

    /**
     * Add a task to the workflow.
     */
    inline fun <reified T : Task> addItem(): Workflow {
        val task = T::class.java.getDeclaredConstructor().newInstance()
        workflowItems.add(task)
        jumpMap[T::class] = workflowItems.size - 1
        return this
    }

    /**
     * Retrieve a specific task from the workflow.
     */
    inline fun <reified T : Task> getItem(): T {
        return workflowItems[jumpMap[T::class]!!] as T //TODO: change !! to something safer
    }

    /**
     * Insert a task before a specific task.
     */
    inline fun <reified InsertItem : Task, reified BeforeItem : Task> insertBefore(): Workflow {
        val pos = jumpMap[BeforeItem::class]
            ?: throw IllegalArgumentException("Inserting task before non-existent task")
        return insert<InsertItem>(pos)
    }

    /**
     * Insert a task after a specific task.
     */
    inline fun <reified InsertItem : Task, reified AfterItem : Task> insertAfter(): Workflow {
        val pos = jumpMap[AfterItem::class]
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
        jumpMap[T::class] = pos
        return this
    }

    /**
     * Executes the workflow asynchronously.
     */
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult = withContext(Dispatchers.Default) {
        val currentWorkflow = taskName
        AppLog.LOGI("Execute workflow $currentWorkflow")

        currentTaskIndex = 0

        while (currentTaskIndex < workflowItems.size) {
            when (val workflowResult = executeTask(context, dependencies)) {
                is TaskResult.NextWorkflowItem -> {
                    AppLog.LOGI("*** $currentWorkflow jump to: ${workflowResult.nextItem.simpleName}")
                    return@withContext workflowResult
                }

                is TaskResult.ResultCode -> {
                    when (workflowResult.status) {
                        TaskStatus.CANCEL -> {
                            AppLog.LOGI("Workflow $currentWorkflow return CANCEL")
                            return@withContext TaskResult.ResultCode(TaskStatus.CANCEL)
                        }

                        TaskStatus.ERROR -> {
                            AppLog.LOGI("Workflow $currentWorkflow return ERROR")
                            //TODO: New Relic integration
                            return@withContext TaskResult.ResultCode(TaskStatus.ERROR)
                        }

                        else -> Unit
                    }
                }
            }
        }

        return@withContext TaskResult.ResultCode(TaskStatus.OK)
    }

    /**
     * Execute task, handle pre and post hoooks
     */
    private suspend fun executeTask(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        val currentTask = workflowItems[currentTaskIndex]
        AppLog.LOGI("Execute task ${currentTask.taskName}")

        // Execute pre-hooks if any
        if (currentTask.hasPreHooks()) {
            AppLog.LOGI("Execute pre hooks")
            when (val hookResult = executeHooks(context, dependencies, currentTask.preHooks)) {
                is TaskResult.NextWorkflowItem -> {
                    if (!setNextProcessingItem(hookResult.nextItem)) {
                        AppLog.LOGI("*** Pre hook jump outside workflow to: ${hookResult.nextItem.simpleName}")
                        return hookResult
                    }
                    AppLog.LOGI("*** Pre hook jump to: ${hookResult.nextItem.simpleName}")
                    return TaskResult.ResultCode(TaskStatus.OK)
                }

                is TaskResult.ResultCode -> {
                    when (hookResult.status) {
                        TaskStatus.CANCEL -> {
                            AppLog.LOGI("Pre hook return CANCEL")
                            return TaskResult.ResultCode(TaskStatus.CANCEL)
                        }

                        TaskStatus.ERROR -> {
                            AppLog.LOGI("Pre hook return ERROR")
                            return TaskResult.ResultCode(TaskStatus.ERROR)
                        }

                        else -> Unit
                    }
                }
            }
        }

        // Execute main task
        // Handle jump if needed
        when (val taskResult = currentTask.runTask(context, dependencies)) {
            is TaskResult.NextWorkflowItem -> {
                if (!setNextProcessingItem(taskResult.nextItem)) {
                    AppLog.LOGI("*** ${currentTask.taskName} jump outside workflow to: ${taskResult.nextItem.simpleName}")
                    return taskResult
                }
                AppLog.LOGI("*** ${currentTask.taskName} jump to: ${taskResult.nextItem.simpleName}")
                return TaskResult.ResultCode(TaskStatus.OK)
            }

            is TaskResult.ResultCode -> {
                when (taskResult.status) {
                    TaskStatus.CANCEL -> {
                        AppLog.LOGI("Pre hook return CANCEL")
                        return TaskResult.ResultCode(TaskStatus.CANCEL)
                    }

                    TaskStatus.ERROR -> {
                        AppLog.LOGI("Pre hook return ERROR")
                        return TaskResult.ResultCode(TaskStatus.ERROR)
                    }

                    else -> Unit
                }
            }
        }

        // Execute post-hooks if any
        if (currentTask.hasPostHooks()) {
            AppLog.LOGI("Execute post hooks")
            when (val hookResult = executeHooks(context, dependencies, currentTask.postHooks)) {
                is TaskResult.NextWorkflowItem -> {
                    if (!setNextProcessingItem(hookResult.nextItem)) {
                        AppLog.LOGI("*** Post hook jump outside workflow to: ${hookResult.nextItem.simpleName}")
                        return hookResult
                    }
                    AppLog.LOGI("*** Post hook jump to: ${hookResult.nextItem.simpleName}")
                    return TaskResult.ResultCode(TaskStatus.OK)
                }

                is TaskResult.ResultCode -> {
                    when (hookResult.status) {
                        TaskStatus.CANCEL -> {
                            AppLog.LOGI("Post hook return CANCEL")
                            return TaskResult.ResultCode(TaskStatus.CANCEL)
                        }

                        TaskStatus.ERROR -> {
                            AppLog.LOGI("Post hook return ERROR")
                            return TaskResult.ResultCode(TaskStatus.ERROR)
                        }

                        else -> Unit
                    }
                }
            }
        }

        currentTaskIndex++

        return TaskResult.ResultCode(TaskStatus.OK)
    }

    /**
     * Execute hooks for the task
     */
    private suspend fun executeHooks(
        context: WorkflowContext,
        dependencies: Dependencies,
        hooksList: List<Task>?
    ): TaskResult = coroutineScope {
        hooksList?.forEach { hook ->
            val hookResult = async {
                hook.runTask(context, dependencies)
            }.await()

            when (hookResult) {
                is TaskResult.NextWorkflowItem -> return@coroutineScope hookResult
                is TaskResult.ResultCode -> {
                    when (hookResult.status) {
                        TaskStatus.OK -> Unit
                        else -> return@coroutineScope hookResult
                    }
                }
            }
        }
        return@coroutineScope TaskResult.ResultCode(TaskStatus.OK)
    }

    /**
     * Set the next processing task in the flow.
     */
    private fun setNextProcessingItem(nextItem: KClass<*>): Boolean {
        val pos = jumpMap[nextItem]
        return if (pos != null) {
            currentTaskIndex = pos
            true
        } else {
            AppLog.LOGI("Invalid attempt to jump to: ${nextItem.simpleName}")
            false
        }
    }
}
