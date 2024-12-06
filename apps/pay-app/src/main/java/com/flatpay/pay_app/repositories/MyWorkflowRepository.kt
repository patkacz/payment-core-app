package com.flatpay.pay_app.repositories

import com.flatpay.common.workflows.DBContext
import com.flatpay.common.workflows.Dependencies
import com.flatpay.common.workflows.Workflow
import com.flatpay.log.AppLog
import com.flatpay.pay_app.tasks.MyOtherTask
import com.flatpay.pay_app.tasks.MyTask
import com.flatpay.pay_app.tasks.MyTaskDecisionMaker
import com.flatpay.pay_app.tasks.MyTaskInsert
import com.flatpay.pay_app.tasks.MyTaskPostHook
import com.flatpay.pay_app.tasks.MyTaskPreHook
import kotlinx.coroutines.runBlocking


interface MyWorkflowRepository {
    fun runWorkflow() {
        AppLog.LOGI("MyWorkflow start")
        runBlocking {
            val context = DBContext()  // Replace with actual context
            val dependencies = Dependencies()  // Replace with actual dependencies

            val workflow = Workflow()
            workflow.addItem<MyTask>()
                .addItem<MyTaskDecisionMaker>()
                .addItem<MyOtherTask>()

            workflow.insertBefore<MyTaskInsert, MyTask>()

            workflow.getItem<MyTask>().addPreHook<MyTaskPreHook>()
            workflow.getItem<MyTask>().addPostHook<MyTaskPostHook>()

            val result =
                workflow.execute(context, dependencies)

            AppLog.LOGI("MyWorkflow completed with result: $result")
        }
    }
}
