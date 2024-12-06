package com.flatpay.pay_app.tasks

import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.log.AppLog

class MyTaskInsert : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyTaskInsert: execute()")
        return TaskResult()
    }
}
