package com.flatpay.common.database

import android.content.Context
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.flatpay.common.PaymentCoreDatabase
import com.flatpay.common.Txn

class DatabaseModule(context: Context) {
    val database = PaymentCoreDatabase(
        driver = AndroidSqliteDriver(PaymentCoreDatabase.Schema, context, "payment.db"),
        txnAdapter = Txn.Adapter(
            transactionStatusAdapter = EnumColumnAdapter(),
            paymentTechnologyAdapter = EnumColumnAdapter()
        )
    )
}