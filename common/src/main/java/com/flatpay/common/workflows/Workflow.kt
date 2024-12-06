package com.flatpay.common.workflows

import com.flatpay.log.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class Workflow() : Task() {

    private var nextItem: Int = 0
    private var lastTaskResult: TaskResult = TaskResult(TaskResult.ResultCodes.OK)
    val workflowItems: MutableList<Task> = mutableListOf()
    val jumpMap: MutableMap<String, Int> = mutableMapOf()

    /**
     * Add a task to the workflow.
     */
    suspend inline fun <reified ItemType : Task> addItem(): Workflow {
        val task = ItemType::class.java.getDeclaredConstructor().newInstance()
        workflowItems.add(task)
        jumpMap[task.taskName] = workflowItems.size - 1
        return this
    }

    /**
     * Retrieve a specific task from the workflow.
     */
    inline fun <reified ItemType : Task> getItem(): ItemType {
        val item = ItemType::class.java.getDeclaredConstructor().newInstance()
        return workflowItems[jumpMap[item.taskName]!!] as ItemType //TODO: change !! to something safer
    }

    /**
     * Insert a task before a specific task.
     */
    inline fun <reified InsertItemType : Task, reified BeforeItemType : Task> insertBefore(): Workflow {
        val beforeTaskName = BeforeItemType::class.simpleName?: "Unknown?"
        val pos = jumpMap[beforeTaskName]
            ?: throw IllegalArgumentException("Inserting task before non-existent task")
        return insert<InsertItemType>(pos)
    }

    /**
     * Insert a task after a specific task.
     */
    inline fun <reified InsertItemType : Task, reified AfterItemType : Task> insertAfter(): Workflow {
        val afterTaskName = AfterItemType::class.simpleName?: "Unknown?"
        val pos = jumpMap[afterTaskName]
            ?: throw IllegalArgumentException("Inserting task after non-existent task")
        return insert<InsertItemType>(pos + 1)
    }

    /**
     * Insert a task at a specific position.
     */
    inline fun <reified ItemType : Task> insert(pos: Int): Workflow {
        val task = ItemType::class.java.getDeclaredConstructor().newInstance()
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

        while (nextItem < workflowItems.size) {
            executeNextTask(context, dependencies)
            workflowResult = this@Workflow.lastTaskResult

            if (workflowResult.retCode == TaskResult.ResultCodes.ERROR) {
                //TODO: New Relic integration
                break
            }

            if (workflowResult.retCode == TaskResult.ResultCodes.CANCEL) {
                break
            }
        }

        nextItem = 0
        workflowResult
    }

    private suspend fun executeNextTask(context: DBContext, dependencies: Dependencies) {
        val currentTask = workflowItems[nextItem].taskName
        AppLog.LOGI("Execute task $currentTask")

        if (workflowItems[nextItem].hasPreHooks()) {
            AppLog.LOGI("Execute pre hooks")
            val hookResult = executeHooks(context, dependencies, workflowItems[nextItem].preHooks)
            if (hookResult == TaskResult(TaskResult.ResultCodes.CANCEL)) {
                AppLog.LOGI("Pre hook return CANCEL")
                lastTaskResult = hookResult
                return
            }
            if (hookResult.isJumping()) {
                AppLog.LOGI("*** Pre hook jump to: $hookResult.getNextItem()")
                if (!setNextProcessingItem(hookResult.nextWorkflowItem ?: "")) {
                    lastTaskResult = TaskResult(TaskResult.ResultCodes.CANCEL)
                    return
                }
                AppLog.LOGI("*** Pre hook jump to: $nextItem")
                return
            }
        }

        val task = workflowItems[nextItem]

        lastTaskResult = task.runTask(context, dependencies, lastTaskResult)

        if (lastTaskResult.isJumping()) {
            setNextProcessingItem(lastTaskResult.nextWorkflowItem ?: "")
            return
        }


        if (workflowItems[nextItem].hasPostHooks()) {
            AppLog.LOGI("Execute post hooks")
            val hookResult = executeHooks(context, dependencies, workflowItems[nextItem].postHooks)
            if (hookResult == TaskResult(TaskResult.ResultCodes.CANCEL)) {
                AppLog.LOGI("Post hook return CANCEL")
                lastTaskResult = hookResult
                return
            }
            if (hookResult.isJumping()) {
                AppLog.LOGI("*** Post hook jump to: $hookResult.getNextItem()")
                if (!setNextProcessingItem(hookResult.nextWorkflowItem ?: "Unknown!")) {
                    lastTaskResult = TaskResult(TaskResult.ResultCodes.CANCEL)
                    return
                }
                AppLog.LOGI("*** Post hook jump to: $nextItem")
                return
            }
        }

        nextItem++
    }

    /**
     * Execute hooks for the workflow asynchronously.
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

            if (result.isJumping() || result.retCode == TaskResult.ResultCodes.CANCEL) {
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
            nextItem = pos
            true
        } else {
            false
        }
    }
}
