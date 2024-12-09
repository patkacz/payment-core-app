package com.flatpay.common.workflows

import android.content.Context
import com.flatpay.common.core.model.Dependencies
import com.flatpay.common.database.WorkflowContext
import com.flatpay.log.AppLog

class ProcessingTask : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("ProcessingTask: execute()")
        // Delay for 5 seconds
        kotlinx.coroutines.delay(5000)
        // show processing screen
        return TaskResult.ResultCode(TaskStatus.OK)
    }
}
