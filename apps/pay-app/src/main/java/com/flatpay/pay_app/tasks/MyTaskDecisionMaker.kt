package com.flatpay.pay_app.tasks

import com.flatpay.common.workflows.DBContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.log.AppLog

var counter = 0

class MyTaskDecisionMaker : Task() {
    override suspend fun execute(
        context: DBContext,
        dependencies: Dependencies
    ): TaskResult {
        AppLog.LOGI("MyTaskDecisionMaker: execute()")

        counter++
        AppLog.LOGI("MyTaskDecisionMaker: counter: $counter")

        if (counter == 1) //Jump to MyTask
            return TaskResult(MyTask::class.simpleName)
        else if (counter == 2)
            return TaskResult()
        else
            return TaskResult(TaskResult.ResultCodes.CANCEL)
    }
}
