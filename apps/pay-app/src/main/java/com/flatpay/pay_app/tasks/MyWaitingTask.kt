package com.flatpay.pay_app.tasks

import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.common.workflows.TaskStatus
import com.flatpay.log.AppLog
import com.flatpay.common.core.model.Dependencies
import kotlinx.coroutines.delay

class MyWaitingTask : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyOtherTask: execute()")
        delay(5000)
        return TaskResult.ResultCode(TaskStatus.OK)
    }
}
