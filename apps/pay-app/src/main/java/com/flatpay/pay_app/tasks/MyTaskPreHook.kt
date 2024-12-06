package com.flatpay.pay_app.tasks

import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.log.AppLog

class MyTaskPreHook : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyTaskPreHook: execute()")
        return TaskResult()
    }
}
