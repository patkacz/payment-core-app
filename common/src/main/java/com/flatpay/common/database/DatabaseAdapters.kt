package com.flatpay.common.database

import app.cash.sqldelight.ColumnAdapter
import java.math.BigDecimal


object BigDecimalAdapter : ColumnAdapter<BigDecimal, String> {
    override fun decode(databaseValue: String): BigDecimal {
        return BigDecimal(databaseValue)
    }

    override fun encode(value: BigDecimal): String {
        return value.toPlainString()
    }
}