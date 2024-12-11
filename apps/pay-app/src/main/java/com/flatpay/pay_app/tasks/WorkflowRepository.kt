package com.flatpay.pay_app.repositories

import com.flatpay.common.core.model.Dependencies
import com.flatpay.common.database.WorkflowContext
import com.flatpay.pay_app.tasks.MyProcessingTask
import com.flatpay.common.workflows.Workflow
import com.flatpay.log.AppLog
import com.flatpay.pay_app.tasks.MyOtherTask
import com.flatpay.pay_app.tasks.MyTask
import com.flatpay.pay_app.tasks.MyTaskDecisionMaker
import com.flatpay.pay_app.tasks.MyTaskInsert
import com.flatpay.pay_app.tasks.MyTaskPostHook
import com.flatpay.pay_app.tasks.MyTaskPreHook
import kotlinx.coroutines.runBlocking


class WorkflowRepository {
    fun runWorkflow(context: WorkflowContext, dependencies: Dependencies) {
        AppLog.LOGI("MyWorkflow start")
        runBlocking {
            val workflow = Workflow()
            workflow.addItem<MyTask>()
                .addItem<MyTaskDecisionMaker>()
                .addItem<MyProcessingTask>()
                .addItem<MyOtherTask>()

            workflow.insertBefore<MyTaskInsert, MyTask>()

            workflow.getItem<MyTask>().addPreHook<MyTaskPreHook>()
            workflow.getItem<MyTask>().addPostHook<MyTaskPostHook>()

            val result =
                workflow.execute(context, dependencies)

            AppLog.LOGI("MyWorkflow completed with result: $result")
        }
    }
    fun runWorkflowSettings() {
        AppLog.LOGI("Settings start")
    }
}
