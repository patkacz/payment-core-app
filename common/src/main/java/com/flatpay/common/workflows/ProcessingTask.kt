package com.flatpay.common.workflows;

import com.flatpay.log.AppLog

public class ProcessingTask : Task() {
    override suspend fun execute(
            context: DBContext,
            dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("!!!!!!!!!!!!!!!ProcessingTask: execute()")
        dependencies.retrieveCurrentViewModel().navigateToNextScreen()
        // Delay for 5 seconds
        kotlinx.coroutines.delay(5000)
       // dependencies.retrieveCurrentViewModel().hideProcessingScreen()

        return TaskResult(TaskResult.ResultCodes.OK)
    }
}

