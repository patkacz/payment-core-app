package com.flatpay.pay_app.repositories

import android.app.Activity
import com.flatpay.common.workflows.DBContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.ProcessingTask
import com.flatpay.common.workflows.Workflow
import com.flatpay.log.AppLog
import com.flatpay.pay_app.tasks.MyOtherTask
import com.flatpay.pay_app.tasks.MyTask
import com.flatpay.pay_app.tasks.MyTaskDecisionMaker
import com.flatpay.pay_app.tasks.MyTaskInsert
import com.flatpay.pay_app.tasks.MyTaskPostHook
import com.flatpay.pay_app.tasks.MyTaskPreHook

import kotlinx.coroutines.runBlocking


class MyWorkflowRepository {
    fun runWorkflow(dependencies: Dependencies) {
        AppLog.LOGI("MyWorkflow start1")
        runBlocking {
            val context = DBContext()  // Replace with actual context
           // val dependencies = Dependencies()  // Replace with actual dependencies


            val workflow = Workflow()
            workflow.addItem<MyTask>()
                .addItem<MyTaskDecisionMaker>()
                .addItem<ProcessingTask>()
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
