package com.flatpay.common.data.database

import android.content.Context
import com.flatpay.common.database.AppDatabase

object DatabaseHandler {

    private lateinit var database: AppDatabase
    fun initialize(context: Context) {
        // database = AppDatabase()
    }

    fun getDatabase(): AppDatabase = database
}