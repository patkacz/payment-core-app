package com.flatpay.pay_app.tasks

import com.flatpay.common.TransactionQueries
import com.flatpay.common.Txn
import com.flatpay.common.core.model.Dependencies
import com.flatpay.common.database.WorkflowContext
import com.flatpay.common.database.TransactionStatus
import com.flatpay.common.workflows.Task
import com.flatpay.common.workflows.TaskResult
import com.flatpay.common.workflows.TaskStatus
import com.flatpay.log.AppLog

class MyTask : Task() {
    override suspend fun execute(
        context: WorkflowContext,
        dependencies: Dependencies
    ): TaskResult {

        AppLog.LOGI("MyTask: execute()")

        var txn = context.get<Txn>()

        AppLog.LOGI("MyTask 1: txnUUID = ${txn?.uuid}, txn.status = ${txn?.transactionStatus}")

        txn = context.set<Txn>(txn?.copy(transactionStatus = TransactionStatus.APPROVED)!!)

        AppLog.LOGI("MyTask 2: txnUUID = ${txn?.uuid}, txn.status = ${txn?.transactionStatus}")

        //txn = context.database.transactionQueries.get(txn.id)
        val allTxn = context.query<Txn, TransactionQueries>()?.selectAll()?.executeAsList()

        AppLog.LOGI("MyTask 3: allTxn: $allTxn")

        context.save<Txn>()

        return TaskResult.ResultCode(TaskStatus.OK)
    }
}
