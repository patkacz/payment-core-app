package com.flatpay.pay_app.tasks

import com.flatpay.common.core.model.Dependencies
import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.common.workflows.TaskStatus
import com.flatpay.log.AppLog

class MyOtherTask : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyOtherTask: execute()")
        return TaskResult.ResultCode(TaskStatus.OK)
    }
}
