package com.flatpay.pay_app.tasks

import com.flatpay.common.workflows.DBContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.log.AppLog

class MyTaskPostHook : Task() {
    override suspend fun execute(
        context: DBContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyTaskPostHook: execute()")
        return TaskResult()
    }
}
