package com.flatpay.common.database

import android.content.Context

abstract class AppDatabase {

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            return instance
        }

        private fun buildDatabase(context: Context): AppDatabase? {
            //build database
            return null
        }
    }
}